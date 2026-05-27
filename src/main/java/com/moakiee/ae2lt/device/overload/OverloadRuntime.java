package com.moakiee.ae2lt.device.overload;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session-local overload runtime for one physical device stack.
 */
public final class OverloadRuntime {
    private static final Map<UUID, OverloadRuntime> RUNTIMES = new ConcurrentHashMap<>();

    private final LoadBucket bucket;
    private final OverloadDynamics dynamics;
    private boolean unpaidEnergy;
    private String pendingDebtReason = "";
    private String currentDebtReason = "";
    private final java.util.ArrayDeque<LoadEvent> recentLoadEvents = new java.util.ArrayDeque<>();

    private OverloadRuntime() {
        this(new LoadBucket(), new OverloadDynamics());
    }

    private OverloadRuntime(LoadBucket bucket, OverloadDynamics dynamics) {
        this.bucket = bucket;
        this.dynamics = dynamics;
    }

    public static OverloadRuntime get(UUID deviceId) {
        return RUNTIMES.computeIfAbsent(deviceId, ignored -> new OverloadRuntime());
    }

    public static OverloadRuntime get(
            UUID deviceId,
            double pulseDecay,
            int pulseMaxTicks,
            double pulseEpsilon,
            int lockTriggerTicks,
            int lockDurationTicks) {
        return RUNTIMES.computeIfAbsent(
                deviceId,
                ignored -> new OverloadRuntime(
                        new LoadBucket(pulseDecay, pulseMaxTicks, pulseEpsilon),
                        new OverloadDynamics(lockTriggerTicks, lockDurationTicks)));
    }

    public static void reset(UUID deviceId) {
        if (deviceId != null) {
            RUNTIMES.remove(deviceId);
        }
    }

    public LoadBucket bucket() {
        return bucket;
    }

    public OverloadDynamics dynamics() {
        return dynamics;
    }

    public LockState tick(int cap) {
        int currentLoad = bucket.tick();
        boolean unpaid = unpaidEnergy;
        String reason = pendingDebtReason;
        unpaidEnergy = false;
        pendingDebtReason = "";

        LockState state = dynamics.tick(currentLoad, cap, unpaid);
        if (dynamics.locked()) {
            currentDebtReason = "locked";
        } else if (dynamics.debtTicks() > 0) {
            currentDebtReason = unpaid ? normalizeDebtReason(reason) : "overloaded";
        } else {
            currentDebtReason = "";
        }
        return state;
    }

    public int currentLoad() {
        return bucket.current();
    }

    public void clearTransientLoad() {
        bucket.clear();
        recentLoadEvents.clear();
        unpaidEnergy = false;
        pendingDebtReason = "";
        if (!dynamics.locked() && dynamics.debtTicks() <= 0) {
            currentDebtReason = "";
        }
    }

    public void addPulse(String key, int load) {
        bucket.addPulse(key, load);
        recordLoadEvent(key, load);
    }

    public void markEnergyUnpaid(String reason) {
        unpaidEnergy = true;
        pendingDebtReason = normalizeDebtReason(reason);
    }

    public String currentDebtReason() {
        return currentDebtReason;
    }

    public List<LoadEvent> recentLoadEvents() {
        return List.copyOf(recentLoadEvents);
    }

    private void recordLoadEvent(String key, int load) {
        if (load <= 0 || key == null || key.isBlank()) {
            return;
        }
        recentLoadEvents.removeIf(event -> event.key().equals(key));
        recentLoadEvents.addFirst(new LoadEvent(key, load));
        while (recentLoadEvents.size() > 3) {
            recentLoadEvents.removeLast();
        }
    }

    private static String normalizeDebtReason(String reason) {
        return reason == null || reason.isBlank() ? "energy" : reason;
    }

    public record LoadEvent(String key, int load) {
    }
}
