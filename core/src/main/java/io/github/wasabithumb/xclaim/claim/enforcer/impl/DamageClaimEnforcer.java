package io.github.wasabithumb.xclaim.claim.enforcer.impl;

import io.github.wasabithumb.xclaim.api.enums.EntityGroup;
import io.github.wasabithumb.xclaim.api.enums.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformEntityDamagedEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public abstract sealed class DamageClaimEnforcer extends ClaimEnforcer {

    public DamageClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    //

    protected abstract @NotNull EntityGroup group();

    protected boolean inGroup(@NotNull PlatformEntity entity) {
        return entity.isInGroup(this.group());
    }

    //

    @PlatformEventHandler
    public void onDamage(@NotNull PlatformEntityDamagedEvent event) {
        PlatformEntity victim = event.entity();
        if (!this.inGroup(victim)) return;

        PlatformEntity damager = event.damager();
        if (damager == null) return;

        PlatformPlayer ply;
        if (damager instanceof PlatformPlayer) {
            ply = (PlatformPlayer) damager;
        } else {
            ply = damager.sourcePlayer();
            if (ply == null) return;
        }

        Claim claim = this.claimAt(victim.location());
        if (claim == null) return;
        if (this.isNotPermitted(claim, ply)) {
            event.setCancelled(true);
        }
    }

    //

    public static final class Friendly extends DamageClaimEnforcer {

        public Friendly(@NotNull ClaimManager manager) {
            super(manager);
        }

        @Override
        protected @NotNull EntityGroup group() {
            return EntityGroup.FRIENDLY;
        }

        @Override
        protected @NotNull Permission permission() {
            return Permission.ENTITY_DAMAGE_FRIENDLY;
        }

    }

    public static final class Hostile extends DamageClaimEnforcer {

        public Hostile(@NotNull ClaimManager manager) {
            super(manager);
        }

        @Override
        protected @NotNull EntityGroup group() {
            return EntityGroup.HOSTILE;
        }

        @Override
        protected @NotNull Permission permission() {
            return Permission.ENTITY_DAMAGE_HOSTILE;
        }

    }

    public static final class Vehicle extends DamageClaimEnforcer {

        public Vehicle(@NotNull ClaimManager manager) {
            super(manager);
        }

        @Override
        protected @NotNull EntityGroup group() {
            return EntityGroup.VEHICLE;
        }

        @Override
        protected @NotNull Permission permission() {
            return Permission.ENTITY_DAMAGE_VEHICLE;
        }

    }

    public static final class NonLiving extends DamageClaimEnforcer {

        public NonLiving(@NotNull ClaimManager manager) {
            super(manager);
        }

        @Override
        protected @NotNull EntityGroup group() {
            return EntityGroup.NOT_ALIVE;
        }

        @Override
        protected @NotNull Permission permission() {
            return Permission.ENTITY_DAMAGE_NL;
        }

    }

    public static final class Misc extends DamageClaimEnforcer {

        public Misc(@NotNull ClaimManager manager) {
            super(manager);
        }

        @Override
        protected @NotNull EntityGroup group() {
            return EntityGroup.MISC;
        }

        @Override
        protected @NotNull Permission permission() {
            return Permission.ENTITY_DAMAGE_MISC;
        }

    }

}
