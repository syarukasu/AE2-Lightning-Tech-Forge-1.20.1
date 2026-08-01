package com.moakiee.ae2lt.machine.firmament.recipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import com.moakiee.ae2lt.machine.firmament.FirmamentConversionInventory;
import com.moakiee.ae2lt.registry.ModRecipeTypes;
import com.moakiee.ae2lt.util.RecipeSerializationHelper;

public final class FirmamentConversionRecipe implements Recipe<FirmamentConversionRecipeInput> {
    private final ResourceLocation id;
    private final int priority;
    private final List<FirmamentConversionIngredient> inputs;
    private final List<ItemStack> results;
    private final int processTime;
    private final int totalInputCount;

    public FirmamentConversionRecipe(
            ResourceLocation id,
            int priority,
            List<FirmamentConversionIngredient> inputs,
            ItemStack result,
            int processTime) {
        this(id, priority, inputs, List.of(result), processTime);
    }

    public FirmamentConversionRecipe(
            ResourceLocation id,
            int priority,
            List<FirmamentConversionIngredient> inputs,
            List<ItemStack> results,
            int processTime) {
        this.id = Objects.requireNonNull(id, "id");
        Objects.requireNonNull(inputs, "inputs");
        Objects.requireNonNull(results, "results");
        if (inputs.isEmpty() || inputs.size() > 3) {
            throw new IllegalArgumentException("inputs must contain 1 to 3 entries");
        }
        if (results.isEmpty() || results.size() > FirmamentConversionInventory.OUTPUT_SLOT_COUNT) {
            throw new IllegalArgumentException("results must contain 1 to 4 entries");
        }
        if (results.stream().anyMatch(ItemStack::isEmpty)) {
            throw new IllegalArgumentException("results cannot contain empty stacks");
        }
        if (processTime <= 0) {
            throw new IllegalArgumentException("processTime must be positive");
        }

        this.priority = priority;
        this.inputs = List.copyOf(inputs);
        this.results = results.stream().map(ItemStack::copy).toList();
        this.processTime = processTime;
        this.totalInputCount = this.inputs.stream().mapToInt(FirmamentConversionIngredient::count).sum();
    }

    public int priority() {
        return priority;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public List<FirmamentConversionIngredient> inputs() {
        return inputs;
    }

    public ItemStack getResultStack() {
        return results.get(0).copy();
    }

    public List<ItemStack> getResultStacks() {
        return results.stream().map(ItemStack::copy).toList();
    }

    public int processTime() {
        return processTime;
    }

    public int totalInputCount() {
        return totalInputCount;
    }

    @Override
    public boolean matches(FirmamentConversionRecipeInput input, Level level) {
        return planMatch(input).isPresent();
    }

    public Optional<FirmamentConversionRecipeMatch> planMatch(FirmamentConversionRecipeInput input) {
        List<FirmamentConversionRecipeInput.SlotStack> slotStacks = input.slotStacks();
        if (slotStacks.isEmpty() || slotStacks.size() > 3) {
            return Optional.empty();
        }

        int[] slotFlexibility = new int[slotStacks.size()];
        List<List<Integer>> rawMatches = new ArrayList<>(inputs.size());

        for (FirmamentConversionIngredient requirement : inputs) {
            List<Integer> matchingSlots = new ArrayList<>();
            int availableCount = 0;

            for (int slotIndex = 0; slotIndex < slotStacks.size(); slotIndex++) {
                var slotStack = slotStacks.get(slotIndex);
                if (!requirement.ingredient().test(slotStack.stack())) {
                    continue;
                }

                matchingSlots.add(slotIndex);
                availableCount += slotStack.stack().getCount();
                slotFlexibility[slotIndex]++;
            }

            if (availableCount < requirement.count()) {
                return Optional.empty();
            }

            rawMatches.add(matchingSlots);
        }

        List<RequirementState> requirements = new ArrayList<>(inputs.size());
        for (int requirementIndex = 0; requirementIndex < inputs.size(); requirementIndex++) {
            FirmamentConversionIngredient requirement = inputs.get(requirementIndex);
            List<Integer> matchingSlots = rawMatches.get(requirementIndex);
            matchingSlots.sort(Comparator
                    .comparingInt((Integer slotIndex) -> slotFlexibility[slotIndex])
                    .thenComparing(Comparator.comparingInt(
                            (Integer slotIndex) -> slotStacks.get(slotIndex).stack().getCount()).reversed()));
            requirements.add(new RequirementState(
                    requirement.count(),
                    matchingSlots.stream().mapToInt(Integer::intValue).toArray()));
        }

        requirements.sort(Comparator
                .comparingInt(RequirementState::matchingSlotCount)
                .thenComparing(Comparator.comparingInt(RequirementState::count).reversed()));

        int[] remainingCounts = slotStacks.stream().mapToInt(slotStack -> slotStack.stack().getCount()).toArray();
        int[] slotConsumptions = new int[3];

        if (!allocateRequirement(0, requirements, slotStacks, remainingCounts, slotConsumptions)) {
            return Optional.empty();
        }

        return Optional.of(new FirmamentConversionRecipeMatch(slotConsumptions));
    }

    @Override
    public ItemStack assemble(FirmamentConversionRecipeInput input, RegistryAccess registries) {
        return getResultStack();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return getResultStack();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (var input : inputs) {
            ingredients.add(input.ingredient());
        }
        return ingredients;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.FIRMAMENT_CONVERSION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.FIRMAMENT_CONVERSION_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean isIncomplete() {
        return inputs.isEmpty()
                || results.isEmpty()
                || results.stream().anyMatch(ItemStack::isEmpty)
                || processTime <= 0
                || inputs.stream().anyMatch(input -> input.ingredient().getItems().length == 0);
    }

    private List<ItemStack> rawResults() {
        return results;
    }

    private boolean allocateRequirement(
            int requirementIndex,
            List<RequirementState> requirements,
            List<FirmamentConversionRecipeInput.SlotStack> slotStacks,
            int[] remainingCounts,
            int[] slotConsumptions) {
        if (requirementIndex >= requirements.size()) {
            return true;
        }

        RequirementState requirement = requirements.get(requirementIndex);
        return allocateAcrossSlots(
                requirementIndex,
                requirements,
                requirement,
                slotStacks,
                0,
                requirement.count(),
                remainingCounts,
                slotConsumptions);
    }

    private boolean allocateAcrossSlots(
            int requirementIndex,
            List<RequirementState> requirements,
            RequirementState requirement,
            List<FirmamentConversionRecipeInput.SlotStack> slotStacks,
            int slotCursor,
            int needed,
            int[] remainingCounts,
            int[] slotConsumptions) {
        if (needed == 0) {
            return allocateRequirement(requirementIndex + 1, requirements, slotStacks, remainingCounts, slotConsumptions);
        }
        if (slotCursor >= requirement.matchingSlots.length) {
            return false;
        }
        if (remainingCapacity(requirement.matchingSlots, slotCursor, remainingCounts) < needed) {
            return false;
        }

        int slotIndex = requirement.matchingSlots[slotCursor];
        int maxTake = Math.min(needed, remainingCounts[slotIndex]);
        int machineSlot = slotStacks.get(slotIndex).slot();

        for (int take = maxTake; take >= 0; take--) {
            if (take > 0) {
                remainingCounts[slotIndex] -= take;
                slotConsumptions[machineSlot] += take;
            }

            if (allocateAcrossSlots(
                    requirementIndex,
                    requirements,
                    requirement,
                    slotStacks,
                    slotCursor + 1,
                    needed - take,
                    remainingCounts,
                    slotConsumptions)) {
                return true;
            }

            if (take > 0) {
                slotConsumptions[machineSlot] -= take;
                remainingCounts[slotIndex] += take;
            }
        }

        return false;
    }

    private int remainingCapacity(int[] matchingSlots, int startIndex, int[] remainingCounts) {
        int total = 0;
        for (int index = startIndex; index < matchingSlots.length; index++) {
            total += remainingCounts[matchingSlots[index]];
        }
        return total;
    }

    private static final class RequirementState {
        private final int count;
        private final int[] matchingSlots;

        private RequirementState(int count, int[] matchingSlots) {
            this.count = count;
            this.matchingSlots = matchingSlots;
        }

        private int count() {
            return count;
        }

        private int matchingSlotCount() {
            return matchingSlots.length;
        }
    }

    public static final class Serializer implements RecipeSerializer<FirmamentConversionRecipe> {
        @Override
        public FirmamentConversionRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            JsonArray inputsJson = GsonHelper.getAsJsonArray(json, "inputs");
            List<FirmamentConversionIngredient> inputs = new ArrayList<>(inputsJson.size());
            for (var element : inputsJson) {
                inputs.add(FirmamentConversionIngredient.fromJson(
                        GsonHelper.convertToJsonObject(element, "inputs[]")));
            }

            JsonArray resultsJson = GsonHelper.getAsJsonArray(json, "results");
            List<ItemStack> results = new ArrayList<>(resultsJson.size());
            for (var element : resultsJson) {
                results.add(RecipeSerializationHelper.itemStackFromJson(
                        GsonHelper.convertToJsonObject(element, "results[]")));
            }

            return new FirmamentConversionRecipe(
                    recipeId,
                    GsonHelper.getAsInt(json, "priority", 0),
                    inputs,
                    results,
                    GsonHelper.getAsInt(json, "processTime"));
        }

        @Override
        public FirmamentConversionRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int inputCount = buffer.readVarInt();
            List<FirmamentConversionIngredient> inputs = new ArrayList<>(inputCount);
            for (int i = 0; i < inputCount; i++) {
                inputs.add(FirmamentConversionIngredient.fromNetwork(buffer));
            }

            int resultCount = buffer.readVarInt();
            List<ItemStack> results = new ArrayList<>(resultCount);
            for (int i = 0; i < resultCount; i++) {
                results.add(buffer.readItem());
            }

            return new FirmamentConversionRecipe(
                    recipeId,
                    buffer.readVarInt(),
                    inputs,
                    results,
                    buffer.readVarInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FirmamentConversionRecipe recipe) {
            buffer.writeVarInt(recipe.inputs().size());
            for (FirmamentConversionIngredient input : recipe.inputs()) {
                input.toNetwork(buffer);
            }
            buffer.writeVarInt(recipe.rawResults().size());
            for (ItemStack result : recipe.rawResults()) {
                buffer.writeItem(result);
            }
            buffer.writeVarInt(recipe.priority());
            buffer.writeVarInt(recipe.processTime());
        }
    }
}
