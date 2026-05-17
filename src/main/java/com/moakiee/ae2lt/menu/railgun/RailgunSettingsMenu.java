package com.moakiee.ae2lt.menu.railgun;

import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;

import com.moakiee.ae2lt.AE2LightningTech;
import com.moakiee.ae2lt.config.AE2LTCommonConfig;
import com.moakiee.ae2lt.item.railgun.RailgunModuleEntries;
import com.moakiee.ae2lt.item.railgun.RailgunSettings;
import com.moakiee.ae2lt.logic.railgun.RailgunBinding;
import com.moakiee.ae2lt.logic.railgun.RailgunEnergyBuffer;

public class RailgunSettingsMenu extends AEBaseMenu {

    public static final MenuType<RailgunSettingsMenu> TYPE = MenuTypeBuilder
            .create(RailgunSettingsMenu::new, RailgunHost.class)
            .buildUnregistered(ResourceLocation.fromNamespaceAndPath(
                    AE2LightningTech.MODID, "railgun_settings"));

    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 104;
    private static final int HOTBAR_X = 8;
    private static final int HOTBAR_Y = 162;
    private static final int SLOT_SPACING = 18;

    @GuiSync(0)
    public long bufferStored;
    @GuiSync(1)
    public long bufferCapacity;
    @GuiSync(2)
    public String boundDimensionLabel = "";
    @GuiSync(3)
    public boolean terrainDestruction;
    @GuiSync(4)
    public boolean pvpLock;
    @GuiSync(5)
    public int coreInstalled;
    @GuiSync(6)
    public int computeCount;
    @GuiSync(7)
    public int accelCount;
    @GuiSync(8)
    public int overloadExecInstalled;
    @GuiSync(9)
    public boolean aoeEnabled;
    @GuiSync(10)
    public boolean terrainDestructionAllowed;

    private final RailgunHost host;

    public RailgunSettingsMenu(int id, Inventory playerInventory, RailgunHost host) {
        super(TYPE, id, playerInventory, host);
        this.host = host;

        addPlayerInventorySlots(playerInventory);

        registerClientAction("toggleTerrain", this::toggleTerrain);
        registerClientAction("togglePvpLock", this::togglePvpLock);
        registerClientAction("toggleAoe", this::toggleAoe);
        updateSnapshot();
    }

    public RailgunHost host() {
        return host;
    }

    public void clientToggleTerrain() {
        sendClientAction("toggleTerrain");
    }

    public void clientTogglePvpLock() {
        sendClientAction("togglePvpLock");
    }

    public void clientToggleAoe() {
        sendClientAction("toggleAoe");
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            updateSnapshot();
        }
        super.broadcastChanges();
    }

    public boolean isBound() {
        return !boundDimensionLabel.isBlank();
    }

    public String moduleSummary() {
        String core = coreInstalled != 0 ? "Core yes" : "Core no";
        String exec = overloadExecInstalled != 0 ? "OE yes" : "OE no";
        return core + " / Compute x" + computeCount + " / Accel x" + accelCount + " / " + exec;
    }

    private void toggleTerrain() {
        if (!isServerSide()) {
            return;
        }
        if (!AE2LTCommonConfig.railgunTerrainDestructionEnabled()) {
            updateSnapshot();
            return;
        }
        RailgunSettings current = host.getSettings();
        host.setSettings(current.withTerrain(!current.terrainDestruction()));
        updateSnapshot();
    }

    private void togglePvpLock() {
        if (!isServerSide()) {
            return;
        }
        RailgunSettings current = host.getSettings();
        host.setSettings(current.withPvpLock(!current.pvpLock()));
        updateSnapshot();
    }

    private void toggleAoe() {
        if (!isServerSide()) {
            return;
        }
        RailgunSettings current = host.getSettings();
        host.setSettings(current.withAoeEnabled(!current.aoeEnabled()));
        updateSnapshot();
    }

    private void updateSnapshot() {
        ItemStack stack = host.getStack();
        bufferStored = RailgunEnergyBuffer.read(stack);
        bufferCapacity = RailgunEnergyBuffer.capacity();

        GlobalPos boundPos = RailgunBinding.getBoundPos(stack);
        boundDimensionLabel = boundPos == null ? "" : boundPos.dimension().location().toString();

        RailgunSettings settings = host.getSettings();
        terrainDestructionAllowed = AE2LTCommonConfig.railgunTerrainDestructionEnabled();
        terrainDestruction = terrainDestructionAllowed && settings.terrainDestruction();
        pvpLock = settings.pvpLock();
        aoeEnabled = settings.aoeEnabled();

        RailgunModuleEntries modules = host.getModules();
        coreInstalled = modules.hasCore() ? 1 : 0;
        computeCount = modules.computeCount();
        accelCount = modules.accelerationCount();
        overloadExecInstalled = modules.hasOverloadExecution() ? 1 : 0;
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9,
                                PLAYER_INV_X + col * SLOT_SPACING,
                                PLAYER_INV_Y + row * SLOT_SPACING),
                        SlotSemantics.PLAYER_INVENTORY);
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col,
                            HOTBAR_X + col * SLOT_SPACING, HOTBAR_Y),
                    SlotSemantics.PLAYER_HOTBAR);
        }
    }
}
