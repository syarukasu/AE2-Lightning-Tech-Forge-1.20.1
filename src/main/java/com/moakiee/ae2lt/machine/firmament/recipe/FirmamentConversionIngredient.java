package com.moakiee.ae2lt.machine.firmament.recipe;

import java.util.Objects;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public record FirmamentConversionIngredient(Ingredient ingredient, int count) {
    public FirmamentConversionIngredient {
        Objects.requireNonNull(ingredient, "ingredient");
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
    }

    public static FirmamentConversionIngredient fromJson(JsonObject json) {
        if (!json.has("ingredient")) {
            throw new IllegalArgumentException("Missing required field 'ingredient'");
        }
        return new FirmamentConversionIngredient(
                Ingredient.fromJson(json.get("ingredient")),
                GsonHelper.getAsInt(json, "count"));
    }

    public static FirmamentConversionIngredient fromNetwork(FriendlyByteBuf buffer) {
        return new FirmamentConversionIngredient(Ingredient.fromNetwork(buffer), buffer.readVarInt());
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        ingredient.toNetwork(buffer);
        buffer.writeVarInt(count);
    }
}
