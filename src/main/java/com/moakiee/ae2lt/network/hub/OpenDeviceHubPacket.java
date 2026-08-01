package com.moakiee.ae2lt.network.hub;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

import com.moakiee.ae2lt.menu.hub.DeviceHubHost;
import com.moakiee.ae2lt.network.NetworkInit;

/** Client → Server: request to open the DeviceHub UI. */
public record OpenDeviceHubPacket(int defaultTab) {
    public static OpenDeviceHubPacket decode(FriendlyByteBuf buf) {
        return new OpenDeviceHubPacket(buf.readVarInt());
    }

    public static void encode(OpenDeviceHubPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.defaultTab);
    }

    public static void handle(OpenDeviceHubPacket pkt, Supplier<NetworkEvent.Context> supplier) {
        var ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                DeviceHubHost.open(player, pkt.defaultTab());
            }
        });
        ctx.setPacketHandled(true);
    }
}
