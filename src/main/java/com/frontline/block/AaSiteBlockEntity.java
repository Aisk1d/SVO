package com.frontline.block;

import com.frontline.ModBlockEntities;
import com.frontline.ModEntities;
import com.frontline.entity.InterceptorEntity;
import com.frontline.entity.Trackable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Зенитная батарея. Сама выбирает ближайшую чужую цель и пускает перехватчик.
 * Без радара видит цели на 48 блоков, с радаром — на 160.
 */
public class AaSiteBlockEntity extends BlockEntity {
    public static final int MAX_AMMO = 4;
    private static final double NEAR_RANGE = 48.0;
    private static final double FAR_RANGE = 160.0;
    private static final double MIN_RANGE = 6.0;

    private int ammo;
    private int cooldown;
    @Nullable
    private UUID owner;

    public AaSiteBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AA_SITE.get(), pos, state);
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
        setChanged();
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AaSiteBlockEntity be) {
        if (be.cooldown > 0) be.cooldown--;
        if (be.ammo <= 0 || be.cooldown > 0 || level.getGameTime() % 5 != 0) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        long now = level.getGameTime();
        Vec3 origin = Vec3.atBottomCenterOf(pos.above()).add(0.0, 0.8, 0.0);
        AABB box = new AABB(pos).inflate(FAR_RANGE);

        List<InterceptorEntity> inFlight = level.getEntitiesOfClass(InterceptorEntity.class, box);

        Entity best = null;
        double bestDist = Double.MAX_VALUE;
        List<Entity> candidates = level.getEntitiesOfClass(Entity.class, box,
                x -> x instanceof Trackable tr && tr.isEngageable()
                        && !Trackable.friendly(tr.getOwnerUuid(), be.owner));

        for (Entity e : candidates) {
            double d = e.position().distanceTo(origin);
            boolean tracked = now - e.getPersistentData().getLong(Trackable.RADAR_TAG) <= 40;
            if (d < MIN_RANGE || d > (tracked ? FAR_RANGE : NEAR_RANGE)) continue;

            int assigned = 0;
            for (InterceptorEntity ic : inFlight) {
                if (ic.getTargetId() == e.getId()) assigned++;
            }
            if (assigned >= 2) continue;

            if (d < bestDist) {
                best = e;
                bestDist = d;
            }
        }
        if (best == null) return;

        InterceptorEntity missile = ModEntities.INTERCEPTOR.get().create(serverLevel);
        if (missile == null) return;
        missile.launch(origin, best);
        serverLevel.addFreshEntity(missile);
        serverLevel.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.BLOCKS, 3.0F, 0.8F);

        be.ammo--;
        be.cooldown = 30;
        be.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Ammo", ammo);
        if (owner != null) {
            tag.putUUID("Owner", owner);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ammo = tag.getInt("Ammo");
        owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
    }
}
