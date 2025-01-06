package io.github.wasabithumb.xclaim.claim.transaction.impl;

import io.github.wasabithumb.xclaim.api.enums.Permission;
import io.github.wasabithumb.xclaim.claim.ClaimMutationContext;
import io.github.wasabithumb.xclaim.claim.transaction.ClaimTransaction;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TransferOwnerClaimTransaction extends ClaimTransaction {

    private UUID newOwner = null;
    public TransferOwnerClaimTransaction(@NotNull ClaimMutationContext context) {
        super(context);
    }

    @Override
    protected void onCommit() {
        if (this.newOwner == null) return;
        this.data.setOwner(this.newOwner);
        this.data.setUserPermission(this.newOwner, Permission.MANAGE, true);
    }

    @Contract("_ -> this")
    public @NotNull TransferOwnerClaimTransaction setNewOwner(@NotNull PlatformUser newOwner) {
        if (this.manageCheck()) return this;
        if (!this.user.uuid().equals(this.data.getOwner())) {
            this.valid = false;
            this.langMessage("permHandler-stdError");
        } else {
            this.newOwner = newOwner.uuid();
            this.langMessage("gui-tx-success");
        }
        return this;
    }

    @Contract("_ -> this")
    public @NotNull TransferOwnerClaimTransaction silent(boolean silent) {
        this.silent = silent;
        return this;
    }

}
