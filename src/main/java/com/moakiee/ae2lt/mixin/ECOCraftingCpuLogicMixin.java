package com.moakiee.ae2lt.mixin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.CraftingLink;
import appeng.crafting.inv.ListCraftingInventory;

import com.moakiee.ae2lt.overload.cpu.InsertContext;
import com.moakiee.ae2lt.overload.cpu.OverloadClaimResult;
import com.moakiee.ae2lt.overload.cpu.OverloadCpuStateManager;
import com.moakiee.ae2lt.overload.cpu.OverloadPatternReference;
import com.moakiee.ae2lt.overload.pattern.OverloadedProviderOnlyPatternDetails;
import com.moakiee.ae2lt.util.MixinReflectionSupport;

/**
 * Ports AE2LT's overload-output claim tracking to Neo ECO AE 20.3.x.
 * Neo ECO's 1.20.1 job and elapsed-time classes are private implementation
 * details, so the same state transitions are applied through guarded reflection.
 */
@Pseudo
@Mixin(targets = "cn.dancingsnow.neoecoae.api.me.ECOCraftingCPULogic", remap = false)
public abstract class ECOCraftingCpuLogicMixin {
    @Unique
    private static final @Nullable Class<?> AE2LT_ECO_LOGIC_CLASS =
            MixinReflectionSupport.findClassSafe("cn.dancingsnow.neoecoae.api.me.ECOCraftingCPULogic");
    @Unique
    private static final @Nullable Class<?> AE2LT_ECO_JOB_CLASS =
            MixinReflectionSupport.findClassSafe("cn.dancingsnow.neoecoae.api.me.ExecutingCraftingJob");
    @Unique
    private static final @Nullable Class<?> AE2LT_ECO_TRACKER_CLASS =
            MixinReflectionSupport.findClassSafe("cn.dancingsnow.neoecoae.api.me.ElapsedTimeTracker");

    @Unique
    private static final @Nullable Field AE2LT_ECO_JOB_FIELD =
            MixinReflectionSupport.findDeclaredFieldSafe(AE2LT_ECO_LOGIC_CLASS, "job");
    @Unique
    private static final @Nullable Field AE2LT_ECO_INVENTORY_FIELD =
            MixinReflectionSupport.findDeclaredFieldSafe(AE2LT_ECO_LOGIC_CLASS, "inventory");
    @Unique
    private static final @Nullable Field AE2LT_ECO_CPU_FIELD =
            MixinReflectionSupport.findDeclaredFieldSafe(AE2LT_ECO_LOGIC_CLASS, "cpu");
    @Unique
    private static final @Nullable Method AE2LT_ECO_FINISH_JOB_METHOD =
            MixinReflectionSupport.findDeclaredMethodSafe(AE2LT_ECO_LOGIC_CLASS, "finishJob", boolean.class);
    @Unique
    private static final @Nullable Method AE2LT_ECO_POST_CHANGE_METHOD =
            MixinReflectionSupport.findDeclaredMethodSafe(AE2LT_ECO_LOGIC_CLASS, "postChange", AEKey.class);

    @Unique
    private static final @Nullable Field AE2LT_ECO_WAITING_FOR_FIELD =
            MixinReflectionSupport.findDeclaredFieldSafe(AE2LT_ECO_JOB_CLASS, "waitingFor");
    @Unique
    private static final @Nullable Field AE2LT_ECO_TIME_TRACKER_FIELD =
            MixinReflectionSupport.findDeclaredFieldSafe(AE2LT_ECO_JOB_CLASS, "timeTracker");
    @Unique
    private static final @Nullable Field AE2LT_ECO_FINAL_OUTPUT_FIELD =
            MixinReflectionSupport.findDeclaredFieldSafe(AE2LT_ECO_JOB_CLASS, "finalOutput");
    @Unique
    private static final @Nullable Field AE2LT_ECO_REMAINING_AMOUNT_FIELD =
            MixinReflectionSupport.findDeclaredFieldSafe(AE2LT_ECO_JOB_CLASS, "remainingAmount");
    @Unique
    private static final @Nullable Field AE2LT_ECO_LINK_FIELD =
            MixinReflectionSupport.findDeclaredFieldSafe(AE2LT_ECO_JOB_CLASS, "link");
    @Unique
    private static final @Nullable Method AE2LT_ECO_DECREMENT_ITEMS_METHOD =
            MixinReflectionSupport.findDeclaredMethodSafe(
                    AE2LT_ECO_TRACKER_CLASS, "decrementItems", long.class, AEKeyType.class);

    @Unique
    private static final boolean AE2LT_ECO_AVAILABLE = AE2LT_ECO_LOGIC_CLASS != null
            && AE2LT_ECO_JOB_CLASS != null
            && AE2LT_ECO_TRACKER_CLASS != null
            && AE2LT_ECO_JOB_FIELD != null
            && AE2LT_ECO_INVENTORY_FIELD != null
            && AE2LT_ECO_CPU_FIELD != null
            && AE2LT_ECO_FINISH_JOB_METHOD != null
            && AE2LT_ECO_POST_CHANGE_METHOD != null
            && AE2LT_ECO_WAITING_FOR_FIELD != null
            && AE2LT_ECO_TIME_TRACKER_FIELD != null
            && AE2LT_ECO_FINAL_OUTPUT_FIELD != null
            && AE2LT_ECO_REMAINING_AMOUNT_FIELD != null
            && AE2LT_ECO_LINK_FIELD != null
            && AE2LT_ECO_DECREMENT_ITEMS_METHOD != null;

    @Unique
    private @Nullable InsertContext ae2lt$insertContext;

    @Inject(method = "insert", at = @At("HEAD"))
    private void ae2lt$beginInsertContext(AEKey what, long amount, Actionable mode,
            CallbackInfoReturnable<Long> cir) {
        if (AE2LT_ECO_AVAILABLE) {
            this.ae2lt$insertContext = new InsertContext(what, amount, mode);
        }
    }

    @WrapOperation(
            method = "insert",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/crafting/inv/ListCraftingInventory;extract(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;)J",
                    ordinal = 0),
            remap = false)
    private long ae2lt$captureStrictWaitingMatch(ListCraftingInventory waitingFor, AEKey what,
            long amount, Actionable mode, Operation<Long> original) {
        long strictMatched = original.call(waitingFor, what, amount, mode);
        if (AE2LT_ECO_AVAILABLE && mode == Actionable.SIMULATE && this.ae2lt$insertContext != null) {
            this.ae2lt$insertContext.setStrictMatched(strictMatched);
        }
        return strictMatched;
    }

    @Inject(method = "insert", at = @At("RETURN"), cancellable = true)
    private void ae2lt$claimOverloadRemainder(AEKey what, long amount, Actionable mode,
            CallbackInfoReturnable<Long> cir) {
        if (!AE2LT_ECO_AVAILABLE) {
            return;
        }
        var context = this.ae2lt$insertContext;
        this.ae2lt$insertContext = null;
        if (context == null || context.getRequestedAmount() <= 0) {
            return;
        }
        long remainder = Math.max(0L, context.getRequestedAmount() - context.getStrictMatched());
        if (remainder <= 0 || !OverloadCpuStateManager.INSTANCE.hasAnyPending(this)) {
            return;
        }
        var claims = OverloadCpuStateManager.INSTANCE.claim(this, what, remainder, mode);
        if (!claims.claimedAnything()) {
            return;
        }
        if (mode == Actionable.MODULATE) {
            ae2lt$deductClaimedWaitingFor(claims);
            long accepted = ae2lt$applyInventoryClaims(what, claims)
                    + ae2lt$applyRequesterClaims(what, claims);
            var cpu = ae2lt$getCpu();
            if (cpu != null) {
                ((ECOCraftingCpuAccessor) cpu).invokeMarkDirty();
            }
            cir.setReturnValue(cir.getReturnValue() + accepted);
        } else {
            cir.setReturnValue(cir.getReturnValue() + claims.claimedAmount());
        }
    }

    @WrapOperation(
            // Neo ECO AE 20.3.x performs the ordinary provider push in this slow-path method.
            method = "tryPushSlowPattern",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/api/networking/crafting/ICraftingProvider;pushPattern(Lappeng/api/crafting/IPatternDetails;[Lappeng/api/stacks/KeyCounter;)Z"),
            remap = false)
    private boolean ae2lt$registerExpectedOutputs(ICraftingProvider provider, IPatternDetails details,
            KeyCounter[] inputHolder, Operation<Boolean> original) {
        if (!AE2LT_ECO_AVAILABLE) {
            return original.call(provider, details, inputHolder);
        }
        OverloadPatternReference patternReference = null;
        if (details instanceof OverloadedProviderOnlyPatternDetails overloadDetails) {
            patternReference = new OverloadPatternReference(
                    overloadDetails.overloadPatternIdentity(),
                    overloadDetails.overloadPatternDetailsView().sourcePattern());
            if (OverloadCpuStateManager.INSTANCE.hasAmbiguousOutputRegistration(
                    this, patternReference, overloadDetails.overloadPatternDetailsView())) {
                return false;
            }
        }
        boolean pushed = original.call(provider, details, inputHolder);
        Object job = ae2lt$getJob();
        if (pushed && details instanceof OverloadedProviderOnlyPatternDetails overloadDetails && job != null) {
            GenericStack finalOutput = ae2lt$getFinalOutput(job);
            AEKey finalOutputKey = finalOutput != null ? finalOutput.what() : null;
            CraftingLink link = ae2lt$getLink(job);
            if (link != null) {
                UUID craftingId = link.getCraftingID();
                OverloadCpuStateManager.INSTANCE.registerExpectedOutputs(
                        this,
                        craftingId,
                        patternReference != null ? patternReference : new OverloadPatternReference(
                                overloadDetails.overloadPatternIdentity(),
                                overloadDetails.overloadPatternDetailsView().sourcePattern()),
                        overloadDetails.overloadPatternDetailsView(),
                        details.getOutputs(),
                        finalOutputKey,
                        1L);
            }
        }
        return pushed;
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void ae2lt$writeOverloadState(CompoundTag data, HolderLookup.Provider registries,
            CallbackInfo ci) {
        if (!AE2LT_ECO_AVAILABLE) {
            return;
        }
        CompoundTag overloadState = OverloadCpuStateManager.INSTANCE.writeToTag(this);
        if (overloadState != null) {
            data.put("ae2ltOverloadState", overloadState);
        } else {
            data.remove("ae2ltOverloadState");
        }
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void ae2lt$readOverloadState(CompoundTag data, HolderLookup.Provider registries,
            CallbackInfo ci) {
        if (!AE2LT_ECO_AVAILABLE) {
            return;
        }
        OverloadCpuStateManager.INSTANCE.clear(this);
        Object job = ae2lt$getJob();
        if (job != null && data.contains("ae2ltOverloadState", CompoundTag.TAG_COMPOUND)) {
            CraftingLink link = ae2lt$getLink(job);
            if (link != null) {
                OverloadCpuStateManager.INSTANCE.readFromTag(
                        this, link.getCraftingID(), data.getCompound("ae2ltOverloadState"));
            }
        }
    }

    @Inject(method = "finishJob", at = @At("HEAD"))
    private void ae2lt$clearOverloadState(boolean success, CallbackInfo ci) {
        if (AE2LT_ECO_AVAILABLE) {
            OverloadCpuStateManager.INSTANCE.clear(this);
        }
    }

    @Unique
    private long ae2lt$applyInventoryClaims(AEKey incoming, OverloadClaimResult claims) {
        long claimed = claims.claimedForInventory();
        Object job = ae2lt$getJob();
        if (claimed <= 0 || job == null) {
            return 0;
        }
        ae2lt$decrementJobItems(job, claimed, incoming.getType());
        ListCraftingInventory inventory = ae2lt$getInventory();
        if (inventory != null) {
            inventory.insert(incoming, claimed, Actionable.MODULATE);
        }
        return claimed;
    }

    @Unique
    private long ae2lt$applyRequesterClaims(AEKey incoming, OverloadClaimResult claims) {
        long claimed = claims.claimedForRequester();
        Object job = ae2lt$getJob();
        if (claimed <= 0 || job == null) {
            return 0;
        }
        ae2lt$decrementJobItems(job, claimed, incoming.getType());
        CraftingLink link = ae2lt$getLink(job);
        long inserted = link != null ? link.insert(incoming, claimed, Actionable.MODULATE) : 0;
        ae2lt$invokePostChange(incoming);
        long remaining = Math.max(0L, ae2lt$getRemainingAmount(job) - claimed);
        ae2lt$setRemainingAmount(job, remaining);
        if (remaining <= 0) {
            ae2lt$invokeFinishJob(true);
        }
        return inserted;
    }

    @Unique
    private void ae2lt$deductClaimedWaitingFor(OverloadClaimResult claims) {
        Object job = ae2lt$getJob();
        ListCraftingInventory waitingFor = job != null ? ae2lt$getWaitingFor(job) : null;
        if (waitingFor == null) {
            return;
        }
        for (var claim : claims.claims()) {
            waitingFor.extract(claim.exactExpectedKey(), claim.claimedAmount(), Actionable.MODULATE);
        }
    }

    @Unique
    private @Nullable Object ae2lt$getJob() {
        return MixinReflectionSupport.getFieldValueSafe(AE2LT_ECO_JOB_FIELD, this);
    }

    @Unique
    private @Nullable ListCraftingInventory ae2lt$getInventory() {
        Object value = MixinReflectionSupport.getFieldValueSafe(AE2LT_ECO_INVENTORY_FIELD, this);
        return value instanceof ListCraftingInventory inventory ? inventory : null;
    }

    @Unique
    private @Nullable Object ae2lt$getCpu() {
        return MixinReflectionSupport.getFieldValueSafe(AE2LT_ECO_CPU_FIELD, this);
    }

    @Unique
    private @Nullable ListCraftingInventory ae2lt$getWaitingFor(Object job) {
        Object value = MixinReflectionSupport.getFieldValueSafe(AE2LT_ECO_WAITING_FOR_FIELD, job);
        return value instanceof ListCraftingInventory inventory ? inventory : null;
    }

    @Unique
    private @Nullable GenericStack ae2lt$getFinalOutput(Object job) {
        Object value = MixinReflectionSupport.getFieldValueSafe(AE2LT_ECO_FINAL_OUTPUT_FIELD, job);
        return value instanceof GenericStack stack ? stack : null;
    }

    @Unique
    private long ae2lt$getRemainingAmount(Object job) {
        return MixinReflectionSupport.getLongFieldSafe(AE2LT_ECO_REMAINING_AMOUNT_FIELD, job, 0L);
    }

    @Unique
    private void ae2lt$setRemainingAmount(Object job, long amount) {
        MixinReflectionSupport.setLongFieldSafe(
                AE2LT_ECO_REMAINING_AMOUNT_FIELD, job, amount, "set ECO job remaining amount");
    }

    @Unique
    private @Nullable CraftingLink ae2lt$getLink(Object job) {
        Object value = MixinReflectionSupport.getFieldValueSafe(AE2LT_ECO_LINK_FIELD, job);
        return value instanceof CraftingLink link ? link : null;
    }

    @Unique
    private void ae2lt$decrementJobItems(Object job, long amount, AEKeyType keyType) {
        Object tracker = MixinReflectionSupport.getFieldValueSafe(AE2LT_ECO_TIME_TRACKER_FIELD, job);
        if (tracker != null) {
            MixinReflectionSupport.invokeMethodSafe(
                    AE2LT_ECO_DECREMENT_ITEMS_METHOD, tracker, "decrement ECO job items", amount, keyType);
        }
    }

    @Unique
    private void ae2lt$invokeFinishJob(boolean success) {
        MixinReflectionSupport.invokeMethodSafe(
                AE2LT_ECO_FINISH_JOB_METHOD, this, "finish ECO job", success);
    }

    @Unique
    private void ae2lt$invokePostChange(AEKey what) {
        MixinReflectionSupport.invokeMethodSafe(
                AE2LT_ECO_POST_CHANGE_METHOD, this, "ECO post change", what);
    }
}
