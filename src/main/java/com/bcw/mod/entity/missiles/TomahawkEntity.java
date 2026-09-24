package com.bcw.mod.entity.missiles;

import com.bcw.mod.entity.AbstractMissileEntity;
import com.bcw.mod.entity.MissileType;
import com.bcw.mod.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * BGM-109 Tomahawk — subsonic cruise missile that maintains a fixed cruise altitude
 * above terrain until it closes within terminal-dive range of its target, then dives in.
 * Coordinates come from GPS-style guidance set on launch via {@link #setTarget}.
 */
public class TomahawkEntity extends AbstractMissileEntity {

    private static final float EXPLOSION_POWER = 4.0F;
    private static final double CRUISE_ALTITUDE = 60.0;
    private static final double TERMINAL_RADIUS = 32.0;

    public TomahawkEntity(net.minecraft.world.entity.EntityType<? extends TomahawkEntity> type, Level level) {
        super(type, level, MissileType.CRUISE);
    }

    public static TomahawkEntity create(Level level, double x, double y, double z, double tx, double ty, double tz) {
        TomahawkEntity missile = new TomahawkEntity(ModEntities.TOMAHAWK.get(), level);
        missile.setPos(x, y, z);
        missile.setTarget(tx, ty, tz);
        return missile;
    }

    @Override
    protected Vec3 computeVelocity() {
        if (!hasTarget()) {
            return this.getDeltaMovement();
        }

        double dx = getTargetX() - getX();
        double dz = getTargetZ() - getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
        double speed = missileType.getTopSpeed();

        if (horizontalDist > TERMINAL_RADIUS) {
            // Cruise phase: hold a fixed altitude above the ground, homing horizontally toward target.
            double horizLen = Math.max(horizontalDist, 1.0E-4);
            double vx = (dx / horizLen) * speed;
            double vz = (dz / horizLen) * speed;
            double altitudeError = CRUISE_ALTITUDE - getY();
            double vy = Math.max(-0.6, Math.min(0.6, altitudeError * 0.1));
            return new Vec3(vx, vy, vz);
        }

        // Terminal phase: dive straight at the target, all three axes.
        double dy = getTargetY() - getY();
        return new Vec3(dx, dy, dz).normalize().scale(speed);
    }

    @Override
    protected void onDetonate(ServerLevel level) {
        explodeAt(level, getX(), getY(), getZ(), EXPLOSION_POWER);
    }
}
