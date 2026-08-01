package com.moakiee.ae2lt.machine.firmament.recipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import com.moakiee.ae2lt.machine.firmament.FirmamentConversionInventory;
import com.moakiee.ae2lt.registry.ModRecipeTypes;
import com.moakiee.ae2lt.util.RecipeManagerByTypeAccess;

public final class FirmamentConversionRecipeService {
    private static final Comparator<FirmamentConversionRecipe> RECIPE_ORDER = Comparator
            .comparingInt(FirmamentConversionRecipe::priority)
            .reversed()
            .thenComparing(Comparator.comparingInt(
                    (FirmamentConversionRecipe recipe) -> recipe.inputs().size()).reversed())
            .thenComparing(Comparator.comparingInt(
                    FirmamentConversionRecipe::totalInputCount).reversed())
            .thenComparing(recipe -> recipe.getId().toString());

    private FirmamentConversionRecipeService() {
    }

    public static Optional<FirmamentConversionRecipeCandidate> findFirstProcessable(
            Level level,
            FirmamentConversionInventory inventory) {
        if (level == null) {
            return Optional.empty();
        }

        FirmamentConversionRecipeInput input = FirmamentConversionRecipeInput.fromInventory(inventory);
        if (input.isEmpty()) {
            return Optional.empty();
        }

        List<FirmamentConversionRecipe> recipes =
                new ArrayList<>(level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.FIRMAMENT_CONVERSION_TYPE.get()));
        recipes.sort(RECIPE_ORDER);

        for (FirmamentConversionRecipe recipe : recipes) {
            Optional<FirmamentConversionRecipeMatch> match = recipe.planMatch(input);
            if (match.isEmpty()) {
                continue;
            }
            if (!canAcceptOutputs(inventory, recipe.getResultStacks())) {
                continue;
            }
            return Optional.of(new FirmamentConversionRecipeCandidate(recipe, match.get()));
        }

        return Optional.empty();
    }

    public static Optional<FirmamentConversionRecipe> findRecipeById(Level level, ResourceLocation recipeId) {
        if (level == null || recipeId == null) {
            return Optional.empty();
        }

        return RecipeManagerByTypeAccess.findById(
                level.getRecipeManager(),
                ModRecipeTypes.FIRMAMENT_CONVERSION_TYPE.get(),
                recipeId);
    }

    public static Optional<FirmamentConversionRecipeCandidate> findLockedRecipeMatch(
            Level level,
            FirmamentConversionInventory inventory,
            FirmamentConversionLockedRecipe lockedRecipe) {
        if (level == null || lockedRecipe == null) {
            return Optional.empty();
        }

        Optional<FirmamentConversionRecipe> recipe = findRecipeById(level, lockedRecipe.recipeId());
        if (recipe.isEmpty() || recipe.get().processTime() != lockedRecipe.processTime()) {
            return Optional.empty();
        }

        FirmamentConversionRecipeInput input = FirmamentConversionRecipeInput.fromInventory(inventory);
        if (input.isEmpty()) {
            return Optional.empty();
        }

        Optional<FirmamentConversionRecipeMatch> match = recipe.get().planMatch(input);
        if (match.isEmpty()) {
            return Optional.empty();
        }
        if (!canAcceptOutputs(inventory, recipe.get().getResultStacks())) {
            return Optional.empty();
        }

        return Optional.of(new FirmamentConversionRecipeCandidate(recipe.get(), match.get()));
    }

    public static boolean canAcceptOutput(FirmamentConversionInventory inventory, ItemStack result) {
        return inventory.canAcceptRecipeOutput(result);
    }

    public static boolean canAcceptOutputs(FirmamentConversionInventory inventory, List<ItemStack> results) {
        return inventory.canAcceptRecipeOutputs(results);
    }
}
