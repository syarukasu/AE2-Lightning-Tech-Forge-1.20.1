package com.moakiee.ae2lt.client;

import java.util.List;

import com.moakiee.ae2lt.client.gui.FrequencyScreen;
import com.moakiee.ae2lt.network.SyncFrequencyDetailPacket;
import com.moakiee.ae2lt.network.SyncFrequencyListPacket;
import com.moakiee.ae2lt.network.UpdateFrequencyBasicPacket;
import com.moakiee.ae2lt.network.hub.DeviceHubSyncPacket;
import com.moakiee.ae2lt.menu.hub.DeviceHubMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public final class ClientNetworkPacketHandlers {

    private ClientNetworkPacketHandlers() {
    }

    public static void handleEasterEgg() {
        EasterEggOverlay.trigger();
    }

    public static void handleFrequencyResponse(Component message) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        if (minecraft.screen instanceof FrequencyScreen fs) {
            fs.showInlineError(message);
        } else {
            player.displayClientMessage(message, true);
        }
    }

    public static void handleFrequencyList(List<SyncFrequencyListPacket.FrequencyEntry> entries) {
        ClientFrequencyCache.updateFromSync(entries);
    }

    public static void handleFrequencyDetail(int frequencyId, byte syncType, CompoundTag data) {
        if (syncType == SyncFrequencyDetailPacket.TYPE_MEMBERS) {
            ClientFrequencyCache.updateMembers(frequencyId, data);
        } else if (syncType == SyncFrequencyDetailPacket.TYPE_CONNECTIONS) {
            ClientFrequencyCache.updateConnections(frequencyId, data);
        }
    }

    public static void handleFrequencyBasicUpdate(UpdateFrequencyBasicPacket packet) {
        if (packet.deleted()) {
            ClientFrequencyCache.removeFrequency(packet.frequencyId());
        } else {
            ClientFrequencyCache.upsertFrequency(
                    packet.frequencyId(),
                    packet.name(),
                    packet.color(),
                    packet.ownerUUID(),
                    packet.security());
        }
    }

    public static void handleDeviceHubSync(DeviceHubSyncPacket packet) {
        var player = Minecraft.getInstance().player;
        if (player == null
                || !(player.containerMenu instanceof DeviceHubMenu menu)
                || menu.containerId != packet.containerId()) {
            return;
        }
        menu.receiveSync(
                packet.deviceName(),
                packet.hasCore(),
                packet.powered(),
                packet.terrainDestruction(),
                packet.pvp(),
                packet.soundEnabled(),
                packet.moduleNameKeys(),
                packet.moduleCounts(),
                packet.moduleEnabled(),
                packet.selectedModuleIndex(),
                packet.moduleConfigKeys(),
                packet.moduleConfigLabels(),
                packet.moduleConfigValues(),
                packet.moduleConfigEditable());
    }
}
