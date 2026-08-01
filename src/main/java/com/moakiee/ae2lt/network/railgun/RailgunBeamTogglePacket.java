package com.moakiee.ae2lt.network.railgun;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

import com.moakiee.ae2lt.item.railgun.ElectromagneticRailgunItem;
import com.moakiee.ae2lt.logic.railgun.RailgunBeamService;
import com.moakiee.ae2lt.network.NetworkInit;

/** Client to server: toggle left-beam firing on/off. */
public record RailgunBeamTogglePacket(boolean firing, InteractionHand hand) {
    public static void encode(RailgunBeamTogglePacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.firing());
        buf.writeEnum(packet.hand());
    }

    public static RailgunBeamTogglePacket decode(FriendlyByteBuf buf) {
        return new RailgunBeamTogglePacket(buf.readBoolean(), buf.readEnum(InteractionHand.class));
    }

    public static void handle(RailgunBeamTogglePacket pkt, Supplier<NetworkEvent.Context> supplier) {
        var ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer p = ctx.getSender();
            if (p == null) return;
            ItemStack stack = p.getItemInHand(pkt.hand());
            if (pkt.firing() && !(stack.getItem() instanceof ElectromagneticRailgunItem)) return;
            RailgunBeamService.setFiring(p, pkt.hand(), pkt.firing());
        });
        ctx.setPacketHandled(true);
    }
}
