package com.frontline.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/** Зенитная управляемая ракета: наводится на цель с упреждением, подрывается вблизи. */
public class InterceptorEntity extends Entity {
    private static final double MAX_SPEED = 3.4;
    private static final int MAX_AGE = 240;

    private int targetId = -1;
    private int age;
    private double speed = 0.6;
    private double lastDist = Double.MAX_VALUE;

    public InterceptorEntity(EntityType<? extends InterceptorEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public int getTargetId() {
        return targetId;
    }

    public void launch(Vec3 start, Entity target) {
        setPos(start.x, start.y, start.z);
        this.targetId = target.getId();
        setDeltaMovement(0.0, 0.6, 0.0);
        setXRot(90.0F);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            spawnExhaust();
            return;
        }

        age++;
        Entity target = level().getEntity(targetId);
        if (age > MAX_AGE || target == null || !target.isAlive()) {
            fizzle();
            return;
        }

        speed = Math.min(MAX_SPEED, speed + 0.1);

        Vec3 aimPoint = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0);
        Vec3 targetVel = new Vec3(target.getX() - target.xo, target.getY() - target.yo, target.getZ() - target.zo);
        double dist = aimPoint.subtract(position()).length();
        double timeToGo = Math.min(dist / speed, 30.0);
        Vec3 lead = aimPoint.add(targetVel.scale(timeToGo)).subtract(position());

        Vec3 desired = lead.lengthSqr() < 1.0E-6 ? getDeltaMovement() : lead.normalize().scale(speed);
        Vec3 v = getDeltaMovement().lerp(desired, 0.35);
        if (v.lengthSqr() > 1.0E-6) {
            v = v.normalize().scale(speed);
        }
        setDeltaMovement(v);

        Vec3 prev = position();
        Vec3 next = prev.add(v);
        BlockHitResult hit = level().clip(new ClipContext(prev, next,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hit.getType() != HitResult.Type.MISS) {
            blast(hit.getLocation(), null);
            return;
        }

        setPos(next);
        orient(v);

        double now = next.distanceTo(target.position().add(0.0, target.getBbHeight() * 0.5, 0.0));
        if (now < 3.5 || (lastDist < 12.0 && now > lastDist)) {
            blast(next, target);
            return;
        }
        lastDist = now;
    }

    private void blast(Vec3 at, @Nullable Entity target) {
        level().explode(this, at.x, at.y, at.z, 2.5F, Level.ExplosionInteraction.NONE);
        if (target != null && target.isAlive()) {
            target.hurt(level().damageSources().explosion(this, null), 1000.0F);
        }
        discard();
    }

    private void fizzle() {
        level().explode(this, getX(), getY(), getZ(), 1.0F, Level.ExplosionInteraction.NONE);
        discard();
    }

    private void orient(Vec3 v) {
        double horiz = Math.sqrt(v.x * v.x + v.z * v.z);
        if (horiz > 1.0E-4) {
            setYRot((float) (Mth.atan2(v.x, v.z) * (180.0 / Math.PI)));
        }
        setXRot((float) (Mth.atan2(v.y, horiz) * (180.0 / Math.PI)));
    }

    private void spawnExhaust() {
        FlightFx.exhaustTrail(this, ParticleTypes.FLAME, 1.0, 2);
        FlightFx.exhaustTrail(this, ParticleTypes.CLOUD, 1.0, 3);
        FlightFx.engineSound(this, SoundEvents.FIREWORK_ROCKET_TWINKLE, 0.5F, 1.3F, 6);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // после перезагрузки мира цель потеряна — ракета самоуничтожится
        targetId = -1;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
}
