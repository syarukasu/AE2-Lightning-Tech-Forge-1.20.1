package com.moakiee.ae2lt.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record DashPacket() {
    public static DashPacket decode(FriendlyByteBuf buf) {
        return new DashPacket();
    }

    public static void encode(DashPacket payload, FriendlyByteBuf buf) {}

    public static void handle(DashPacket payload, Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                com.moakiee.ae2lt.celestweave.module.DashSubmodule.applyDash(
                        player,
                        player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET));
            }
        });
        context.setPacketHandled(true);
    }
}
