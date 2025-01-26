package io.github.wasabithumb.xclaim.claim.transaction;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.ClaimMutationContext;
import io.github.wasabithumb.xclaim.claim.data.ClaimData;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;

public abstract class ClaimTransaction {

    protected final XClaim runtime;
    protected final ClaimManager manager;
    protected final Claim claim;
    protected final ClaimData data;
    protected final PlatformUser user;
    protected boolean silent;
    protected boolean valid;

    public ClaimTransaction(@NotNull ClaimMutationContext context) {
        this.runtime = context.runtime();
        this.manager = context.manager();
        this.claim = context.claim();
        this.data = context.data();
        this.user = context.user();
        this.silent = false;
        this.valid = true;
    }

    protected final void message(@NotNull Translatable key) {
        if (this.valid && !this.silent)
            this.user.sendMessage(this.runtime.lang(key));
    }

    protected final boolean checkPermission(@NotNull Permission permission) {
        return this.claim.checkPermission(this.user, permission);
    }

    protected final boolean manageCheck() {
        if (this.checkPermission(Permission.MANAGE)) return false;
        this.valid = false;
        this.message(I18N.PERM_HANDLER_STD_ERROR);
        return true;
    }

    public @NotNull ClaimTransactionResult commit() {
        if (this.valid) this.onCommit();
        if (this.valid) {
            this.manager.commit(this.claim);
            return ClaimTransactionResult.success(this.claim);
        } else {
            return ClaimTransactionResult.error();
        }
    }

    /**
     * Mutates the claim data appropriately, or sets valid to false if commit may not proceed.
     */
    protected abstract void onCommit();

}
