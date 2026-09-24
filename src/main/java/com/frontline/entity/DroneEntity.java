package com.frontline.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

/** Квадрокоптер-камикадзе (FPV). STRIKE — летит на заданную точку, HUNT — охотится за ближайшим игроком. */
public class DroneEntity extends AerialEntity {
    public enum Mode { STRIKE, HUNT }

    private static final double SPEED = 0.85;
    private static final int BATTERY_TICKS = 4800;

    private Mode mode = Mode.STRIKE;
    private Vec3 target = Vec3.ZERO;
    private double cruiseY;

    public DroneEntity(EntityType<? extends DroneEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected float maxHealth() {
        return 6.0F;
    }

    public void launchStrike(Vec3 start, Vec3 target, @Nullable UUID owner) {
        this.mode = Mode.STRIKE;
        this.target = target;
        this.cruiseY = Math.max(start.y, target.y) + 18.0;
        this.ownerUuid = owner;
        setPos(start.x, start.y, start.z);
        setDeltaMovement(0.0, 0.3, 0.0);
    }

    public void launchHunt(Vec3 start) {
        this.mode = Mode.HUNT;
        this.cruiseY = start.y;
        setPos(start.x, start.y, start.z);
    }

    @Override
    protected void serverTick(ServerLevel level) {
        if (flightTicks > BATTERY_TICKS) {
            crash();
            return;
        }

        Vec3 aim = target;
        if (mode == Mode.HUNT) {
            Player player = level.getNearestPlayer(this, 64.0);
            if (player == null) {
                Vec3 slow = getDeltaMovement().scale(0.9);
                setDeltaMovement(slow);
                flyBy(slow);
                return;
            }
            aim = player.position().add(0.0, player.getBbHeight() * 0.6, 0.0);
            target = aim;
            cruiseY = aim.y + 8.0;
        }

        Vec3 pos = position();
        Vec3 toAim = aim.subtract(pos);
        double horiz = Math.sqrt(toAim.x * toAim.x + toAim.z * toAim.z);

        Vec3 desired;
        if (horiz > 16.0) {
            // крейсерский полёт на высоте
            double climb = Mth.clamp((cruiseY - pos.y) * 0.2, -0.4, 0.5);
            Vec3 h = new Vec3(toAim.x, 0.0, toAim.z).normalize().scale(SPEED);
            desired = new Vec3(h.x, climb, h.z);
        } else {
            // пикирование на цель
            desired = toAim.lengthSqr() < 1.0E-6 ? Vec3.ZERO : toAim.normalize().scale(SPEED * 1.15);
        }

        Vec3 v = getDeltaMovement().lerp(desired, 0.12);
        setDeltaMovement(v);
        boolean hit = flyBy(v);
        orientQuad(v);
        keepChunksLoaded(level, position().add(v.scale(20.0)));

        if (hit || position().distanceToSqr(aim) < 2.5 * 2.5) {
            detonate(2.5F);
        }
    }

    /** Квадрокоптер летит носом по курсу и наклоняется вперёд при разгоне. */
    private void orientQuad(Vec3 v) {
        double horiz = Math.sqrt(v.x * v.x + v.z * v.z);
        if (horiz > 0.05) {
            setYRot((float) (Mth.atan2(v.x, v.z) * (180.0 / Math.PI)));
        }
        setXRot((float) -Mth.clamp(horiz * 30.0, 0.0, 25.0));
    }

    @Override
    protected void clientTick() {
        // лёгкий шлейф пыли от винтов + высокий писк моторчиков, оба дозированы по тикам
        FlightFx.exhaustTrail(this, ParticleTypes.SMOKE, 0.5, 4);
        FlightFx.engineSound(this, SoundEvents.BEACON_AMBIENT, 0.3F, 1.8F, 10);
    }

    @Override
    protected void writeExtra(CompoundTag tag) {
        tag.putInt("Mode", mode.ordinal());
        tag.putDouble("TX", target.x);
        tag.putDouble("TY", target.y);
        tag.putDouble("TZ", target.z);
        tag.putDouble("Cruise", cruiseY);
    }

    @Override
    protected void readExtra(CompoundTag tag) {
        mode = tag.getInt("Mode") == 1 ? Mode.HUNT : Mode.STRIKE;
        target = new Vec3(tag.getDouble("TX"), tag.getDouble("TY"), tag.getDouble("TZ"));
        cruiseY = tag.getDouble("Cruise");
    }
}
