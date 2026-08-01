package com.moakiee.ae2lt.menu.railgun;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import com.moakiee.ae2lt.item.railgun.RailgunModuleEntries;
import com.moakiee.ae2lt.item.railgun.RailgunModuleStorage;
import com.moakiee.ae2lt.item.railgun.RailgunSettings;
import com.moakiee.ae2lt.registry.ModDataComponents;

public class RailgunHost extends ItemMenuHost {
    public RailgunHost(Player player, int slot, ItemStack stack) {
        super(player, slot, stack);
    }

    public RailgunModuleEntries getModules() {
        return RailgunModuleStorage.entryData(getItemStack());
    }

    public void setModules(RailgunModuleEntries m) {
        RailgunModuleStorage.setEntries(getItemStack(), m);
    }

    public RailgunSettings getSettings() {
        return ModDataComponents.getRailgunSettings(getItemStack());
    }

    public void setSettings(RailgunSettings s) {
        ModDataComponents.setRailgunSettings(getItemStack(), s);
    }

    public ItemStack getStack() {
        return getItemStack();
    }
}
