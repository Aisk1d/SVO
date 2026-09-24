package com.frontline.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

/** Основа для дронов и БПЛА: здоровье, владелец, подгрузка чанков, полёт без гравитации. */
public abstract class AerialEntity extends Entity implements Trackable {
    private static final TicketType<Long> TICKET =
            TicketType.create("frontline_aerial", Long::compare, 60);

    protected float health;
    @Nullable
    protected UUID ownerUuid;
    protected int flightTicks;

    protected AerialEntity(EntityType<? extends AerialEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
        this.health = maxHealth();
    }

    protected abstract float maxHealth();

    protected abstract void serverTick(ServerLevel level);

    protected void clientTick() {
    }

    protected void writeExtra(CompoundTag tag) {
    }

    protected void readExtra(CompoundTag tag) {
    }

    public void setOwner(@Nullable UUID owner) {
        this.ownerUuid = owner;
    }

    @Nullable
    @Override
    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            clientTick();
            return;
        }
        if (!(level() instanceof ServerLevel serverLevel)) return;
        flightTicks++;
        serverTick(serverLevel);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide || isRemoved() || isInvulnerableTo(source)) return false;
        health -= amount;
        if (health <= 0.0F) {
            crash();
        }
        return true;
    }

    /** Сбит: небольшой взрыв без разрушения блоков. */
    protected void crash() {
        level().explode(this, getX(), getY(), getZ(), 1.5F, Level.ExplosionInteraction.NONE);
        discard();
    }

    /** Боевой подрыв. */
    protected void detonate(float power) {
        level().explode(this, getX(), getY(), getZ(), power, Level.ExplosionInteraction.TNT);
        discard();
    }

    /** Двигаемся с учётом столкновений. true — врезались во что-то. */
    protected boolean flyBy(Vec3 velocity) {
        move(MoverType.SELF, velocity);
        return horizontalCollision || verticalCollision;
    }

    protected void orient(Vec3 v) {
        double horiz = Math.sqrt(v.x * v.x + v.z * v.z);
        if (horiz > 1.0E-4) {
            setYRot((float) (Mth.atan2(v.x, v.z) * (180.0 / Math.PI)));
        }
        if (horiz > 1.0E-4 || Math.abs(v.y) > 1.0E-4) {
            setXRot((float) (Mth.atan2(v.y, horiz) * (180.0 / Math.PI)));
        }
    }

    protected void keepChunksLoaded(ServerLevel level, Vec3 ahead) {
        long id = getId();
        level.getChunkSource().addRegionTicket(TICKET, new ChunkPos(BlockPos.containing(position())), 2, id);
        level.getChunkSource().addRegionTicket(TICKET, new ChunkPos(BlockPos.containing(ahead)), 2, id);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("Health", health);
        tag.putInt("Flight", flightTicks);
        if (ownerUuid != null) {
            tag.putUUID("Owner", ownerUuid);
        }
        writeExtra(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        health = tag.contains("Health") ? tag.getFloat("Health") : maxHealth();
        flightTicks = tag.getInt("Flight");
        ownerUuid = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        readExtra(tag);
    }
}
