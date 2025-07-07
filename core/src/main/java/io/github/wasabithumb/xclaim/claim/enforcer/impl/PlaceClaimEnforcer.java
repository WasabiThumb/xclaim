package io.github.wasabithumb.xclaim.claim.enforcer.impl;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformEntityPlaceEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public abstract sealed class PlaceClaimEnforcer extends ClaimEnforcer.ForPermission {

    public PlaceClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    protected abstract boolean isApplicable(@NotNull PlatformEntityPlaceEvent event);

    //

    @PlatformEventHandler
    public void onPlace(@NotNull PlatformEntityPlaceEvent event) {
        if (!this.isApplicable(event)) return;
        Claim claim = this.claimAt(event.entity().location());
        if (claim == null) return;
        if (this.isNotPermitted(claim, event.player())) {
            event.setCancelled(true);
        }
    }

    //

    public static final class Entities extends PlaceClaimEnforcer {

        public Entities(@NotNull ClaimManager manager) {
            super(manager);
        }

        @Override
        protected boolean isApplicable(@NotNull PlatformEntityPlaceEvent event) {
            return !event.isVehicle();
        }

        @Override
        protected @NotNull Permission permission() {
            return Permission.ENT_PLACE;
        }

    }

    public static final class Vehicles extends PlaceClaimEnforcer {

        public Vehicles(@NotNull ClaimManager manager) {
            super(manager);
        }

        @Override
        protected boolean isApplicable(@NotNull PlatformEntityPlaceEvent event) {
            return event.isVehicle();
        }

        @Override
        protected @NotNull Permission permission() {
            return Permission.VEHICLE_PLACE;
        }

    }

}
