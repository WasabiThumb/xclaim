package io.github.wasabithumb.xclaim.claim.transaction.impl;

import io.github.wasabithumb.xclaim.api.enums.Permission;
import io.github.wasabithumb.xclaim.api.enums.TrustLevel;
import io.github.wasabithumb.xclaim.claim.ClaimMutationContext;
import io.github.wasabithumb.xclaim.claim.transaction.ClaimTransaction;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModifyPermissionsClaimTransaction extends ClaimTransaction {

    private final Map<Permission, TrustLevel> globalPermissions = new HashMap<>();
    private final Map<UserPermissionData, Boolean> userPermissions = new HashMap<>();
    public ModifyPermissionsClaimTransaction(@NotNull ClaimMutationContext context) {
        super(context);
    }

    @Override
    protected void onCommit() {
        for (Map.Entry<Permission, TrustLevel> entry : this.globalPermissions.entrySet()) {
            this.data.setGlobalPermission(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<UserPermissionData, Boolean> entry : this.userPermissions.entrySet()) {
            this.data.setUserPermission(
                    entry.getKey().user,
                    entry.getKey().permission,
                    entry.getValue()
            );
        }
    }

    @Contract("_, _ -> this")
    public @NotNull ModifyPermissionsClaimTransaction setGlobalPermission(
            @NotNull Permission permission,
            @NotNull TrustLevel level
    ) {
        if (this.data.getGlobalPermission(permission) == level) return this;
        if (this.manageCheck()) return this;
        this.globalPermissions.put(permission, level);
        return this;
    }

    @Contract("_, _, _ -> this")
    public @NotNull ModifyPermissionsClaimTransaction setUserPermission(
            @NotNull PlatformUser user,
            @NotNull Permission permission,
            boolean value
    ) {
        if (this.data.getUserPermission(user.uuid(), permission) == value) return this;
        if (this.manageCheck()) return this;
        this.userPermissions.put(new UserPermissionData(user.uuid(), permission), value);
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ModifyPermissionsClaimTransaction silent(boolean silent) {
        this.silent = silent;
        return this;
    }

    //

    private record UserPermissionData(
            @NotNull UUID user,
            @NotNull Permission permission
    ) { }

}
