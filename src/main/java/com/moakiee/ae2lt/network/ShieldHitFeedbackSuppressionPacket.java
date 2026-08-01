package com.moakiee.ae2lt.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record ShieldHitFeedbackSuppressionPacket(int entityId) {
    public static void encode(ShieldHitFeedbackSuppressionPacket payload, FriendlyByteBuf buf) {
        buf.writeVarInt(payload.entityId());
    }

    public static ShieldHitFeedbackSuppressionPacket decode(FriendlyByteBuf buf) {
        return new ShieldHitFeedbackSuppressionPacket(buf.readVarInt());
    }

    public static void handle(ShieldHitFeedbackSuppressionPacket payload, Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        context.enqueueWork(() -> ShieldHitFeedbackClientBridge.suppress(payload));
        context.setPacketHandled(true);
    }
}
