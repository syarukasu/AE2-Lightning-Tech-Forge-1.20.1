package com.moakiee.ae2lt.network.railgun;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

import com.moakiee.ae2lt.network.NetworkInit;

/**
 * Server to client: a charged shot fired with the listed chain segments. The
 * client uses this to render electric arcs along each segment plus a flash
 * at the first hit.
 *
 * @param shooterId   id of the firing player; the client looks them up to compute the
 *                    gun-barrel position so the visual plasma trail emanates from the
 *                    weapon, not from the eye/screen-center.
 * @param from        the eye position the shot was fired from (server fallback)
 * @param firstHit    the impact location (entity hit position, or block-hit point on miss)
 * @param chainPath   pairs of points (from, to) for each chain segment to render
 * @param tier        charge tier ordinal (0=HV, 1=EHV1, 2=EHV2, 3=EHV3)
 * @param isMax       true on max-tier shots
 * @param soundEnabled true when railgun-specific sounds should play client-side
 * @param impactRadius radius (blocks) of the impact splash AOE for shockwave rendering
 */
public record RailgunFirePacket(
        UUID shooterId,
        Vec3 from,
        Vec3 firstHit,
        List<Vec3> chainPath,
        int tier,
        boolean isMax,
        boolean soundEnabled,
        float impactRadius) {

    public static void encode(RailgunFirePacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.shooterId);
        buf.writeDouble(packet.from.x); buf.writeDouble(packet.from.y); buf.writeDouble(packet.from.z);
        buf.writeDouble(packet.firstHit.x); buf.writeDouble(packet.firstHit.y); buf.writeDouble(packet.firstHit.z);
        buf.writeVarInt(packet.chainPath.size());
        for (Vec3 v : packet.chainPath) {
            buf.writeDouble(v.x); buf.writeDouble(v.y); buf.writeDouble(v.z);
        }
        buf.writeVarInt(packet.tier);
        buf.writeBoolean(packet.isMax);
        buf.writeBoolean(packet.soundEnabled);
        buf.writeFloat(packet.impactRadius);
    }

    public static RailgunFirePacket decode(FriendlyByteBuf buf) {
        UUID shooterId = buf.readUUID();
        Vec3 from = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3 first = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        int n = buf.readVarInt();
        List<Vec3> path = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            path.add(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        int tier = buf.readVarInt();
        boolean isMax = buf.readBoolean();
        boolean soundEnabled = buf.readBoolean();
        float impactRadius = buf.readFloat();
        return new RailgunFirePacket(shooterId, from, first, path, tier, isMax, soundEnabled, impactRadius);
    }

    public static void handle(RailgunFirePacket p, Supplier<NetworkEvent.Context> supplier) {
        var ctx = supplier.get();
        ctx.enqueueWork(() -> RailgunClientBridge.fire(p));
        ctx.setPacketHandled(true);
    }
}
