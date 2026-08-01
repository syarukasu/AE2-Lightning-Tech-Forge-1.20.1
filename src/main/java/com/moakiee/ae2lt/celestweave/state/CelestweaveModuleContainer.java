package com.moakiee.ae2lt.celestweave.state;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Immutable per-stack persistent state for one Celestweave armor piece. Backs a
 * data component, so it is auto-synced to clients and saved with the stack.
 * Mutations return a new instance (Mekanism-style), which keeps callers from
 * aliasing the stored map and forces a {@code stack.set} to re-sync.
 *
 * <p>{@code energyModuleCapacityFe} empty means "never computed" (legacy stacks);
 * present (even 0) means the cache is valid.
 */
public record CelestweaveModuleContainer(
        Optional<UUID> armorId,
        List<ItemStack> modules,
        Map<String, Boolean> toggles,
        Map<String, CompoundTag> submoduleData,
        Optional<Long> energyModuleCapacityFe) {

    public static final CelestweaveModuleContainer EMPTY = new CelestweaveModuleContainer(
            Optional.empty(), List.of(), Map.of(), Map.of(), Optional.empty());

    public CelestweaveModuleContainer {
        modules = copyModules(modules);
        toggles = toggles == null ? Map.of() : Map.copyOf(toggles);
        submoduleData = copySubmoduleData(submoduleData);
    }

    @Override
    public List<ItemStack> modules() {
        return copyModules(modules);
    }

    @Override
    public Map<String, CompoundTag> submoduleData() {
        return copySubmoduleData(submoduleData);
    }

    public CelestweaveModuleContainer withArmorId(UUID id) {
        return new CelestweaveModuleContainer(Optional.ofNullable(id), modules, toggles, submoduleData, energyModuleCapacityFe);
    }

    /** Replace the module list and the (re-derived) capacity cache together; they always change as a unit. */
    public CelestweaveModuleContainer withModules(List<ItemStack> newModules, Optional<Long> capacityFe) {
        return new CelestweaveModuleContainer(armorId, newModules, toggles, submoduleData, capacityFe);
    }

    public CelestweaveModuleContainer withToggles(Map<String, Boolean> newToggles) {
        return new CelestweaveModuleContainer(armorId, modules, newToggles, submoduleData, energyModuleCapacityFe);
    }

    public CelestweaveModuleContainer withSubmoduleData(Map<String, CompoundTag> newData) {
        return new CelestweaveModuleContainer(armorId, modules, toggles, newData, energyModuleCapacityFe);
    }

    public CelestweaveModuleContainer withCapacity(Optional<Long> capacityFe) {
        return new CelestweaveModuleContainer(armorId, modules, toggles, submoduleData, capacityFe);
    }

    private static List<ItemStack> copyModules(List<ItemStack> modules) {
        if (modules == null || modules.isEmpty()) {
            return List.of();
        }
        return modules.stream()
                .filter(stack -> stack != null && !stack.isEmpty())
                .map(ItemStack::copy)
                .toList();
    }

    private static Map<String, CompoundTag> copySubmoduleData(Map<String, CompoundTag> data) {
        if (data == null || data.isEmpty()) {
            return Map.of();
        }
        Map<String, CompoundTag> copy = new LinkedHashMap<>();
        for (var entry : data.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank() || entry.getValue() == null) {
                continue;
            }
            copy.put(entry.getKey(), entry.getValue().copy());
        }
        return Map.copyOf(copy);
    }
}
