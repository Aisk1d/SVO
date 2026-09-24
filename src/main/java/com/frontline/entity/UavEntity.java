package com.frontline.entity;

import com.frontline.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Беспилотник самолётного типа.
 *  - разведывательный: кружит над точкой и подсвечивает враждебных мобов вокруг;
 *  - ударный (барражирующий боеприпас): летит на цель на высоте и пикирует.
 */
public class UavEntity extends AerialEntity {
    private static final double RECON_SPEED = 1.0;
    private static final double STRIKE_SPEED = 1.3;
    private static final double ORBIT_RADIUS = 48.0;
    private static final int RECON_LIFETIME = 12000;

    private final boolean strike;
    private Vec3 target = Vec3.ZERO;
    private double cruiseY;

    public UavEntity(EntityType<? extends UavEntity> type, Level level, boolean strike) {
        super(type, level);
        this.strike = strike;
    }

    @Override
    protected float maxHealth() {
        return 20.0F;
    }

    public void launchRecon(Vec3 start, Vec3 center, Vec3 heading, @Nullable UUID owner) {
        this.target = center;
        this.cruiseY = Math.max(start.y, center.y) + 55.0;
        this.ownerUuid = owner;
        setPos(start.x, start.y, start.z);
        setDeltaMovement(new Vec3(heading.x, 0.1, heading.z).normalize().scale(RECON_SPEED));
    }

    public void launchStrike(Vec3 start, Vec3 target, Vec3 heading, @Nullable UUID owner) {
        this.target = target;
        this.cruiseY = Math.max(start.y, target.y) + 45.0;
        this.ownerUuid = owner;
        setPos(start.x, start.y, start.z);
        setDeltaMovement(new Vec3(heading.x, 0.1, heading.z).normalize().scale(STRIKE_SPEED));
    }

    @Override
    protected void serverTick(ServerLevel level) {
        Vec3 pos = position();
        Vec3 aim;
        boolean diving = false;
        double speed = strike ? STRIKE_SPEED : RECON_SPEED;

        if (strike) {
            double dx = target.x - pos.x;
            double dz = target.z - pos.z;
            double horiz = Math.sqrt(dx * dx + dz * dz);
            if (horiz > 70.0) {
                aim = new Vec3(target.x, cruiseY, target.z);
            } else {
                aim = target;
                diving = true;
            }
        } else {
            if (flightTicks > RECON_LIFETIME) {
                crash();
                return;
            }
            double dx = pos.x - target.x;
            double dz = pos.z - target.z;
            double r = Math.sqrt(dx * dx + dz * dz);
            if (r > ORBIT_RADIUS + 40.0) {
                aim = new Vec3(target.x, cruiseY, target.z);
            } else {
                double a = Math.atan2(dz, dx) + 0.35;
                aim = new Vec3(target.x + ORBIT_RADIUS * Math.cos(a), cruiseY, target.z + ORBIT_RADIUS * Math.sin(a));
            }
            if (flightTicks % 20 == 0) {
                markEnemies(level);
            }
        }

        Vec3 toAim = aim.subtract(pos);
        Vec3 desired = toAim.lengthSqr() < 1.0E-6 ? getDeltaMovement() : toAim.normalize().scale(speed);
        if (!diving) {
            desired = new Vec3(desired.x, Mth.clamp(desired.y, -0.25, 0.3), desired.z);
        }

        Vec3 v = getDeltaMovement().lerp(desired, strike ? 0.08 : 0.06);
        setDeltaMovement(v);
        boolean hit = flyBy(v);
        orient(v);
        keepChunksLoaded(level, position().add(v.scale(25.0)));

        if (strike) {
            if (hit || position().distanceToSqr(target) < 3.0 * 3.0) {
                detonate(6.0F);
            }
        } else if (hit) {
            crash();
        }
    }

    /** Разведка: враждебные мобы вокруг светятся. */
    private void markEnemies(ServerLevel level) {
        for (Monster m : level.getEntitiesOfClass(Monster.class, getBoundingBox().inflate(64.0, 90.0, 64.0))) {
            m.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false));
        }
    }

    @Override
    protected void clientTick() {
        if (tickCount % 2 != 0) return;
        Vec3 v = position().subtract(xo, yo, zo);
        if (v.lengthSqr() < 1.0E-6) return;
        Vec3 tail = position().subtract(v.normalize().scale(1.6));
        level().addParticle(ParticleTypes.SMOKE, tail.x, tail.y, tail.z, 0.0, 0.0, 0.0);

        boolean geran = getType() == ModEntities.UAV_GERAN.get();
        SoundEvent sound = geran ? SoundEvents.CONDUIT_AMBIENT : SoundEvents.BEACON_AMBIENT;
        float volume = geran ? 0.55F : 0.3F;
        float pitch = geran ? 0.5F : (strike ? 1.1F : 1.3F); // "Герань" — низкий треск мопед-мотора, разведчик — выше и тише
        FlightFx.engineSound(this, sound, volume, pitch, geran ? 14 : 10);
    }

    @Override
    protected void writeExtra(CompoundTag tag) {
        tag.putDouble("TX", target.x);
        tag.putDouble("TY", target.y);
        tag.putDouble("TZ", target.z);
        tag.putDouble("Cruise", cruiseY);
    }

    @Override
    protected void readExtra(CompoundTag tag) {
        target = new Vec3(tag.getDouble("TX"), tag.getDouble("TY"), tag.getDouble("TZ"));
        cruiseY = tag.getDouble("Cruise");
    }
}
