package com.moakiee.ae2lt.registry;

import com.moakiee.ae2lt.AE2LightningTech;
import com.moakiee.ae2lt.effect.ElectromagneticParalysisEffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMobEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AE2LightningTech.MODID);

    public static final RegistryObject<ElectromagneticParalysisEffect>
            ELECTROMAGNETIC_PARALYSIS = EFFECTS.register(
                    "electromagnetic_paralysis",
                    () -> {
                        var effect = new ElectromagneticParalysisEffect();
                        effect.addAttributeModifier(
                                Attributes.MOVEMENT_SPEED,
                                "b5b07af0-2e99-4f16-b66d-c2f69a694a35",
                                -0.75D,
                                AttributeModifier.Operation.MULTIPLY_TOTAL);
                        return effect;
                    });

    private ModMobEffects() {}
}
