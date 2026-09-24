package com.bcw.mod.entity.missiles;

import com.bcw.mod.entity.AbstractMissileEntity;
import com.bcw.mod.entity.MissileType;
import com.bcw.mod.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/**
 * RS-28 Sarmat — heavy ICBM. Climbs to a suborbital apex, then falls back along a
 * ballistic arc and releases 10 independent sub-munitions ("cassette warheads")
 * that scatter in a ring around the aim point before each detonates separately.
 */
public class SarmatEntity extends AbstractMissileEntity {

    private static final float MAIN_EXPLOSION_POWER = 5.0F;
    private static final float SUBMUNITION_EXPLOSION_POWER = 3.5F;
    private static final double SCATTER_RADIUS = 40.0;

    public SarmatEntity(net.minecraft.world.entity.EntityType<? extends SarmatEntity> type, Level level) {
        super(type, level, MissileType.MIRV_BALLISTIC);
    }

    public static SarmatEntity create(Level level, double x, double y, double z, double tx, double ty, double tz) {
        SarmatEntity missile = new SarmatEntity(ModEntities.SARMAT.get(), level);
        missile.setPos(x, y, z);
        missile.setTarget(tx, ty, tz);
        return missile;
    }

    @Override
    protected void onDetonate(ServerLevel level) {
        int count = missileType.getWarheadCount();
        double originX = getX();
        double originY = getY();
        double originZ = getZ();

        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            double offsetX = Math.cos(angle) * SCATTER_RADIUS;
            double offsetZ = Math.sin(angle) * SCATTER_RADIUS;
            explodeAt(level, originX + offsetX, originY, originZ + offsetZ, SUBMUNITION_EXPLOSION_POWER);
        }
        explodeAt(level, originX, originY, originZ, MAIN_EXPLOSION_POWER);
    }
}
