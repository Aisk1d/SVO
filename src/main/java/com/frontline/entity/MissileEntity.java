package com.frontline.entity;

import com.frontline.missile.BallisticTrajectory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class MissileEntity extends Entity implements Trackable {
    private static final float DEFAULT_EXPLOSION_POWER = 8.0F;
    private static final int LOOKAHEAD_TICKS = 30;
    private static final TicketType<Long> CHUNK_TICKET =
            TicketType.create("frontline_missile", Long::compare, 100);

    private Vec3 launchPos = Vec3.ZERO;
    private Vec3 targetPos = Vec3.ZERO;
    private BallisticTrajectory trajectory;
    private BallisticTrajectory.Profile profile = BallisticTrajectory.Profile.HIGH_ARC;
    private float explosionPower = DEFAULT_EXPLOSION_POWER;
    private int flightTick;
    @Nullable
    private UUID ownerUuid;

    public MissileEntity(EntityType<? extends MissileEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public void launch(Vec3 start, Vec3 target) {
        launch(start, target, BallisticTrajectory.Profile.HIGH_ARC, DEFAULT_EXPLOSION_POWER);
    }

    /** Запуск с указанием игрового профиля полёта и мощности взрыва (используется для разных типов ракет). */
    public void launch(Vec3 start, Vec3 target, BallisticTrajectory.Profile profile, float explosionPower) {
        this.launchPos = start;
        this.targetPos = target;
        this.profile = profile;
        this.explosionPower = explosionPower;
        this.trajectory = BallisticTrajectory.plan(start, target, profile);
        this.flightTick = 0;
        this.setPos(start);
        this.setXRot(90.0F);
    }

    public void setOwner(@Nullable UUID owner) {
        this.ownerUuid = owner;
    }

    @Nullable
    @Override
    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    /** Ракету можно сбить только перехватчиком ПВО. */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide || isRemoved()) return false;
        if (source.getDirectEntity() instanceof InterceptorEntity) {
            level().explode(this, getX(), getY(), getZ(), 2.0F, Level.ExplosionInteraction.NONE);
            discard();
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            spawnExhaust();
            return;
        }
        if (trajectory == null || !(level() instanceof ServerLevel serverLevel)) {
            discard();
            return;
        }

        keepChunksLoaded(serverLevel);

        Vec3 prev = position();
        flightTick++;
        Vec3 next = trajectory.positionAt(flightTick);

        // столкновение с блоками на пути за этот тик
        BlockHitResult hit = level().clip(new ClipContext(prev, next,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hit.getType() != HitResult.Type.MISS) {
            detonate(hit.getLocation());
            return;
        }

        setPos(next);
        orient(next.subtract(prev));

        if (flightTick >= trajectory.totalTicks()) {
            detonate(next);
        }
    }

    /** Держим загруженными чанки вокруг ракеты и чуть впереди неё по курсу. */
    private void keepChunksLoaded(ServerLevel level) {
        long id = getId();
        ChunkPos here = new ChunkPos(BlockPos.containing(position()));
        level.getChunkSource().addRegionTicket(CHUNK_TICKET, here, 2, id);

        Vec3 aheadPos = trajectory.positionAt(flightTick + LOOKAHEAD_TICKS);
        ChunkPos ahead = new ChunkPos(BlockPos.containing(aheadPos));
        level.getChunkSource().addRegionTicket(CHUNK_TICKET, ahead, 2, id);
    }

    private void orient(Vec3 v) {
        double horiz = Math.sqrt(v.x * v.x + v.z * v.z);
        if (horiz > 1.0E-4) {
            setYRot((float) (Mth.atan2(v.x, v.z) * (180.0 / Math.PI)));
        }
        if (horiz > 1.0E-4 || Math.abs(v.y) > 1.0E-4) {
            setXRot((float) (Mth.atan2(v.y, horiz) * (180.0 / Math.PI)));
        }
    }

    private void detonate(Vec3 at) {
        level().explode(this, at.x, at.y, at.z, explosionPower, Level.ExplosionInteraction.TNT);
        discard();
    }

    private void spawnExhaust() {
        // дозировано: пламя раз в 2 тика, дым раз в 4 — почти та же картинка, но заметно дешевле для FPS
        FlightFx.exhaustTrail(this, ParticleTypes.FLAME, 1.3, 2);
        FlightFx.exhaustTrail(this, ParticleTypes.LARGE_SMOKE, 1.3, 4);
        FlightFx.engineSound(this, engineSound(), engineVolume(), enginePitch(), 8);
    }

    /** Звук двигателя зависит от игрового профиля полёта (см. BallisticTrajectory.Profile). */
    private SoundEvent engineSound() {
        return switch (profile) {
            case LOW_CRUISE -> SoundEvents.CONDUIT_AMBIENT;   // ровный гул турбины крылатой ракеты
            default -> SoundEvents.FIRE_AMBIENT;              // рёв твердотопливного двигателя
        };
    }

    private float engineVolume() {
        return switch (profile) {
            case STEEP_LOFTED -> 1.4F; // тяжёлая ракета — громче
            case LOW_CRUISE -> 1.0F;
            default -> 0.8F;
        };
    }

    private float enginePitch() {
        return switch (profile) {
            case STEEP_LOFTED -> 0.6F; // ниже тон — ощущение большой массы
            case LOW_CRUISE -> 0.85F;
            default -> 1.0F;
        };
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        launchPos = new Vec3(tag.getDouble("SX"), tag.getDouble("SY"), tag.getDouble("SZ"));
        targetPos = new Vec3(tag.getDouble("TX"), tag.getDouble("TY"), tag.getDouble("TZ"));
        flightTick = tag.getInt("Tick");
        ownerUuid = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        explosionPower = tag.contains("Power") ? tag.getFloat("Power") : DEFAULT_EXPLOSION_POWER;
        profile = BallisticTrajectory.Profile.values()[Mth.clamp(tag.getInt("Profile"), 0,
                BallisticTrajectory.Profile.values().length - 1)];
        trajectory = BallisticTrajectory.plan(launchPos, targetPos, profile);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putDouble("SX", launchPos.x);
        tag.putDouble("SY", launchPos.y);
        tag.putDouble("SZ", launchPos.z);
        tag.putDouble("TX", targetPos.x);
        tag.putDouble("TY", targetPos.y);
        tag.putDouble("TZ", targetPos.z);
        tag.putInt("Tick", flightTick);
        tag.putFloat("Power", explosionPower);
        tag.putInt("Profile", profile.ordinal());
        if (ownerUuid != null) {
            tag.putUUID("Owner", ownerUuid);
        }
    }
}
