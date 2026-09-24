package com.bcw.mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

/**
 * Common flight, targeting, penetration and cleanup logic shared by every missile.
 * Concrete missiles (ZirconEntity, TomahawkEntity, ...) only need to supply their
 * {@link MissileType} profile, explosion power, and any launch-specific behaviour.
 */
public abstract class AbstractMissileEntity extends Entity {

    private static final EntityDataAccessor<Integer> LAYERS_PENETRATED =
            SynchedEntityData.defineId(AbstractMissileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IGNITED =
            SynchedEntityData.defineId(AbstractMissileEntity.class, EntityDataSerializers.BOOLEAN);

    protected final MissileType missileType;
    protected ChunkLoaderManager chunkLoader;

    private double targetX, targetY, targetZ;
    private boolean hasTarget = false;
    private int ticksAlive = 0;
    private int maxPenetrations = 10;

    protected AbstractMissileEntity(EntityType<?> type, Level level, MissileType missileType) {
        super(type, level);
        this.missileType = missileType;
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LAYERS_PENETRATED, 0);
        this.entityData.define(IGNITED, false);
    }

    public void setTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        this.hasTarget = true;
    }

    /** Maximum number of solid blocks (e.g. concrete/obsidian layers) the warhead can punch through. */
    public void setMaxPenetrations(int layers) {
        this.maxPenetrations = layers;
    }

    @Override
    public void tick() {
        super.tick();
        ticksAlive++;

        if (this.level() instanceof ServerLevel serverLevel) {
            if (chunkLoader == null) {
                chunkLoader = new ChunkLoaderManager(this.getUUID());
            }
            chunkLoader.updateAround(serverLevel, ChunkLoaderManager.chunkOf(getX(), getZ()));
        }

        Vec3 velocity = computeVelocity();
        this.setDeltaMovement(velocity);
        this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
        orientToVelocity(velocity);

        if (checkImpact()) {
            detonate();
        }

        if (ticksAlive > 20 * 600) {
            // Safety valve: 10 minutes of flight with no impact means something went wrong.
            discard();
        }
    }

    /**
     * Computes this tick's velocity vector. Cruise/aeroballistic/hypersonic missiles fly a
     * level or shallow arc toward the target; ballistic/MIRV missiles climb to apex altitude
     * first, then fall along a parabolic arc, mirroring real flight profiles.
     */
    protected Vec3 computeVelocity() {
        if (!hasTarget) {
            return this.getDeltaMovement();
        }

        double dx = targetX - getX();
        double dy = targetY - getY();
        double dz = targetZ - getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
        double speed = missileType.getTopSpeed();

        if (missileType.hasBallisticArc()) {
            double apex = missileType.getApexAltitude();
            if (getY() < apex - 2.0 && horizontalDist > speed * 4) {
                // Boost phase: climb almost straight up while drifting toward target.
                Vec3 dir = new Vec3(dx, apex - getY(), dz).normalize();
                return dir.scale(speed);
            } else {
                // Midcourse/terminal: fall in a straight line toward the target from apex.
                Vec3 dir = new Vec3(dx, dy, dz).normalize();
                return dir.scale(speed);
            }
        } else {
            // Cruise / hypersonic-glide / aeroballistic: direct vector toward target.
            Vec3 dir = new Vec3(dx, dy, dz).normalize();
            return dir.scale(speed);
        }
    }

    protected void orientToVelocity(Vec3 velocity) {
        if (velocity.lengthSqr() < 1.0E-7) return;
        double horizontalDist = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        this.setYRot((float) (Math.toDegrees(Math.atan2(velocity.x, velocity.z))));
        this.setXRot((float) (Math.toDegrees(-Math.atan2(velocity.y, horizontalDist))));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    /** Returns true once the missile has reached/passed its target or hit a solid block it cannot punch through. */
    protected boolean checkImpact() {
        if (!hasTarget) return false;

        double distSqr = this.position().distanceToSqr(targetX, targetY, targetZ);
        if (distSqr < 2.25) {
            return true;
        }

        BlockPos here = this.blockPosition();
        if (!this.level().getBlockState(here).isAir() && !this.level().getBlockState(here).liquid()) {
            int penetrated = this.entityData.get(LAYERS_PENETRATED);
            if (penetrated >= maxPenetrations) {
                return true;
            }
            this.entityData.set(LAYERS_PENETRATED, penetrated + 1);
        }
        return false;
    }

    protected void detonate() {
        if (this.level() instanceof ServerLevel serverLevel) {
            onDetonate(serverLevel);
            if (chunkLoader != null) {
                chunkLoader.release(serverLevel);
            }
        }
        this.discard();
    }

    /** Subclasses implement their specific warhead effect(s) — single blast, MIRV split, BROACH double-charge, etc. */
    protected abstract void onDetonate(ServerLevel level);

    protected void explodeAt(ServerLevel level, double x, double y, double z, float power) {
        level.explode(this, x, y, z, power, Level.ExplosionInteraction.MOB);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (this.level() instanceof ServerLevel serverLevel && chunkLoader != null && !chunkLoader.isReleased()) {
            chunkLoader.release(serverLevel);
        }
        super.remove(reason);
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.hasTarget = tag.getBoolean("HasTarget");
        this.targetX = tag.getDouble("TargetX");
        this.targetY = tag.getDouble("TargetY");
        this.targetZ = tag.getDouble("TargetZ");
        this.maxPenetrations = tag.getInt("MaxPenetrations");
        this.ticksAlive = tag.getInt("TicksAlive");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("HasTarget", hasTarget);
        tag.putDouble("TargetX", targetX);
        tag.putDouble("TargetY", targetY);
        tag.putDouble("TargetZ", targetZ);
        tag.putInt("MaxPenetrations", maxPenetrations);
        tag.putInt("TicksAlive", ticksAlive);
    }

    public MissileType getMissileType() {
        return missileType;
    }

    protected boolean hasTarget() {
        return hasTarget;
    }

    protected double getTargetX() {
        return targetX;
    }

    protected double getTargetY() {
        return targetY;
    }

    protected double getTargetZ() {
        return targetZ;
    }
}
