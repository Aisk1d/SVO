package com.bcw.mod.network;

import com.bcw.mod.client.RadarClientState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Server -> client packet delivering a radar station's current contact list, keyed
 * by the station's block position so the client can update the right terminal GUI.
 */
public class PacketSyncRadar {

    private final BlockPos radarPos;
    private final List<Contact> contacts;

    public PacketSyncRadar(BlockPos radarPos, List<Contact> contacts) {
        this.radarPos = radarPos;
        this.contacts = contacts;
    }

    public static void encode(PacketSyncRadar packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.radarPos);
        buf.writeVarInt(packet.contacts.size());
        for (Contact contact : packet.contacts) {
            buf.writeUUID(contact.uuid());
            buf.writeUtf(contact.name(), 64);
            buf.writeBlockPos(contact.pos());
        }
    }

    public static PacketSyncRadar decode(FriendlyByteBuf buf) {
        BlockPos radarPos = buf.readBlockPos();
        int count = buf.readVarInt();
        List<Contact> contacts = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            UUID uuid = buf.readUUID();
            String name = buf.readUtf(64);
            BlockPos pos = buf.readBlockPos();
            contacts.add(new Contact(uuid, name, pos));
        }
        return new PacketSyncRadar(radarPos, contacts);
    }

    public static void handle(PacketSyncRadar packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> RadarClientState.updateContacts(packet.radarPos, packet.contacts));
        ctx.setPacketHandled(true);
    }

    public record Contact(UUID uuid, String name, BlockPos pos) {}
}
