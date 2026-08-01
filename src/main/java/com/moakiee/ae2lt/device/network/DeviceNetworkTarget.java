package com.moakiee.ae2lt.device.network;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Stores the 1.21 wireless-link component payload in 1.20.1 item NBT. */
public final class DeviceNetworkTarget {
    private static final String TAG_DIMENSION = "ae2lt:wireless_link_dimension";
    private static final String TAG_POSITION = "ae2lt:wireless_link_position";

    private DeviceNetworkTarget() {
    }

    public static @Nullable GlobalPos get(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null
                || !tag.contains(TAG_DIMENSION, Tag.TAG_STRING)
                || !tag.contains(TAG_POSITION, Tag.TAG_LONG)) {
            return null;
        }
        ResourceLocation dimensionId = ResourceLocation.tryParse(tag.getString(TAG_DIMENSION));
        if (dimensionId == null) {
            return null;
        }
        return GlobalPos.of(
                ResourceKey.create(Registries.DIMENSION, dimensionId),
                BlockPos.of(tag.getLong(TAG_POSITION)));
    }

    public static void set(ItemStack stack, GlobalPos target) {
        var tag = stack.getOrCreateTag();
        tag.putString(TAG_DIMENSION, target.dimension().location().toString());
        tag.putLong(TAG_POSITION, target.pos().asLong());
    }

    public static void clear(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null) {
            return;
        }
        tag.remove(TAG_DIMENSION);
        tag.remove(TAG_POSITION);
        if (tag.isEmpty()) {
            stack.setTag(null);
        }
    }
}
