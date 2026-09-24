package com.bcw.mod.entity.missiles;

import com.bcw.mod.entity.AbstractMissileEntity;
import com.bcw.mod.entity.MissileType;
import com.bcw.mod.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/**
 * 3M22 Zircon — hypersonic glide cruise missile. Very high closing speed and strong
 * penetration: it is configured to punch through up to 10 layers of solid blocks
 * (representing concrete/obsidian bunkers) before its warhead finally detonates.
 */
public class ZirconEntity extends AbstractMissileEntity {

    private static final float EXPLOSION_POWER = 6.0F;

    public ZirconEntity(net.minecraft.world.entity.EntityType<? extends ZirconEntity> type, Level level) {
        super(type, level, MissileType.HYPERSONIC_GLIDE);
        this.setMaxPenetrations(10);
    }

    public static ZirconEntity create(Level level, double x, double y, double z, double tx, double ty, double tz) {
        ZirconEntity missile = new ZirconEntity(ModEntities.ZIRCON.get(), level);
        missile.setPos(x, y, z);
        missile.setTarget(tx, ty, tz);
        return missile;
    }

    @Override
    protected void onDetonate(ServerLevel level) {
        explodeAt(level, getX(), getY(), getZ(), EXPLOSION_POWER);
    }
}
