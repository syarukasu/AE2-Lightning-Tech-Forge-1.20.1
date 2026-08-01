package com.moakiee.ae2lt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Pseudo accessor for Neo ECO AE's crafting CPU. */
@Pseudo
@Mixin(targets = "cn.dancingsnow.neoecoae.api.me.ECOCraftingCPU", remap = false)
public interface ECOCraftingCpuAccessor {
    @Invoker("markDirty")
    void invokeMarkDirty();
}
