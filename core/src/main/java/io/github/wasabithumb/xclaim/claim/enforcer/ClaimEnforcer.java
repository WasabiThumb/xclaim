package io.github.wasabithumb.xclaim.claim.enforcer;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.flags.ClaimFlag;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Lang;
import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.platform.event.PlatformListener;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public abstract class ClaimEnforcer implements PlatformListener {

    protected final ClaimManager manager;

    protected ClaimEnforcer(@NotNull ClaimManager manager) {
        this.manager = manager;
    }

    //

    protected final @NotNull XClaim runtime() {
        return this.manager.runtime();
    }

    protected final @NotNull Lang lang() {
        return this.runtime().lang();
    }

    protected final @NotNull Platform platform() {
        return this.runtime().platform();
    }

    public final void register() {
        this.platform().events().register(this);
    }

    public final void unregister() {
        this.platform().events().unregister(this);
    }

    @ApiStatus.OverrideOnly
    protected abstract boolean permits(@NotNull Claim claim, @NotNull PlatformUser user);

    @Contract("_, null -> false")
    protected boolean isNotPermitted(@NotNull Claim claim, PlatformUser user) {
        if (user == null) return false;
        if (this.permits(claim, user)) {
            return false;
        } else {
            user.sendMessage(this.lang().get(I18N.PERM_HANDLER_STD_ERROR));
            return true;
        }
    }

    protected @Nullable Claim claimAt(@NotNull ChunkReference chunk) {
        return this.runtime().claims().getByChunk(chunk);
    }

    protected @Nullable Claim claimAt(@NotNull PlatformLocation location) {
        return this.claimAt(ChunkReference.of(location));
    }

    //

    public static abstract class ForPermission extends ClaimEnforcer {

        protected ForPermission(@NotNull ClaimManager manager) {
            super(manager);
        }

        @Override
        protected boolean permits(@NotNull Claim claim, @NotNull PlatformUser user) {
            return claim.checkPermission(user, this.permission());
        }

        @ApiStatus.OverrideOnly
        protected abstract @NotNull Permission permission();

    }

    public static abstract class ForFlag extends ClaimEnforcer {

        protected ForFlag(@NotNull ClaimManager manager) {
            super(manager);
        }

        @Override
        protected boolean permits(@NotNull Claim claim, @NotNull PlatformUser user) {
            return claim.getFlags().contains(this.flag());
        }

        @ApiStatus.OverrideOnly
        protected abstract @NotNull ClaimFlag flag();

    }

}
