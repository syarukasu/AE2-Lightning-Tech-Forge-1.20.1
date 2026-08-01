package com.moakiee.ae2lt.registry;

import com.moakiee.ae2lt.AE2LightningTech;
import com.moakiee.ae2lt.entity.OverloadTntEntity;
import com.moakiee.ae2lt.entity.FloatingMatterEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, AE2LightningTech.MODID);

    public static final RegistryObject<EntityType<OverloadTntEntity>> OVERLOAD_TNT =
            ENTITY_TYPES.register(
                    "overload_tnt",
                    () -> EntityType.Builder.<OverloadTntEntity>of(OverloadTntEntity::new, MobCategory.MISC)
                            .sized(0.98F, 0.98F)
                            .fireImmune()
                            .clientTrackingRange(10)
                            .updateInterval(10)
                            .build("overload_tnt"));

    public static final RegistryObject<EntityType<FloatingMatterEntity>> FLOATING_MATTER =
            ENTITY_TYPES.register(
                    "floating_matter",
                    () -> EntityType.Builder.<FloatingMatterEntity>of(FloatingMatterEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(6)
                            .updateInterval(20)
                            .build("floating_matter"));

    private ModEntities() {
    }
}

