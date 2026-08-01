package com.moakiee.ae2lt.network;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

import com.moakiee.ae2lt.celestweave.CelestweaveArmorState;

public record FlightInertiaSyncPacket(UUID armorId, boolean inertiaEnabled) {
    public static FlightInertiaSyncPacket decode(FriendlyByteBuf buf) {
        return new FlightInertiaSyncPacket(
                buf.readUUID(),
                buf.readBoolean());
    }

    public static void encode(FlightInertiaSyncPacket payload, FriendlyByteBuf buf) {
        buf.writeUUID(payload.armorId);
        buf.writeBoolean(payload.inertiaEnabled);
    }

    public static void handle(FlightInertiaSyncPacket payload, Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                CelestweaveArmorState.setClientFlightInertia(payload.armorId(), payload.inertiaEnabled())));
        context.setPacketHandled(true);
    }
}
