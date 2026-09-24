package com.bcw.mod.blockentity;

import com.bcw.mod.network.NetworkHandler;
import com.bcw.mod.network.PacketSyncRadar;
import com.bcw.mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * RLS (radar) block entity. Every {@link #SCAN_INTERVAL_TICKS} ticks it sweeps a
 * large cubic volume around itself for players and entities, then pushes the
 * resulting contact list to every nearby client so the launch terminal GUI can
 * display live target coordinates.
 */
public class RadarBlockEntity extends BlockEntity {

    public static final double SCAN_RADIUS = 2500.0;
    private static final int SCAN_INTERVAL_TICKS = 40;
    private static final double SYNC_RADIUS = 128.0;

    private int scanCooldown = 0;
    private float antennaRotation = 0.0F;
    private final List<BlockPos> lastContacts = new ArrayList<>();

    public RadarBlockEntity(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(ModBlockEntities.RADAR.get(), pos, state);
    }

    public void serverTick(Level level, BlockPos pos) {
        antennaRotation = (antennaRotation + 6.0F) % 360.0F;

        if (--scanCooldown > 0) {
            return;
        }
        scanCooldown = SCAN_INTERVAL_TICKS;

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        AABB scanBox = new AABB(pos).inflate(SCAN_RADIUS);
        List<Player> players = serverLevel.getEntitiesOfClass(Player.class, scanBox);

        lastContacts.clear();
        List<PacketSyncRadar.Contact> contacts = new ArrayList<>();
        for (Player player : players) {
            BlockPos contactPos = player.blockPosition();
            lastContacts.add(contactPos);
            contacts.add(new PacketSyncRadar.Contact(player.getUUID(), player.getName().getString(), contactPos));
        }

        PacketSyncRadar packet = new PacketSyncRadar(pos, contacts);
        NetworkHandler.CHANNEL.send(
                PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        pos.getX(), pos.getY(), pos.getZ(), SYNC_RADIUS, serverLevel.dimension())),
                packet
        );
    }

    public float getAntennaRotation() {
        return antennaRotation;
    }

    public List<BlockPos> getLastContacts() {
        return lastContacts;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ScanCooldown", scanCooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        scanCooldown = tag.getInt("ScanCooldown");
    }
}
