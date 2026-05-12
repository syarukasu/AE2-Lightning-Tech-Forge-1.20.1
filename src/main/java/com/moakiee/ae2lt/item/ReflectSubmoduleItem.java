package com.moakiee.ae2lt.item;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import com.moakiee.ae2lt.device.capability.DeviceCapability;
import com.moakiee.ae2lt.overload.armor.module.OverloadArmorFeatureCatalog;
import com.moakiee.ae2lt.overload.armor.module.OverloadArmorSubmodule;
import com.moakiee.ae2lt.overload.armor.module.OverloadArmorSubmoduleItem;
import com.moakiee.ae2lt.overload.armor.module.ReflectSubmodule;

public final class ReflectSubmoduleItem extends Item implements OverloadArmorSubmoduleItem {

    private static final ReflectSubmodule INSTANCE = ReflectSubmodule.INSTANCE;

    static {
        OverloadArmorFeatureCatalog.registerSubmodule(INSTANCE);
    }

    public ReflectSubmoduleItem(Properties properties) {
        super(properties.stacksTo(64));
    }

    @Override
    public void collectSubmodules(ItemStack stack, Consumer<OverloadArmorSubmodule> output) {
        output.accept(INSTANCE);
    }

    @Override
    public List<DeviceCapability> capabilities(ItemStack stack) {
        // 25% reflect per module instance; additive with multiple reflect modules.
        return List.of(new DeviceCapability.DamageMitigation(0.25D, 0.0D));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.ae2lt.armor_submodule_reflect.tooltip")
                .withStyle(ChatFormatting.GRAY));
    }
}
