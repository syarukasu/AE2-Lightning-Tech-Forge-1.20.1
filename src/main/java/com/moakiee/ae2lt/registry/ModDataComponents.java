package com.moakiee.ae2lt.registry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.moakiee.ae2lt.celestweave.state.CelestweaveModuleContainer;
import com.moakiee.ae2lt.item.railgun.RailgunModuleEntries;
import com.moakiee.ae2lt.item.railgun.RailgunSettings;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

/**
 * Forge 1.20.1 persistence bridge for the stack data components introduced by
 * upstream 1.1.4. Item stack NBT is synchronized and persisted by vanilla, so
 * it preserves the same per-stack behavior without relying on the 1.21 data
 * component registry.
 */
public final class ModDataComponents {
    private static final String ROOT = "AE2LT114";
    private static final String RAILGUN_MODULE_ENTRIES = "RailgunModuleEntries";
    private static final String RAILGUN_STRUCTURAL_CORE = "RailgunStructuralCore";
    private static final String RAILGUN_SETTINGS = "RailgunSettings";
    private static final String RAILGUN_CHARGE_TICKS = "RailgunChargeTicks";
    private static final String RAILGUN_ENERGY_BUFFER = "RailgunEnergyBuffer";
    private static final String CELESTWEAVE_STRUCTURAL_CORE = "CelestweaveStructuralCore";
    private static final String CELESTWEAVE_ENERGY_BUFFER = "CelestweaveEnergyBuffer";
    private static final String CELESTWEAVE_MODULES = "CelestweaveModules";
    private static final String CELESTWEAVE_MODULES_POWERED = "CelestweaveModulesPowered";

    private ModDataComponents() {
    }

    public static RailgunModuleEntries getRailgunModuleEntries(ItemStack stack) {
        CompoundTag data = read(stack);
        if (!data.contains(RAILGUN_MODULE_ENTRIES, Tag.TAG_LIST)) {
            return RailgunModuleEntries.EMPTY;
        }
        ListTag list = data.getList(RAILGUN_MODULE_ENTRIES, Tag.TAG_COMPOUND);
        var entries = new ArrayList<ItemStack>(list.size());
        for (int i = 0; i < list.size(); i++) {
            ItemStack entry = ItemStack.of(list.getCompound(i));
            if (!entry.isEmpty()) {
                entries.add(entry);
            }
        }
        return new RailgunModuleEntries(entries);
    }

    public static void setRailgunModuleEntries(ItemStack stack, RailgunModuleEntries entries) {
        if (entries == null || entries.entries().isEmpty()) {
            remove(stack, RAILGUN_MODULE_ENTRIES);
            return;
        }
        ListTag list = new ListTag();
        for (ItemStack entry : entries.entries()) {
            if (!entry.isEmpty()) {
                list.add(entry.save(new CompoundTag()));
            }
        }
        write(stack, data -> data.put(RAILGUN_MODULE_ENTRIES, list));
    }

    public static ItemStack getRailgunStructuralCore(ItemStack stack) {
        return getStoredStack(stack, RAILGUN_STRUCTURAL_CORE);
    }

    public static void setRailgunStructuralCore(ItemStack stack, ItemStack core) {
        setStoredStack(stack, RAILGUN_STRUCTURAL_CORE, core);
    }

    public static RailgunSettings getRailgunSettings(ItemStack stack) {
        CompoundTag data = read(stack);
        if (!data.contains(RAILGUN_SETTINGS, Tag.TAG_COMPOUND)) {
            return RailgunSettings.DEFAULT;
        }
        CompoundTag settings = data.getCompound(RAILGUN_SETTINGS);
        return new RailgunSettings(
                settings.getBoolean("Terrain"),
                settings.getBoolean("Pvp"),
                !settings.contains("Sound", Tag.TAG_BYTE) || settings.getBoolean("Sound"));
    }

    public static void setRailgunSettings(ItemStack stack, RailgunSettings settings) {
        RailgunSettings value = settings == null ? RailgunSettings.DEFAULT : settings;
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Terrain", value.terrainDestruction());
        tag.putBoolean("Pvp", value.pvp());
        tag.putBoolean("Sound", value.soundEnabled());
        write(stack, data -> data.put(RAILGUN_SETTINGS, tag));
    }

    public static long getRailgunChargeTicks(ItemStack stack) {
        return getLong(stack, RAILGUN_CHARGE_TICKS);
    }

    public static void setRailgunChargeTicks(ItemStack stack, long value) {
        setLong(stack, RAILGUN_CHARGE_TICKS, Math.max(0L, value), true);
    }

    public static void removeRailgunChargeTicks(ItemStack stack) {
        remove(stack, RAILGUN_CHARGE_TICKS);
    }

    public static long getRailgunEnergyBuffer(ItemStack stack) {
        return getLong(stack, RAILGUN_ENERGY_BUFFER);
    }

    public static void setRailgunEnergyBuffer(ItemStack stack, long value) {
        setLong(stack, RAILGUN_ENERGY_BUFFER, Math.max(0L, value), false);
    }

    public static ItemStack getCelestweaveStructuralCore(ItemStack stack) {
        return getStoredStack(stack, CELESTWEAVE_STRUCTURAL_CORE);
    }

    public static void setCelestweaveStructuralCore(ItemStack stack, ItemStack core) {
        setStoredStack(stack, CELESTWEAVE_STRUCTURAL_CORE, core);
    }

    public static long getCelestweaveEnergyBuffer(ItemStack stack) {
        return getLong(stack, CELESTWEAVE_ENERGY_BUFFER);
    }

    public static void setCelestweaveEnergyBuffer(ItemStack stack, long value) {
        setLong(stack, CELESTWEAVE_ENERGY_BUFFER, Math.max(0L, value), false);
    }

    public static boolean areCelestweaveModulesPowered(ItemStack stack) {
        CompoundTag data = read(stack);
        return !data.contains(CELESTWEAVE_MODULES_POWERED, Tag.TAG_BYTE)
                || data.getBoolean(CELESTWEAVE_MODULES_POWERED);
    }

    public static void setCelestweaveModulesPowered(ItemStack stack, boolean powered) {
        if (powered) {
            remove(stack, CELESTWEAVE_MODULES_POWERED);
        } else {
            write(stack, data -> data.putBoolean(CELESTWEAVE_MODULES_POWERED, false));
        }
    }

    public static CelestweaveModuleContainer getCelestweaveModules(ItemStack stack) {
        CompoundTag data = read(stack);
        if (!data.contains(CELESTWEAVE_MODULES, Tag.TAG_COMPOUND)) {
            return CelestweaveModuleContainer.EMPTY;
        }
        CompoundTag modulesTag = data.getCompound(CELESTWEAVE_MODULES);

        Optional<UUID> armorId = modulesTag.hasUUID("ArmorId")
                ? Optional.of(modulesTag.getUUID("ArmorId"))
                : Optional.empty();

        ListTag moduleList = modulesTag.getList("Modules", Tag.TAG_COMPOUND);
        var modules = new ArrayList<ItemStack>(moduleList.size());
        for (int i = 0; i < moduleList.size(); i++) {
            ItemStack module = ItemStack.of(moduleList.getCompound(i));
            if (!module.isEmpty()) {
                modules.add(module);
            }
        }

        Map<String, Boolean> toggles = new LinkedHashMap<>();
        CompoundTag toggleTag = modulesTag.getCompound("Toggles");
        for (String key : toggleTag.getAllKeys()) {
            toggles.put(key, toggleTag.getBoolean(key));
        }

        Map<String, CompoundTag> submoduleData = new LinkedHashMap<>();
        CompoundTag submoduleTag = modulesTag.getCompound("SubmoduleData");
        for (String key : submoduleTag.getAllKeys()) {
            if (submoduleTag.contains(key, Tag.TAG_COMPOUND)) {
                submoduleData.put(key, submoduleTag.getCompound(key).copy());
            }
        }

        Optional<Long> capacity = modulesTag.contains("EnergyCapacityFe", Tag.TAG_LONG)
                ? Optional.of(Math.max(0L, modulesTag.getLong("EnergyCapacityFe")))
                : Optional.empty();
        return new CelestweaveModuleContainer(armorId, modules, toggles, submoduleData, capacity);
    }

    public static void setCelestweaveModules(ItemStack stack, CelestweaveModuleContainer container) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        CelestweaveModuleContainer value = container == null ? CelestweaveModuleContainer.EMPTY : container;
        CompoundTag tag = new CompoundTag();
        value.armorId().ifPresent(id -> tag.putUUID("ArmorId", id));

        ListTag modules = new ListTag();
        for (ItemStack module : value.modules()) {
            if (!module.isEmpty()) {
                modules.add(module.save(new CompoundTag()));
            }
        }
        tag.put("Modules", modules);

        CompoundTag toggles = new CompoundTag();
        value.toggles().forEach(toggles::putBoolean);
        tag.put("Toggles", toggles);

        CompoundTag submoduleData = new CompoundTag();
        value.submoduleData().forEach((key, data) -> submoduleData.put(key, data.copy()));
        tag.put("SubmoduleData", submoduleData);
        value.energyModuleCapacityFe().ifPresent(capacity -> tag.putLong("EnergyCapacityFe", Math.max(0L, capacity)));
        write(stack, data -> data.put(CELESTWEAVE_MODULES, tag));
    }

    private static ItemStack getStoredStack(ItemStack stack, String key) {
        CompoundTag data = read(stack);
        if (!data.contains(key, Tag.TAG_COMPOUND)) {
            return ItemStack.EMPTY;
        }
        ItemStack stored = ItemStack.of(data.getCompound(key));
        return stored.isEmpty() ? ItemStack.EMPTY : stored.copyWithCount(1);
    }

    private static void setStoredStack(ItemStack stack, String key, ItemStack value) {
        if (value == null || value.isEmpty()) {
            remove(stack, key);
            return;
        }
        ItemStack stored = value.copyWithCount(1);
        write(stack, data -> data.put(key, stored.save(new CompoundTag())));
    }

    private static long getLong(ItemStack stack, String key) {
        CompoundTag data = read(stack);
        return data.contains(key, Tag.TAG_LONG) ? Math.max(0L, data.getLong(key)) : 0L;
    }

    private static void setLong(ItemStack stack, String key, long value, boolean keepZero) {
        if (!keepZero && value == 0L) {
            remove(stack, key);
            return;
        }
        write(stack, data -> data.putLong(key, value));
    }

    private static CompoundTag read(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getTag() == null) {
            return new CompoundTag();
        }
        return stack.getTag().getCompound(ROOT);
    }

    private static void write(ItemStack stack, java.util.function.Consumer<CompoundTag> writer) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        CompoundTag root = stack.getOrCreateTag();
        CompoundTag data = root.contains(ROOT, Tag.TAG_COMPOUND)
                ? root.getCompound(ROOT)
                : new CompoundTag();
        writer.accept(data);
        if (data.isEmpty()) {
            root.remove(ROOT);
        } else {
            root.put(ROOT, data);
        }
    }

    private static void remove(ItemStack stack, String key) {
        if (stack == null || stack.isEmpty() || stack.getTag() == null) {
            return;
        }
        CompoundTag root = stack.getTag();
        if (!root.contains(ROOT, Tag.TAG_COMPOUND)) {
            return;
        }
        CompoundTag data = root.getCompound(ROOT);
        data.remove(key);
        if (data.isEmpty()) {
            root.remove(ROOT);
        } else {
            root.put(ROOT, data);
        }
    }
}
