package com.moakiee.ae2lt.network;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

import com.moakiee.ae2lt.celestweave.CelestweaveArmorState;

public record CelestweaveSubmoduleActivePacket(UUID armorId, String submoduleId, boolean active) {
    public static CelestweaveSubmoduleActivePacket decode(FriendlyByteBuf buf) {
        return new CelestweaveSubmoduleActivePacket(
                buf.readUUID(),
                buf.readUtf(128),
                buf.readBoolean());
    }

    public static void encode(CelestweaveSubmoduleActivePacket payload, FriendlyByteBuf buf) {
        buf.writeUUID(payload.armorId);
        buf.writeUtf(payload.submoduleId, 128);
        buf.writeBoolean(payload.active);
    }

    public static void handle(CelestweaveSubmoduleActivePacket payload, Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                CelestweaveArmorState.markClientActive(payload.armorId(), payload.submoduleId(), payload.active())));
        context.setPacketHandled(true);
    }
}
