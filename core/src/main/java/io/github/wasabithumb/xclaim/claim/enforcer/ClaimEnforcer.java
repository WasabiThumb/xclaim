package io.github.wasabithumb.xclaim.claim.enforcer;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
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
    public ClaimEnforcer(@NotNull ClaimManager manager) {
        this.manager = manager;
    }

    protected final @NotNull XClaim runtime() {
        return this.manager.runtime();
    }

    protected final @NotNull Lang lang() {
        return this.runtime().lang();
    }

    protected final @NotNull Platform platform() {
        return this.runtime().platform();
    }

    protected abstract @NotNull Permission permission();

    public void register() {
        this.onRegister();
        this.platform().events().register(this);
    }

    public void unregister() {
        this.platform().events().unregister(this);
        this.onUnregister();
    }

    protected void onRegister() { }

    protected void onUnregister() { }

    @Contract("_, null -> false")
    protected boolean isNotPermitted(@NotNull Claim claim, PlatformUser user) {
        if (user == null) return false;
        if (claim.checkPermission(user, this.permission())) {
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

}
