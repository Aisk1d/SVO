package com.frontline.block;

import com.frontline.ModBlockEntities;
import com.frontline.ModEntities;
import com.frontline.entity.MissileEntity;
import com.frontline.missile.BallisticTrajectory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.UUID;

public class MissileLauncherBlockEntity extends BlockEntity {

    /** Тип заряженной ракеты: обычная / "Орешник" / "Буревестник" — у каждой свой профиль полёта, дальность и мощность. */
    public enum MissileKind {
        BALLISTIC(ModEntities.MISSILE, BallisticTrajectory.Profile.HIGH_ARC, 8.0F,
                BallisticTrajectory.MIN_RANGE, BallisticTrajectory.MAX_RANGE),
        ORESHNIK(ModEntities.ORESHNIK, BallisticTrajectory.Profile.STEEP_LOFTED, 14.0F,
                60.0, 12_000.0),
        BUREVESTNIK(ModEntities.BUREVESTNIK, BallisticTrajectory.Profile.LOW_CRUISE, 10.0F,
                80.0, 60_000.0);

        public final RegistryObject<net.minecraft.world.entity.EntityType<MissileEntity>> entityType;
        public final BallisticTrajectory.Profile profile;
        public final float explosionPower;
        public final double minRange;
        public final double maxRange;

        MissileKind(RegistryObject<net.minecraft.world.entity.EntityType<MissileEntity>> entityType,
                    BallisticTrajectory.Profile profile, float explosionPower, double minRange, double maxRange) {
            this.entityType = entityType;
            this.profile = profile;
            this.explosionPower = explosionPower;
            this.minRange = minRange;
            this.maxRange = maxRange;
        }
    }

    private boolean loaded;
    private MissileKind loadedKind = MissileKind.BALLISTIC;
    private boolean hasTarget;
    private double tx, ty, tz;
    @Nullable
    private UUID owner;

    public MissileLauncherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LAUNCHER.get(), pos, state);
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        setChanged();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public MissileKind getLoadedKind() {
        return loadedKind;
    }

    public void setLoaded(boolean value, MissileKind kind) {
        this.loaded = value;
        this.loadedKind = kind;
        setChanged();
    }

    @Nullable
    public Vec3 getTarget() {
        return hasTarget ? new Vec3(tx, ty, tz) : null;
    }

    public void setTarget(Vec3 t) {
        this.hasTarget = true;
        this.tx = t.x;
        this.ty = t.y;
        this.tz = t.z;
        setChanged();
    }

    /** Запуск (вызывается по сигналу редстоуна). */
    public boolean tryLaunch(ServerLevel level, BlockPos pos) {
        if (!loaded || !hasTarget) return false;

        Vec3 start = Vec3.atBottomCenterOf(pos.above());
        Vec3 target = new Vec3(tx, ty, tz);
        if (BallisticTrajectory.validate(start, target, loadedKind.minRange, loadedKind.maxRange)
                != BallisticTrajectory.Result.OK) return false;
        if (!level.getWorldBorder().isWithinBounds(BlockPos.containing(target))) return false;

        MissileEntity missile = new MissileEntity(loadedKind.entityType.get(), level);
        missile.launch(start, target, loadedKind.profile, loadedKind.explosionPower);
        missile.setOwner(owner);
        level.addFreshEntity(missile);
        level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.BLOCKS, 4.0F, 0.5F);

        loaded = false;
        setChanged();
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("Loaded", loaded);
        tag.putInt("LoadedKind", loadedKind.ordinal());
        tag.putBoolean("HasTarget", hasTarget);
        tag.putDouble("TX", tx);
        tag.putDouble("TY", ty);
        tag.putDouble("TZ", tz);
        if (owner != null) {
            tag.putUUID("Owner", owner);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loaded = tag.getBoolean("Loaded");
        int kindOrdinal = tag.contains("LoadedKind") ? tag.getInt("LoadedKind") : 0;
        MissileKind[] kinds = MissileKind.values();
        loadedKind = kinds[Math.max(0, Math.min(kindOrdinal, kinds.length - 1))];
        hasTarget = tag.getBoolean("HasTarget");
        tx = tag.getDouble("TX");
        ty = tag.getDouble("TY");
        tz = tag.getDouble("TZ");
        owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
    }
}
