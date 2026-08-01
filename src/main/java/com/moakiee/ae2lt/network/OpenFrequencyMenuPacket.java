package com.moakiee.ae2lt.network;

import java.util.function.Supplier;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import com.moakiee.ae2lt.api.frequency.FrequencyBindingHost;
import com.moakiee.ae2lt.api.frequency.FrequencyBindingMenuHost;
import com.moakiee.ae2lt.grid.FrequencySecurityLevel;
import com.moakiee.ae2lt.grid.WirelessFrequencyManager;
import com.moakiee.ae2lt.item.TerminalCardAccess;
import com.moakiee.ae2lt.menu.FrequencyMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public record OpenFrequencyMenuPacket(boolean cardMode) {

    public static OpenFrequencyMenuPacket forBlock() {
        return new OpenFrequencyMenuPacket(false);
    }

    public static OpenFrequencyMenuPacket forCard() {
        return new OpenFrequencyMenuPacket(true);
    }

    public static void encode(OpenFrequencyMenuPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.cardMode);
    }

    public static OpenFrequencyMenuPacket decode(FriendlyByteBuf buf) {
        return new OpenFrequencyMenuPacket(buf.readBoolean());
    }

    public static void handle(OpenFrequencyMenuPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            if (packet.cardMode) {
                handleCardMode(player);
            } else {
                handleBlockMode(player);
            }
        });
        context.setPacketHandled(true);
    }

    private static void handleBlockMode(ServerPlayer player) {
        if (!(player.containerMenu instanceof AEBaseMenu parentMenu)
                || !(parentMenu instanceof FrequencyBindingMenuHost)
                || !parentMenu.stillValid(player)) {
            reject(player);
            return;
        }

        MenuLocator parentLocator = parentMenu.getLocator();
        if (parentLocator == null) {
            reject(player);
            return;
        }

        FrequencyBindingHost bindingHost = parentLocator.locate(player, FrequencyBindingHost.class);
        if (bindingHost == null) {
            reject(player);
            return;
        }
        int frequencyId = bindingHost.getFrequencyId();
        if (frequencyId > 0) {
            var manager = WirelessFrequencyManager.get();
            var frequency = manager == null ? null : manager.getFrequency(frequencyId);
            if (frequency != null
                    && !frequency.getPlayerAccess(player).canUse()
                    && frequency.getSecurity() != FrequencySecurityLevel.ENCRYPTED) {
                player.displayClientMessage(
                        Component.translatable("ae2lt.gui.error.no_access").withStyle(ChatFormatting.RED),
                        true);
                return;
            }
        }

        if (!MenuOpener.open(FrequencyMenu.TYPE, player, parentLocator)) {
            reject(player);
        }
    }

    private static void handleCardMode(ServerPlayer player) {
        if (!(player.containerMenu instanceof AEBaseMenu aeMenu) || !aeMenu.stillValid(player)) {
            reject(player);
            return;
        }

        MenuLocator locator = aeMenu.getLocator();
        ItemMenuHost host = locator == null ? null : locator.locate(player, ItemMenuHost.class);
        if (host == null || !TerminalCardAccess.hasCard(host.getItemStack())) {
            player.displayClientMessage(
                    Component.translatable("ae2lt.frequency_card.terminal_no_card").withStyle(ChatFormatting.RED),
                    true);
            return;
        }

        if (!MenuOpener.open(FrequencyMenu.TYPE, player, locator)) {
            reject(player);
        }
    }

    private static void reject(ServerPlayer player) {
        player.displayClientMessage(
                Component.translatable("ae2lt.gui.error.rejected").withStyle(ChatFormatting.RED),
                true);
    }
}
