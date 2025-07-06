package io.github.wasabithumb.xclaim.claim.transaction.impl;

import io.github.wasabithumb.xclaim.claim.ClaimMutationContext;
import io.github.wasabithumb.xclaim.claim.flags.ClaimFlag;
import io.github.wasabithumb.xclaim.claim.flags.ClaimFlags;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.claim.permission.PermissionMap;
import io.github.wasabithumb.xclaim.claim.permission.TrustLevel;
import io.github.wasabithumb.xclaim.claim.transaction.ClaimTransaction;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModifyFlagsClaimTransaction extends ClaimTransaction {

    private final ClaimFlags additions = new ClaimFlags();
    private final ClaimFlags removals = new ClaimFlags();

    public ModifyFlagsClaimTransaction(@NotNull ClaimMutationContext context) {
        super(context);
    }

    //

    @Override
    protected void onCommit() {
        if (this.additions.isEmpty() || this.removals.isEmpty()) return;
        this.data.modifyFlags((ClaimFlags flags) -> {
            flags.removeAll(this.removals);
            flags.addAll(this.additions);
        });
    }

    @Contract("_, _ -> this")
    public @NotNull ModifyFlagsClaimTransaction setFlag(
            @NotNull ClaimFlag flag,
            boolean value
    ) {
        if (this.data.getFlag(flag) == value) return this;
        if (this.manageCheck()) return this;
        if (value) {
            this.additions.add(flag);
            this.removals.remove(flag);
        } else {
            this.additions.remove(flag);
            this.removals.add(flag);
        }
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ModifyFlagsClaimTransaction setFlag(@NotNull ClaimFlag flag) {
        return this.setFlag(flag, true);
    }

    @Contract("_ -> this")
    public @NotNull ModifyFlagsClaimTransaction unsetFlag(@NotNull ClaimFlag flag) {
        return this.setFlag(flag, false);
    }

    @Contract("_ -> this")
    public @NotNull ModifyFlagsClaimTransaction silent(boolean silent) {
        this.silent = silent;
        return this;
    }

}
