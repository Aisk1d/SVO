package com.bcw.mod.entity;

/**
 * Flight/physics profile shared by every missile family. New missiles pick one of
 * these instead of redefining physics from scratch.
 */
public enum MissileType {

    /** Flat, terrain-hugging flight at constant altitude. */
    CRUISE(0.9, 0.0, false, 1),

    /** Steep climb followed by a ballistic arc back down. */
    BALLISTIC(1.6, 400.0, true, 1),

    /** Very high speed, shallow arc, strong penetration on impact. */
    HYPERSONIC_GLIDE(3.8, 120.0, false, 1),

    /** Climbs like a ballistic missile, but releases sub-munitions at apex. */
    MIRV_BALLISTIC(1.4, 400.0, true, 10),

    /** Steep terminal dive after a short powered climb. */
    AEROBALLISTIC(2.6, 180.0, true, 1);

    private final double topSpeedBlocksPerTick;
    private final double apexAltitude;
    private final boolean hasBallisticArc;
    private final int warheadCount;

    MissileType(double topSpeedBlocksPerTick, double apexAltitude, boolean hasBallisticArc, int warheadCount) {
        this.topSpeedBlocksPerTick = topSpeedBlocksPerTick;
        this.apexAltitude = apexAltitude;
        this.hasBallisticArc = hasBallisticArc;
        this.warheadCount = warheadCount;
    }

    public double getTopSpeed() {
        return topSpeedBlocksPerTick;
    }

    public double getApexAltitude() {
        return apexAltitude;
    }

    public boolean hasBallisticArc() {
        return hasBallisticArc;
    }

    public int getWarheadCount() {
        return warheadCount;
    }
}
