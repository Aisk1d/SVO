package com.bcw.mod.client;

import com.bcw.mod.network.PacketSyncRadar;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-only cache mapping each known radar station's block position to its most
 * recently received contact list. The launch terminal GUI reads from here.
 */
@OnlyIn(Dist.CLIENT)
public final class RadarClientState {

    private static final Map<BlockPos, List<PacketSyncRadar.Contact>> CONTACTS_BY_RADAR = new ConcurrentHashMap<>();

    public static void updateContacts(BlockPos radarPos, List<PacketSyncRadar.Contact> contacts) {
        CONTACTS_BY_RADAR.put(radarPos, contacts);
    }

    public static List<PacketSyncRadar.Contact> getContacts(BlockPos radarPos) {
        return CONTACTS_BY_RADAR.getOrDefault(radarPos, List.of());
    }

    private RadarClientState() {}
}
