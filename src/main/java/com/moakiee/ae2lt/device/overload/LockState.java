package com.moakiee.ae2lt.device.overload;

/**
 * Overload lock state for a device. Mirrors the existing armor lock semantics so
 * Phase 4 can migrate without changing player-visible behavior.
 */
public sealed interface LockState {

    LockState UNLOCKED = new Unlocked();

    static LockState debt(int ticks) {
        return new Debt(ticks);
    }

    static LockState locked(int ticks) {
        return new Locked(ticks);
    }

    record Unlocked() implements LockState {}

    record Debt(int ticksRemaining) implements LockState {}

    record Locked(int ticksRemaining) implements LockState {}
}
