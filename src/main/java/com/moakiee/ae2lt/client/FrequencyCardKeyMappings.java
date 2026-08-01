package com.moakiee.ae2lt.client;

import org.lwjgl.glfw.GLFW;

import com.moakiee.ae2lt.AE2LightningTech;
import com.moakiee.ae2lt.network.ToggleFrequencyCardAutoConnectPacket;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import com.moakiee.ae2lt.network.NetworkInit;

@EventBusSubscriber(modid = AE2LightningTech.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FrequencyCardKeyMappings {
    private static final String CATEGORY = "key.categories.ae2lt";

    private static final KeyMapping TOGGLE_AUTO_CONNECT = new KeyMapping(
            "key.ae2lt.toggle_frequency_card_auto_connect",
            GLFW.GLFW_KEY_UNKNOWN,
            CATEGORY);

    private FrequencyCardKeyMappings() {
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_AUTO_CONNECT);
    }

    @EventBusSubscriber(modid = AE2LightningTech.MODID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class RuntimeHandler {
        private RuntimeHandler() {
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent event) {
            if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
                return;
            }
            while (TOGGLE_AUTO_CONNECT.consumeClick()) {
                NetworkInit.sendToServer(ToggleFrequencyCardAutoConnectPacket.forPreferredCard());
            }
        }
    }
}
