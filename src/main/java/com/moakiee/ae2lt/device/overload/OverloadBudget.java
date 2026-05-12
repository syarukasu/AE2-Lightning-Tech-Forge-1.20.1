package com.moakiee.ae2lt.device.overload;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Per-device overload budget tracking. The armor implementation is the primary
 * consumer; the railgun gets a no-op wrapper (cap = MAX_VALUE) until D-something
 * enables the budget on weapons.
 */
public interface OverloadBudget {

    int currentLoad(ItemStack stack);

    int budgetCap(ItemStack stack);

    LockState lockState(ItemStack stack);

    void tick(ItemStack stack, Player player);
}
