package com.moakiee.ae2lt.item.railgun;

import net.minecraft.world.item.ItemStack;

import com.moakiee.ae2lt.registry.ModDataComponents;

public record RailgunSettings(boolean terrainDestruction, boolean pvp, boolean soundEnabled) {

    public static final RailgunSettings DEFAULT = new RailgunSettings(false, false, true);

    public static boolean soundEnabled(ItemStack stack) {
        return stack == null || stack.isEmpty() || ModDataComponents.getRailgunSettings(stack).soundEnabled();
    }

    public RailgunSettings withTerrain(boolean v) {
        return new RailgunSettings(v, this.pvp, this.soundEnabled);
    }

    public RailgunSettings withPvp(boolean v) {
        return new RailgunSettings(this.terrainDestruction, v, this.soundEnabled);
    }

    public RailgunSettings withSound(boolean v) {
        return new RailgunSettings(this.terrainDestruction, this.pvp, v);
    }
}
