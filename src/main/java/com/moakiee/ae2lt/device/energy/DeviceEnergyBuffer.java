package com.moakiee.ae2lt.device.energy;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEKey;

/**
 * Unified energy buffer interface for overload devices.
 *
 * <p>Implementations:
 * <ul>
 *   <li>{@link NetworkBoundEnergyBuffer} — railgun, pulls AE from a bound ME network</li>
 *   <li>{@link SelfContainedEnergyBuffer} — armor, drains an installed LightningCell</li>
 * </ul>
 *
 * <p>The two are not interchangeable; the interface exists so device-level code can
 * speak about "consume N energy" without branching on the concrete energy source.
 */
public interface DeviceEnergyBuffer {

    long stored(ItemStack stack);

    long capacity(ItemStack stack);

    boolean tryConsume(ItemStack stack, ServerPlayer player, long amount);

    void refill(ItemStack stack, ServerPlayer player);

    /** Optional: typed extraction (HV/EHV ammo, fluids, ...). Default: unsupported. */
    default boolean tryConsumeKey(ItemStack stack, ServerPlayer player, AEKey key, long amount) {
        return false;
    }
}
