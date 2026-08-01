package com.moakiee.ae2lt.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

import com.moakiee.ae2lt.AE2LightningTech;
import com.moakiee.ae2lt.config.AE2LTCommonConfig;
import com.moakiee.ae2lt.celestweave.ArmorPhaseFlightRules;
import com.moakiee.ae2lt.celestweave.CelestweaveArmorState;
import com.moakiee.ae2lt.celestweave.module.PhaseFlightSubmodule;

@EventBusSubscriber(modid = AE2LightningTech.MODID, value = Dist.CLIENT)
public final class ClientPhaseFlightHandler {
    private ClientPhaseFlightHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.START) {
            return;
        }
        var minecraft = Minecraft.getInstance();
        if (minecraft.player == null || event.player != minecraft.player) {
            return;
        }

        var player = minecraft.player;
        if (isClientPhaseActive()) {
            PhaseFlightSubmodule.applyClientPhaseFlightState(player);
            PhaseFlightSubmodule.applyTransientPhaseState(player);
            return;
        }

        if (PhaseFlightSubmodule.hasTransientPhaseState(player)) {
            PhaseFlightSubmodule.clearTransientPhaseState(player);
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        CelestweaveArmorState.clearClientActiveCache();
        if (event.getPlayer() != null && PhaseFlightSubmodule.hasTransientPhaseState(event.getPlayer())) {
            PhaseFlightSubmodule.clearTransientPhaseState(event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(ClientPlayerNetworkEvent.Clone event) {
        CelestweaveArmorState.clearClientActiveCache();
        if (PhaseFlightSubmodule.hasTransientPhaseState(event.getOldPlayer())) {
            PhaseFlightSubmodule.clearTransientPhaseState(event.getOldPlayer());
        }
        if (PhaseFlightSubmodule.hasTransientPhaseState(event.getNewPlayer())) {
            PhaseFlightSubmodule.clearTransientPhaseState(event.getNewPlayer());
        }
    }

    private static boolean isClientPhaseActive() {
        return ArmorPhaseFlightRules.clientPhaseStateActive(
                CelestweaveArmorState.isAnyClientPhaseFlightActive(),
                true,
                true,
                true,
                AE2LTCommonConfig.overloadArmorPhaseFlightEnabled());
    }
}
