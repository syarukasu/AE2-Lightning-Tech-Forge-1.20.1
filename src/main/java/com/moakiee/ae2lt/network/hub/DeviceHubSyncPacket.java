package com.moakiee.ae2lt.network.hub;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

import com.moakiee.ae2lt.client.ClientNetworkPacketHandlers;

/** Server -> Client: sync full hub display state that cannot safely fit in menu data slots. */
public record DeviceHubSyncPacket(
        int containerId,
        String deviceName,
        boolean hasCore,
        boolean powered,
        boolean terrainDestruction,
        boolean pvp,
        boolean soundEnabled,
        List<String> moduleNameKeys,
        List<Integer> moduleCounts,
        List<Boolean> moduleEnabled,
        int selectedModuleIndex,
        List<String> moduleConfigKeys,
        List<String> moduleConfigLabels,
        List<String> moduleConfigValues,
        List<Boolean> moduleConfigEditable
) {

    public static DeviceHubSyncPacket decode(FriendlyByteBuf buf) {
        int containerId = buf.readVarInt();
        String deviceName = buf.readUtf(256);
        boolean hasCore = buf.readBoolean();
        boolean powered = buf.readBoolean();
        boolean terrainDestruction = buf.readBoolean();
        boolean pvp = buf.readBoolean();
        boolean soundEnabled = buf.readBoolean();
        int count = buf.readVarInt();
        List<String> nameKeys = new ArrayList<>(count);
        List<Integer> counts = new ArrayList<>(count);
        List<Boolean> enabled = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            nameKeys.add(buf.readUtf(256));
            counts.add(buf.readVarInt());
            enabled.add(buf.readBoolean());
        }
        int selectedModuleIndex = buf.readVarInt();
        int configCount = buf.readVarInt();
        List<String> moduleConfigKeys = new ArrayList<>(configCount);
        List<String> moduleConfigLabels = new ArrayList<>(configCount);
        List<String> moduleConfigValues = new ArrayList<>(configCount);
        List<Boolean> moduleConfigEditable = new ArrayList<>(configCount);
        for (int i = 0; i < configCount; i++) {
            moduleConfigKeys.add(buf.readUtf(128));
            moduleConfigLabels.add(buf.readUtf(256));
            moduleConfigValues.add(buf.readUtf(256));
            moduleConfigEditable.add(buf.readBoolean());
        }
        return new DeviceHubSyncPacket(
                containerId,
                deviceName,
                hasCore,
                powered,
                terrainDestruction,
                pvp,
                soundEnabled,
                nameKeys,
                counts,
                enabled,
                selectedModuleIndex,
                moduleConfigKeys,
                moduleConfigLabels,
                moduleConfigValues,
                moduleConfigEditable);
    }

    public static void encode(DeviceHubSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.containerId);
        buf.writeUtf(packet.deviceName, 256);
        buf.writeBoolean(packet.hasCore);
        buf.writeBoolean(packet.powered);
        buf.writeBoolean(packet.terrainDestruction);
        buf.writeBoolean(packet.pvp);
        buf.writeBoolean(packet.soundEnabled);
        int count = Math.min(Math.min(packet.moduleNameKeys.size(), packet.moduleCounts.size()), packet.moduleEnabled.size());
        buf.writeVarInt(count);
        for (int i = 0; i < count; i++) {
            buf.writeUtf(packet.moduleNameKeys.get(i), 256);
            buf.writeVarInt(packet.moduleCounts.get(i));
            buf.writeBoolean(packet.moduleEnabled.get(i));
        }
        buf.writeVarInt(packet.selectedModuleIndex);
        int configCount = Math.min(
                Math.min(Math.min(packet.moduleConfigKeys.size(), packet.moduleConfigLabels.size()), packet.moduleConfigValues.size()),
                packet.moduleConfigEditable.size());
        buf.writeVarInt(configCount);
        for (int i = 0; i < configCount; i++) {
            buf.writeUtf(packet.moduleConfigKeys.get(i), 128);
            buf.writeUtf(packet.moduleConfigLabels.get(i), 256);
            buf.writeUtf(packet.moduleConfigValues.get(i), 256);
            buf.writeBoolean(packet.moduleConfigEditable.get(i));
        }
    }

    public static void handle(DeviceHubSyncPacket pkt, Supplier<NetworkEvent.Context> supplier) {
        var ctx = supplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> ClientNetworkPacketHandlers.handleDeviceHubSync(pkt)));
        ctx.setPacketHandled(true);
    }
}
