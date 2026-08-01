package com.moakiee.ae2lt.network;

import com.moakiee.ae2lt.AE2LightningTech;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

import com.moakiee.ae2lt.network.hub.DeviceHubActionPacket;
import com.moakiee.ae2lt.network.hub.DeviceHubSyncPacket;
import com.moakiee.ae2lt.network.hub.OpenDeviceHubPacket;
import com.moakiee.ae2lt.network.railgun.RailgunBeamChainFxPacket;
import com.moakiee.ae2lt.network.railgun.RailgunBeamTogglePacket;
import com.moakiee.ae2lt.network.railgun.RailgunBeamUpdatePacket;
import com.moakiee.ae2lt.network.railgun.RailgunFirePacket;
import com.moakiee.ae2lt.network.railgun.RailgunRecoilFxPacket;

public final class NetworkInit {
    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            id("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private static int nextPacketId;
    private static boolean registered;

    private NetworkInit() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        CHANNEL.registerMessage(
                nextPacketId++,
                WirelessConnectorUsePacket.class,
                WirelessConnectorUsePacket::encode,
                WirelessConnectorUsePacket::decode,
                WirelessConnectorUsePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(
                nextPacketId++,
                OpenFrequencyMenuPacket.class,
                OpenFrequencyMenuPacket::encode,
                OpenFrequencyMenuPacket::decode,
                OpenFrequencyMenuPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        // frequency system: C→S
        CHANNEL.registerMessage(
                nextPacketId++,
                CreateFrequencyPacket.class,
                CreateFrequencyPacket::encode,
                CreateFrequencyPacket::decode,
                CreateFrequencyPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(
                nextPacketId++,
                DeleteFrequencyPacket.class,
                DeleteFrequencyPacket::encode,
                DeleteFrequencyPacket::decode,
                DeleteFrequencyPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(
                nextPacketId++,
                EditFrequencyPacket.class,
                EditFrequencyPacket::encode,
                EditFrequencyPacket::decode,
                EditFrequencyPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(
                nextPacketId++,
                SelectFrequencyPacket.class,
                SelectFrequencyPacket::encode,
                SelectFrequencyPacket::decode,
                SelectFrequencyPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(
                nextPacketId++,
                ChangeMemberPacket.class,
                ChangeMemberPacket::encode,
                ChangeMemberPacket::decode,
                ChangeMemberPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        // S→C
        CHANNEL.registerMessage(
                nextPacketId++,
                EasterEggPacket.class,
                EasterEggPacket::encode,
                EasterEggPacket::decode,
                EasterEggPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(
                nextPacketId++,
                SyncFrequencyListPacket.class,
                SyncFrequencyListPacket::encode,
                SyncFrequencyListPacket::decode,
                SyncFrequencyListPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(
                nextPacketId++,
                SyncFrequencyDetailPacket.class,
                SyncFrequencyDetailPacket::encode,
                SyncFrequencyDetailPacket::decode,
                SyncFrequencyDetailPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(
                nextPacketId++,
                UpdateFrequencyBasicPacket.class,
                UpdateFrequencyBasicPacket::encode,
                UpdateFrequencyBasicPacket::decode,
                UpdateFrequencyBasicPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(
                nextPacketId++,
                FrequencyResponsePacket.class,
                FrequencyResponsePacket::encode,
                FrequencyResponsePacket::decode,
                FrequencyResponsePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        CHANNEL.registerMessage(
                nextPacketId++, FrequencyCardUsePacket.class,
                FrequencyCardUsePacket::encode, FrequencyCardUsePacket::decode, FrequencyCardUsePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(
                nextPacketId++, ToggleFrequencyCardAutoConnectPacket.class,
                ToggleFrequencyCardAutoConnectPacket::encode, ToggleFrequencyCardAutoConnectPacket::decode,
                ToggleFrequencyCardAutoConnectPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(
                nextPacketId++, DashPacket.class,
                DashPacket::encode, DashPacket::decode, DashPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(
                nextPacketId++, OpenDeviceHubPacket.class,
                OpenDeviceHubPacket::encode, OpenDeviceHubPacket::decode, OpenDeviceHubPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(
                nextPacketId++, DeviceHubActionPacket.class,
                DeviceHubActionPacket::encode, DeviceHubActionPacket::decode, DeviceHubActionPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(
                nextPacketId++, RailgunBeamTogglePacket.class,
                RailgunBeamTogglePacket::encode, RailgunBeamTogglePacket::decode, RailgunBeamTogglePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        CHANNEL.registerMessage(
                nextPacketId++, CelestweaveSubmoduleActivePacket.class,
                CelestweaveSubmoduleActivePacket::encode, CelestweaveSubmoduleActivePacket::decode,
                CelestweaveSubmoduleActivePacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(
                nextPacketId++, FlightInertiaSyncPacket.class,
                FlightInertiaSyncPacket::encode, FlightInertiaSyncPacket::decode, FlightInertiaSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(
                nextPacketId++, ShieldHitFeedbackSuppressionPacket.class,
                ShieldHitFeedbackSuppressionPacket::encode, ShieldHitFeedbackSuppressionPacket::decode,
                ShieldHitFeedbackSuppressionPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(
                nextPacketId++, DeviceHubSyncPacket.class,
                DeviceHubSyncPacket::encode, DeviceHubSyncPacket::decode, DeviceHubSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(
                nextPacketId++, RailgunFirePacket.class,
                RailgunFirePacket::encode, RailgunFirePacket::decode, RailgunFirePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(
                nextPacketId++, RailgunBeamUpdatePacket.class,
                RailgunBeamUpdatePacket::encode, RailgunBeamUpdatePacket::decode, RailgunBeamUpdatePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(
                nextPacketId++, RailgunBeamChainFxPacket.class,
                RailgunBeamChainFxPacket::encode, RailgunBeamChainFxPacket::decode, RailgunBeamChainFxPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(
                nextPacketId++, RailgunRecoilFxPacket.class,
                RailgunRecoilFxPacket::encode, RailgunRecoilFxPacket::decode, RailgunRecoilFxPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(AE2LightningTech.MODID, path);
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }

    public static void sendToPlayer(ServerPlayer player, Object message) {
        CHANNEL.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}

