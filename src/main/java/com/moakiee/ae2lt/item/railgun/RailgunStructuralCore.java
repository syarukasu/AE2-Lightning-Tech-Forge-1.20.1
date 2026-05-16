package com.moakiee.ae2lt.item.railgun;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import com.moakiee.ae2lt.registry.ModDataComponents;

public final class RailgunStructuralCore {
    private RailgunStructuralCore() {
    }

    public static ItemStack getCore(ItemStack railgun) {
        if (railgun == null || railgun.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return railgun
                .getOrDefault(ModDataComponents.RAILGUN_STRUCTURAL_CORE.get(), ItemStack.EMPTY)
                .copyWithCount(1);
    }

    public static void setCore(ItemStack railgun, ItemStack core) {
        if (railgun == null || railgun.isEmpty()) {
            return;
        }
        if (core == null || core.isEmpty()) {
            railgun.remove(ModDataComponents.RAILGUN_STRUCTURAL_CORE.get());
            return;
        }
        railgun.set(ModDataComponents.RAILGUN_STRUCTURAL_CORE.get(), core.copyWithCount(1));
    }

    public static ItemStack removeCore(ItemStack railgun, int amount) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        var existing = getCore(railgun);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }
        setCore(railgun, ItemStack.EMPTY);
        return existing;
    }

    public static boolean hasCore(ItemStack railgun) {
        return baseOverloadBudget(railgun) > 0;
    }

    public static int baseOverloadBudget(ItemStack railgun) {
        return baseOverloadBudgetForCore(getCore(railgun));
    }

    public static int baseOverloadBudgetForCore(ItemStack core) {
        if (core == null || core.isEmpty()) {
            return 0;
        }
        var id = BuiltInRegistries.ITEM.getKey(core.getItem());
        return RailgunStructuralCoreRules.baseOverloadBudgetForItemId(id.toString());
    }

    public static boolean canInstallCore(ItemStack railgun, ItemStack candidateCore) {
        if (candidateCore == null || candidateCore.isEmpty()) {
            return true;
        }
        int candidateBase = baseOverloadBudgetForCore(candidateCore);
        int installedIdle = RailgunModuleStorage.INSTANCE.currentIdleOverload(railgun);
        return installedIdle <= candidateBase;
    }
}
