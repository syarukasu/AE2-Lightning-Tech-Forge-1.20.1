package com.moakiee.ae2lt.network.railgun;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

import com.moakiee.ae2lt.network.NetworkInit;

/**
 * Server to tracking client: keepalive/update packet for an active beam owned
 * by player {@code shooterId}. {@code active=false} signals beam stop.
 */
public record RailgunBeamUpdatePacket(UUID shooterId, Vec3 from, Vec3 to, boolean active)
{

    public static void encode(RailgunBeamUpdatePacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.shooterId);
        buf.writeDouble(packet.from.x); buf.writeDouble(packet.from.y); buf.writeDouble(packet.from.z);
        buf.writeDouble(packet.to.x); buf.writeDouble(packet.to.y); buf.writeDouble(packet.to.z);
        buf.writeBoolean(packet.active);
    }

    public static RailgunBeamUpdatePacket decode(FriendlyByteBuf buf) {
        return new RailgunBeamUpdatePacket(
                buf.readUUID(),
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                buf.readBoolean());
    }

    public static void handle(RailgunBeamUpdatePacket p, Supplier<NetworkEvent.Context> supplier) {
        var ctx = supplier.get();
        ctx.enqueueWork(() -> RailgunClientBridge.beamUpdate(p));
        ctx.setPacketHandled(true);
    }
}
