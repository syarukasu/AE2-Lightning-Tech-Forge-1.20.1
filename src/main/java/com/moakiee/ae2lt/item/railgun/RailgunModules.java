package com.moakiee.ae2lt.item.railgun;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * Persisted per-stack railgun module configuration. Each slot maps to one module
 * item; lists allow up to 2 of compute/acceleration. Empty slots are missing entries.
 *
 * <p>Stored as ItemStack so each module instance can carry its own NBT/components in
 * the future without schema migration.
 *
 * <p>All CODEC fields use {@code optionalFieldOf} with defaults so that old save data
 * missing a field (e.g. the removed {@code resonance}) deserialises gracefully.
 */
public record RailgunModules(
        ItemStack core,
        List<ItemStack> compute,
        List<ItemStack> acceleration,
        ItemStack energy) {

    public static final int MAX_COMPUTE = 2;
    public static final int MAX_ACCELERATION = 2;

    public static final RailgunModules EMPTY = new RailgunModules(
            ItemStack.EMPTY,
            List.of(),
            List.of(),
            ItemStack.EMPTY);

    public static final Codec<RailgunModules> CODEC = RecordCodecBuilder.create(b -> b.group(
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("core", ItemStack.EMPTY).forGetter(RailgunModules::core),
            ItemStack.OPTIONAL_CODEC.listOf().optionalFieldOf("compute", List.of()).forGetter(RailgunModules::compute),
            ItemStack.OPTIONAL_CODEC.listOf().optionalFieldOf("acceleration", List.of()).forGetter(RailgunModules::acceleration),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("energy", ItemStack.EMPTY).forGetter(RailgunModules::energy))
            .apply(b, RailgunModules::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RailgunModules> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, RailgunModules::core,
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), RailgunModules::compute,
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), RailgunModules::acceleration,
            ItemStack.OPTIONAL_STREAM_CODEC, RailgunModules::energy,
            RailgunModules::new);

    public boolean hasCore() {
        return !core.isEmpty();
    }

    public boolean hasEnergy() {
        return !energy.isEmpty();
    }

    public int computeCount() {
        int n = 0;
        for (ItemStack s : compute) {
            if (!s.isEmpty()) n++;
        }
        return Math.min(n, MAX_COMPUTE);
    }

    public int accelerationCount() {
        int n = 0;
        for (ItemStack s : acceleration) {
            if (!s.isEmpty()) n++;
        }
        return Math.min(n, MAX_ACCELERATION);
    }
}
