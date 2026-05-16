package com.moakiee.ae2lt.item.railgun;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RailgunStructuralCoreTest {
    @Test
    void ultimateOverloadCoreProvidesRailgunBudget() {
        assertEquals(
                128,
                RailgunStructuralCoreRules.baseOverloadBudgetForItemId("ae2lt:ultimate_overload_core"));
    }

    @Test
    void railgunModuleCoreDoesNotProvideStructuralBudget() {
        assertEquals(
                0,
                RailgunStructuralCoreRules.baseOverloadBudgetForItemId("ae2lt:railgun_module_core"));
    }
}
