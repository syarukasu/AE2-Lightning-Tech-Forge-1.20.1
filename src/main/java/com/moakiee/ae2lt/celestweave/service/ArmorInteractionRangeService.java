package com.moakiee.ae2lt.celestweave.service;

import java.util.List;
import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;

import com.moakiee.ae2lt.AE2LightningTech;
import com.moakiee.ae2lt.device.capability.DeviceCapability;
import com.moakiee.ae2lt.celestweave.module.ReachSubmodule;
import com.moakiee.ae2lt.celestweave.service.ArmorCapabilityCollector.ActiveCapability;

public final class ArmorInteractionRangeService {
    private static final UUID BLOCK_RANGE_MODIFIER_ID =
            UUID.fromString("9a8edc6a-70aa-41e9-9fd5-f7c4439300a1");
    private static final UUID ENTITY_RANGE_MODIFIER_ID =
            UUID.fromString("fe069276-c4e7-438e-a5d3-68d41b4d851c");

    private ArmorInteractionRangeService() {
    }

    public static void tick(ServerPlayer player, List<ActiveCapability> capabilities) {
        double blockBonus = 0.0D;
        double entityBonus = 0.0D;
        for (var active : capabilities) {
            if (!(active.capability() instanceof DeviceCapability.InteractionRange)) {
                continue;
            }
            blockBonus = Math.max(blockBonus, ReachSubmodule.blockBonus(active.armor()));
            entityBonus = Math.max(entityBonus, ReachSubmodule.entityBonus(active.armor()));
        }

        updateModifier(player, ForgeMod.BLOCK_REACH.get(), BLOCK_RANGE_MODIFIER_ID,
                AE2LightningTech.MODID + ".celestweave_reach_extension_block", blockBonus);
        updateModifier(player, ForgeMod.ENTITY_REACH.get(), ENTITY_RANGE_MODIFIER_ID,
                AE2LightningTech.MODID + ".celestweave_reach_extension_entity", entityBonus);
    }

    private static void updateModifier(
            ServerPlayer player,
            Attribute attribute,
            UUID id,
            String name,
            double amount) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }

        AttributeModifier existing = instance.getModifier(id);
        if (amount <= 0.0D) {
            if (existing != null) {
                instance.removeModifier(existing);
            }
            return;
        }

        if (existing != null
                && Math.abs(existing.getAmount() - amount) < 1.0E-6D
                && existing.getOperation() == AttributeModifier.Operation.ADDITION) {
            return;
        }

        if (existing != null) {
            instance.removeModifier(existing);
        }
        instance.addTransientModifier(new AttributeModifier(
                id, name, amount, AttributeModifier.Operation.ADDITION));
    }
}
