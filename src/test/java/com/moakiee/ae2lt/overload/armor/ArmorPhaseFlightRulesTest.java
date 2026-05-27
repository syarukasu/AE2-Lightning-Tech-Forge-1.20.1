package com.moakiee.ae2lt.overload.armor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class ArmorPhaseFlightRulesTest {

    @Test
    void activeStateSyncsOnlyWhenItChanges() {
        assertTrue(ArmorPhaseFlightRules.shouldSyncClientActiveState(true, true));
        assertTrue(ArmorPhaseFlightRules.shouldSyncClientActiveState(false, true));
        assertFalse(ArmorPhaseFlightRules.shouldSyncClientActiveState(true, false));
        assertFalse(ArmorPhaseFlightRules.shouldSyncClientActiveState(false, false));
    }
}
