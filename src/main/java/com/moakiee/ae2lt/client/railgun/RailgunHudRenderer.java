package com.moakiee.ae2lt.client.railgun;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import com.moakiee.ae2lt.AE2LightningTech;
import com.moakiee.ae2lt.config.RailgunDefaults;
import com.moakiee.ae2lt.item.railgun.ElectromagneticRailgunItem;
import com.moakiee.ae2lt.item.railgun.RailgunChargeTier;
import com.moakiee.ae2lt.item.railgun.RailgunSettings;
import com.moakiee.ae2lt.registry.ModDataComponents;

/**
 * Renders a small charge-progress bar near the crosshair while the player is
 * holding right-click on the railgun.
 *
 * <p>The bar is a 13x19 lightning-bolt icon placed immediately to the right of
 * the crosshair. The empty texture is drawn as the background; the full
 * texture is revealed progressively from the bottom up as charge builds. Tier
 * markers and a compact tier label are shown because acceleration modules can
 * make the charge counter reach higher tiers much faster than wall-clock time
 * suggests.
 */
@EventBusSubscriber(modid = AE2LightningTech.MODID, value = Dist.CLIENT)
public final class RailgunHudRenderer {

    private static final ResourceLocation EMPTY_TEX = ResourceLocation.fromNamespaceAndPath(
            AE2LightningTech.MODID, "textures/gui/hud/lightning_charging_bar.png");
    private static final ResourceLocation FULL_TEX = ResourceLocation.fromNamespaceAndPath(
            AE2LightningTech.MODID, "textures/gui/hud/lightning_charging_bar_full.png");

    private static final int ICON_W = 13;
    private static final int ICON_H = 19;
    /** Horizontal gap between the crosshair center and the icon's left edge. */
    private static final int CROSSHAIR_OFFSET_X = 10;

    private RailgunHudRenderer() {}

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Always-on beam mode tag whenever the player is holding a railgun (either hand).
        renderBeamModeTag(mc, e.getGuiGraphics());

        // Charge-progress bar only while right-click-charging.
        if (!mc.player.isUsingItem()) return;
        ItemStack stack = mc.player.getUseItem();
        if (!(stack.getItem() instanceof ElectromagneticRailgunItem)) return;

        long ticks = stack.getOrDefault(ModDataComponents.RAILGUN_CHARGE_TICKS.get(), 0L);
        int t3 = RailgunDefaults.CHARGE_TICKS_TIER3;
        float progress = Math.min(1.0f, (float) ticks / (float) t3);

        GuiGraphics gfx = e.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        int x = w / 2 + CROSSHAIR_OFFSET_X;
        int y = (h - ICON_H) / 2;

        // Empty bolt as the always-visible background.
        gfx.blit(EMPTY_TEX, x, y, 0, 0, ICON_W, ICON_H, ICON_W, ICON_H);

        // Reveal the lit bolt from the bottom up by sampling the matching
        // bottom slice of the full texture; this keeps pixel alignment exact
        // even at fractional progress values.
        int filledH = Math.round(ICON_H * progress);
        if (filledH > 0) {
            int emptyTop = ICON_H - filledH;
            gfx.blit(FULL_TEX, x, y + emptyTop, 0, emptyTop, ICON_W, filledH, ICON_W, ICON_H);
        }

        renderChargeMarkers(gfx, x, y);
        renderChargeTierLabel(mc, gfx, ticks, x, y);

        ensureUserStillUsing(mc.player, stack);
    }

    private static void renderChargeMarkers(GuiGraphics gfx, int x, int y) {
        drawChargeMarker(gfx, x, y, RailgunDefaults.CHARGE_TICKS_TIER1, 0xFFFFDD55);
        drawChargeMarker(gfx, x, y, RailgunDefaults.CHARGE_TICKS_TIER2, 0xFFFFAA33);
        drawChargeMarker(gfx, x, y, RailgunDefaults.CHARGE_TICKS_TIER3, 0xFFFF55FF);
    }

    private static void drawChargeMarker(GuiGraphics gfx, int x, int y, int threshold, int color) {
        int markerY = y + ICON_H - Math.round(ICON_H * (float) threshold / (float) RailgunDefaults.CHARGE_TICKS_TIER3);
        gfx.fill(x - 1, markerY, x + ICON_W + 1, markerY + 1, color);
    }

    private static void renderChargeTierLabel(Minecraft mc, GuiGraphics gfx, long ticks, int x, int y) {
        RailgunChargeTier tier = RailgunChargeTier.fromTicks(
                ticks,
                RailgunDefaults.CHARGE_TICKS_TIER1,
                RailgunDefaults.CHARGE_TICKS_TIER2,
                RailgunDefaults.CHARGE_TICKS_TIER3);
        Component label = Component.literal(tierLabel(tier));
        Font font = mc.font;
        int labelX = x + ICON_W + 4;
        int labelY = y + (ICON_H - font.lineHeight) / 2;
        int color = switch (tier) {
            case EHV1 -> 0xFFFFDD55;
            case EHV2 -> 0xFFFFAA33;
            case EHV3 -> 0xFFFF55FF;
            default -> 0xFFAAAAAA;
        };

        RenderSystem.enableBlend();
        gfx.fill(labelX - 2, labelY - 1, labelX + font.width(label) + 2, labelY + font.lineHeight, 0x88000000);
        gfx.drawString(font, label, labelX, labelY, color, true);
        RenderSystem.disableBlend();
    }

    private static String tierLabel(RailgunChargeTier tier) {
        return switch (tier) {
            case EHV1 -> "EHv1";
            case EHV2 -> "EHv2";
            case EHV3 -> "EHv3";
            default -> "CHG";
        };
    }

    /**
     * Beam-mode tag drawn just below the crosshair. HV is shown in a cool electric
     * blue, EHV in a hot magenta — easy to scan at a glance without a tooltip.
     * Hidden when the player is in any UI to avoid double-drawing during overlays.
     */
    private static void renderBeamModeTag(Minecraft mc, GuiGraphics gfx) {
        if (mc.screen != null) return;
        Player p = mc.player;
        ItemStack stack = p.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(stack.getItem() instanceof ElectromagneticRailgunItem)) {
            stack = p.getItemInHand(InteractionHand.OFF_HAND);
            if (!(stack.getItem() instanceof ElectromagneticRailgunItem)) return;
        }
        RailgunSettings settings = stack.getOrDefault(
                ModDataComponents.RAILGUN_SETTINGS.get(), RailgunSettings.DEFAULT);
        boolean ehv = settings.beamMode() == RailgunSettings.BeamMode.EHV;
        Component label = Component.translatable("ae2lt.railgun.beam_mode." + settings.beamMode().getSerializedName());
        int color = ehv ? 0xFFFF55FF : 0xFF55FFFF;

        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        Font font = mc.font;
        int textW = font.width(label);
        int x = w / 2 - textW / 2;
        // Below the crosshair (offset chosen to avoid overlapping the hotbar).
        int y = h / 2 + 12;

        RenderSystem.enableBlend();
        // Soft dark backdrop for legibility against bright backgrounds.
        gfx.fill(x - 2, y - 1, x + textW + 2, y + font.lineHeight, 0x88000000);
        gfx.drawString(font, label, x, y, color, true);
        RenderSystem.disableBlend();
    }

    private static void ensureUserStillUsing(LivingEntity user, ItemStack stack) {
        // No-op; placeholder hook for future state validation.
    }
}
