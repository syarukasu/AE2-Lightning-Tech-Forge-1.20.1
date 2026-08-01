package com.moakiee.ae2lt.network;

import com.moakiee.ae2lt.blockentity.AdvancedWirelessOverloadedControllerBlockEntity;
import com.moakiee.ae2lt.blockentity.WirelessOverloadedControllerBlockEntity;
import com.moakiee.ae2lt.grid.WirelessFrequencyManager;
import com.moakiee.ae2lt.grid.wirelesslink.WirelessLinkRegistry;
import com.moakiee.ae2lt.item.OverloadedFrequencyCardItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record FrequencyCardUsePacket(
        InteractionHand hand,
        BlockPos pos,
        Direction face,
        double hitX,
        double hitY,
        double hitZ,
        boolean shiftDown
) {

    public static FrequencyCardUsePacket decode(FriendlyByteBuf buf) {
        return new FrequencyCardUsePacket(
                buf.readEnum(InteractionHand.class),
                buf.readBlockPos(),
                buf.readEnum(Direction.class),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readBoolean());
    }

    public static void encode(FrequencyCardUsePacket packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.hand);
        buf.writeBlockPos(packet.pos);
        buf.writeEnum(packet.face);
        buf.writeDouble(packet.hitX);
        buf.writeDouble(packet.hitY);
        buf.writeDouble(packet.hitZ);
        buf.writeBoolean(packet.shiftDown);
    }

    public static void handle(FrequencyCardUsePacket payload, Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                payload.handleOnServer(player);
            }
        });
        context.setPacketHandled(true);
    }

    private void handleOnServer(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        if (!level.isLoaded(pos)) return;
        if (player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(pos)) > 64.0D) return;

        var stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof OverloadedFrequencyCardItem)) return;

        var be = level.getBlockEntity(pos);
        if (shiftDown || player.isShiftKeyDown()) {
            var data = OverloadedFrequencyCardItem.getData(stack);
            if (data.isBound() && !data.canBeUsedBy(player.getUUID())) {
                message(player, "ae2lt.frequency_card.card_owner_mismatch", ChatFormatting.RED);
                return;
            }
            if (be instanceof AdvancedWirelessOverloadedControllerBlockEntity controller) {
                bindController(player, stack, level, controller);
            } else if (be instanceof WirelessOverloadedControllerBlockEntity) {
                message(player, "ae2lt.frequency_card.bind_requires_advanced", ChatFormatting.RED);
            } else {
                message(player, "ae2lt.frequency_card.not_advanced_controller", ChatFormatting.RED);
            }
            return;
        }

        tryLinkWithCard(player, stack, level, pos, face,
                new net.minecraft.world.phys.Vec3(hitX, hitY, hitZ));
    }

    /**
     * Performs the non-shift "connect/disconnect target" action for a frequency
     * card against the block at {@code pos}, displaying the resulting feedback on
     * the player's action bar. Shared by the held-card right-click (this packet)
     * and the terminal-held right-click handler, so a card installed inside a
     * wireless terminal links exactly like a card held in hand.
     */
    public static void tryLinkWithCard(
            ServerPlayer player,
            net.minecraft.world.item.ItemStack card,
            ServerLevel level,
            BlockPos pos,
            Direction face,
            net.minecraft.world.phys.Vec3 hitVec) {
        var data = OverloadedFrequencyCardItem.getData(card);
        if (!data.isBound()) {
            message(player, "ae2lt.frequency_card.unbound_message", ChatFormatting.RED);
            return;
        }
        if (!data.canBeUsedBy(player.getUUID())) {
            message(player, "ae2lt.frequency_card.card_owner_mismatch", ChatFormatting.RED);
            return;
        }

        var manager = WirelessFrequencyManager.get();
        var frequency = manager == null ? null : manager.getFrequency(data.frequencyId());
        if (frequency == null || !frequency.canPlayerAccess(player, "")) {
            message(player, "ae2lt.frequency_card.no_frequency_permission", ChatFormatting.RED);
            return;
        }

        var feedback = WirelessLinkRegistry.get(level.getServer()).handleManualUse(
                player,
                data.frequencyId(),
                level,
                pos,
                face,
                hitVec);
        player.displayClientMessage(
                Component.translatable(feedback.translationKey(), feedback.args())
                        .withStyle(feedback.style()),
                true);
    }

    private static void bindController(
            ServerPlayer player,
            net.minecraft.world.item.ItemStack stack,
            ServerLevel level,
            AdvancedWirelessOverloadedControllerBlockEntity controller) {
        int frequencyId = controller.getFrequencyId();
        if (frequencyId <= 0) {
            message(player, "ae2lt.frequency_card.controller_no_frequency", ChatFormatting.RED);
            return;
        }

        var manager = WirelessFrequencyManager.get();
        var frequency = manager == null ? null : manager.getFrequency(frequencyId);
        if (frequency == null || !frequency.canPlayerAccess(player, "")) {
            message(player, "ae2lt.frequency_card.no_frequency_permission", ChatFormatting.RED);
            return;
        }

        OverloadedFrequencyCardItem.bindFrequency(
                stack,
                frequencyId,
                level.dimension(),
                controller.getBlockPos(),
                player.getUUID());
        player.displayClientMessage(
                Component.translatable(
                                "ae2lt.frequency_card.bound",
                                FrequencyCardBindingMessage.displayName(frequencyId, frequency.getName()))
                        .withStyle(ChatFormatting.GREEN),
                true);
    }

    private static void message(ServerPlayer player, String key, ChatFormatting style) {
        player.displayClientMessage(Component.translatable(key).withStyle(style), true);
    }
}
