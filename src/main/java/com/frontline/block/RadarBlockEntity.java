package com.frontline.block;

import com.frontline.ModBlockEntities;
import com.frontline.entity.Trackable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/** Радар: раз в полсекунды обзор воздушного пространства, помечает цели для ПВО и даёт сигнал редстоуна. */
public class RadarBlockEntity extends BlockEntity {
    public static final double RANGE = 192.0;

    @Nullable
    private UUID owner;

    public RadarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RADAR.get(), pos, state);
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        setChanged();
    }

    @Nullable
    public UUID getOwner() {
        return owner;
    }

    public static List<Entity> scan(Level level, BlockPos pos) {
        AABB box = new AABB(pos).inflate(RANGE);
        return level.getEntitiesOfClass(Entity.class, box, e -> e instanceof Trackable);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RadarBlockEntity be) {
        if (level.getGameTime() % 10 != 0) return;

        long now = level.getGameTime();
        boolean alert = false;
        for (Entity e : scan(level, pos)) {
            e.getPersistentData().putLong(Trackable.RADAR_TAG, now);
            if (!Trackable.friendly(((Trackable) e).getOwnerUuid(), be.owner)) {
                alert = true;
            }
        }
        if (state.getValue(RadarBlock.ALERT) != alert) {
            level.setBlock(pos, state.setValue(RadarBlock.ALERT, alert), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (owner != null) {
            tag.putUUID("Owner", owner);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
    }
}
