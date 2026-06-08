package com.moakiee.ae2lt.client.ae2wtlib;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.fml.ModList;

import appeng.client.gui.AEBaseScreen;

import com.moakiee.ae2lt.client.FrequencyBindingClient;
import com.moakiee.ae2lt.mixin.client.AEBaseScreenAccessor;

/**
 * Adds a "configure frequency card" button to ae2wtlib wireless terminal
 * screens. Called from an {@link AEBaseScreen} init mixin before AE2 populates
 * the screen widgets, so the button is registered through the native toolbar
 * path instead of being appended after init events.
 *
 * <p>The button is appended to the bottom of AE2's native left vertical toolbar
 * (the terminal already stacks its own buttons from the top), styled like the
 * toolbar frequency button on the mod's machines instead of floating over the
 * GUI. Only the menu type's namespace is inspected, so this class does not need
 * to reference ae2wtlib types and is safe to keep registered unconditionally.</p>
 */
public final class FrequencyTerminalButton {

    private FrequencyTerminalButton() {
    }

    public static boolean shouldInject(AEBaseScreen<?> screen) {
        if (!ModList.get().isLoaded("ae2wtlib")) {
            return false;
        }

        var type = screen.getMenu().getType();
        var key = BuiltInRegistries.MENU.getKey(type);
        return key != null && key.getNamespace().equals("ae2wtlib");
    }

    public static void addToToolbar(AEBaseScreen<?> screen) {
        // Append to the native left toolbar. VerticalButtonBar lays out its button
        // list top-to-bottom every frame, so add() == bottom of the column.
        // AEBaseScreen.init() will populate the toolbar into renderables after
        // this hook runs.
        var button = FrequencyBindingClient.createCardToolbarButton();
        ((AEBaseScreenAccessor) screen).ae2lt$getVerticalToolbar().add(button);
    }
}
