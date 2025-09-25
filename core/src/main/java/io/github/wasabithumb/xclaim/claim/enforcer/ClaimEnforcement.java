package io.github.wasabithumb.xclaim.claim.enforcer;

import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.impl.flag.FireproofClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.enforcer.impl.flag.NoPvpClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.enforcer.impl.flag.WaterproofClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.enforcer.impl.perm.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public final class ClaimEnforcement {

    private static final List<Class<? extends ClaimEnforcer>> IMPLS = List.of(
            // Permissions
            BuildClaimEnforcer.class,
            BreakClaimEnforcer.class,
            EnterClaimEnforcer.class,
            UseStorageClaimEnforcer.class,
            UseRedstoneClaimEnforcer.class,
            UseFixturesClaimEnforcer.class,
            UseDoorsClaimEnforcer.class,
            UseOtherClaimEnforcer.class,
            PlaceClaimEnforcer.Entities.class,
            PlaceClaimEnforcer.Vehicles.class,
            IgniteClaimEnforcer.class,
            ExplodeClaimEnforcer.class,
            DamageClaimEnforcer.Friendly.class,
            DamageClaimEnforcer.Hostile.class,
            DamageClaimEnforcer.Vehicle.class,
            DamageClaimEnforcer.Other.class,
            DropClaimEnforcer.class,

            // Flags
            NoPvpClaimEnforcer.class,
            FireproofClaimEnforcer.class,
            WaterproofClaimEnforcer.class
    );

    //

    private final ClaimManager manager;
    private final List<ClaimEnforcer> enforcers;
    private boolean enabled;

    public ClaimEnforcement(@NotNull ClaimManager manager) {
        this.manager = manager;
        this.enforcers = new ArrayList<>();
        this.enabled = false;
    }

    public synchronized boolean isEnabled() {
        return this.enabled;
    }

    public synchronized void setEnabled(boolean enabled) {
        if (enabled == this.enabled) return;
        if (enabled) {
            this.enable();
        } else {
            this.disable();
        }
        this.enabled = enabled;
    }

    private void enable() {
        ClaimEnforcer next;
        for (Class<? extends ClaimEnforcer> clazz : IMPLS) {
            next = this.create(clazz);
            next.register();
            this.enforcers.add(next);
        }
    }

    private void disable() {
        for (ClaimEnforcer enforcer : this.enforcers) {
            enforcer.unregister();
        }
        this.enforcers.clear();
    }

    private @NotNull ClaimEnforcer create(@NotNull Class<? extends ClaimEnforcer> clazz) {
        try {
            Constructor<? extends ClaimEnforcer> con = clazz.getConstructor(ClaimManager.class);
            return con.newInstance(this.manager);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalStateException("Failed to instantiate claim enforcer (" + clazz.getName() + ")");
        }
    }

}
