package com.moakiee.ae2lt.network.railgun;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

import com.moakiee.ae2lt.network.NetworkInit;

/** Server to client: apply visual recoil after a charged shot. */
public record RailgunRecoilFxPacket(float pitchUp, int tierOrdinal) {
    public static void encode(RailgunRecoilFxPacket packet, FriendlyByteBuf buf) {
        buf.writeFloat(packet.pitchUp());
        buf.writeVarInt(packet.tierOrdinal());
    }

    public static RailgunRecoilFxPacket decode(FriendlyByteBuf buf) {
        return new RailgunRecoilFxPacket(buf.readFloat(), buf.readVarInt());
    }

    public static void handle(RailgunRecoilFxPacket p, Supplier<NetworkEvent.Context> supplier) {
        var ctx = supplier.get();
        ctx.enqueueWork(() -> RailgunClientBridge.recoil(p));
        ctx.setPacketHandled(true);
    }
}
