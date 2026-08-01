package com.moakiee.ae2lt.network;

import com.moakiee.ae2lt.item.OverloadedFrequencyCardItem;
import com.moakiee.ae2lt.item.TerminalCardAccess;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

import appeng.menu.AEBaseMenu;
import appeng.api.implementations.menuobjects.ItemMenuHost;

public record ToggleFrequencyCardAutoConnectPacket(Optional<InteractionHand> hand, boolean terminalCard)
{

    public static ToggleFrequencyCardAutoConnectPacket forHand(InteractionHand hand) {
        return new ToggleFrequencyCardAutoConnectPacket(Optional.of(hand), false);
    }

    public static ToggleFrequencyCardAutoConnectPacket forPreferredCard() {
        return new ToggleFrequencyCardAutoConnectPacket(Optional.empty(), false);
    }

    public static ToggleFrequencyCardAutoConnectPacket forTerminalCard() {
        return new ToggleFrequencyCardAutoConnectPacket(Optional.empty(), true);
    }

    public static ToggleFrequencyCardAutoConnectPacket decode(FriendlyByteBuf buf) {
        boolean terminalCard = buf.readBoolean();
        Optional<InteractionHand> hand = buf.readBoolean()
                ? Optional.of(buf.readEnum(InteractionHand.class))
                : Optional.empty();
        return new ToggleFrequencyCardAutoConnectPacket(hand, terminalCard);
    }

    public static void encode(ToggleFrequencyCardAutoConnectPacket payload, FriendlyByteBuf buf) {
        buf.writeBoolean(payload.terminalCard);
        buf.writeBoolean(payload.hand.isPresent());
        payload.hand.ifPresent(value -> buf.writeEnum(value));
    }

    public static void handle(ToggleFrequencyCardAutoConnectPacket payload, Supplier<NetworkEvent.Context> supplier) {
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
        if (terminalCard) {
            handleTerminalCard(player);
            return;
        }

        ItemStack stack;
        if (hand.isPresent()) {
            stack = player.getItemInHand(hand.get());
        } else {
            var selection = OverloadedFrequencyCardItem.selectToggleCard(player);
            if (selection.ambiguous()) {
                player.displayClientMessage(
                        Component.translatable("ae2lt.frequency_card.auto_ambiguous")
                                .withStyle(ChatFormatting.RED),
                        true);
                return;
            }
            stack = selection.selected().orElse(ItemStack.EMPTY);
            if (stack.isEmpty()) {
                player.displayClientMessage(
                        Component.translatable("ae2lt.frequency_card.no_toggle_candidate")
                                .withStyle(ChatFormatting.RED),
                        true);
                return;
            }
        }
        if (!(stack.getItem() instanceof OverloadedFrequencyCardItem)) return;

        var data = OverloadedFrequencyCardItem.getData(stack);
        if (data.isBound() && !data.canBeUsedBy(player.getUUID())) {
            player.displayClientMessage(
                    Component.translatable("ae2lt.frequency_card.card_owner_mismatch")
                            .withStyle(ChatFormatting.RED),
                    true);
            return;
        }

        boolean enabled = OverloadedFrequencyCardItem.toggleAutoConnect(stack);
        messageAutoConnectState(player, enabled);
    }

    private void handleTerminalCard(ServerPlayer player) {
        if (!(player.containerMenu instanceof AEBaseMenu aeMenu)
                || aeMenu.getLocator() == null
                || !aeMenu.stillValid(player)) {
            player.displayClientMessage(
                    Component.translatable("ae2lt.gui.error.rejected").withStyle(ChatFormatting.RED),
                    true);
            return;
        }

        ItemMenuHost host = aeMenu.getLocator().locate(player, ItemMenuHost.class);
        ItemStack terminal = host == null ? ItemStack.EMPTY : host.getItemStack();
        if (!TerminalCardAccess.hasCard(terminal)) {
            player.displayClientMessage(
                    Component.translatable("ae2lt.frequency_card.terminal_no_card")
                            .withStyle(ChatFormatting.RED),
                    true);
            return;
        }

        var data = TerminalCardAccess.readCardData(terminal);
        if (data.isBound() && !data.canBeUsedBy(player.getUUID())) {
            player.displayClientMessage(
                    Component.translatable("ae2lt.frequency_card.card_owner_mismatch")
                            .withStyle(ChatFormatting.RED),
                    true);
            return;
        }

        if (!TerminalCardAccess.updateCard(terminal, cardData -> cardData.toggleAutoConnect())) {
            player.displayClientMessage(
                    Component.translatable("ae2lt.frequency_card.terminal_no_card")
                            .withStyle(ChatFormatting.RED),
                    true);
            return;
        }

        messageAutoConnectState(player, TerminalCardAccess.readCardData(terminal).autoConnect());
    }

    private static void messageAutoConnectState(ServerPlayer player, boolean enabled) {
        player.displayClientMessage(
                Component.translatable(enabled
                                ? "ae2lt.frequency_card.auto_enabled"
                                : "ae2lt.frequency_card.auto_disabled")
                        .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.YELLOW),
                true);
    }
}
