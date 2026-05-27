package com.moakiee.ae2lt.item;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import com.moakiee.ae2lt.device.capability.DeviceCapability;
import com.moakiee.ae2lt.device.module.ModuleTooltip;
import com.moakiee.ae2lt.overload.armor.ArmorOverloadRules;
import com.moakiee.ae2lt.overload.armor.ArmorPart;
import com.moakiee.ae2lt.overload.armor.module.OverloadArmorSubmodule;
import com.moakiee.ae2lt.overload.armor.module.OverloadArmorSubmoduleItem;
import com.moakiee.ae2lt.overload.armor.module.UndyingSubmodule;

public final class UndyingSubmoduleItem extends Item implements OverloadArmorSubmoduleItem {

    private static final UndyingSubmodule INSTANCE = UndyingSubmodule.INSTANCE;

    public UndyingSubmoduleItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void collectSubmodules(ItemStack stack, Consumer<OverloadArmorSubmodule> output) {
        output.accept(INSTANCE);
    }

    @Override
    public ArmorPart armorPart() {
        return ArmorPart.CHEST;
    }

    @Override
    public List<DeviceCapability> capabilities(ItemStack stack) {
        return List.of(
                new DeviceCapability.LastStandTuning(
                        ArmorOverloadRules.UNDYING_TRIGGER_COST_FE,
                        ArmorOverloadRules.UNDYING_COOLDOWN_TICKS,
                        ArmorOverloadRules.UNDYING_COMBO_WINDOW_TICKS),
                new DeviceCapability.PassiveDrain(ArmorOverloadRules.UNDYING_PASSIVE_DRAIN_FE));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.ae2lt.module_undying.tooltip")
                .withStyle(ChatFormatting.GRAY));
        ModuleTooltip.appendInstallInfo(this, tooltip);
    }
}
