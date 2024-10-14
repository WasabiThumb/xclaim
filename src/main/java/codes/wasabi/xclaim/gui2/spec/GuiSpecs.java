package codes.wasabi.xclaim.gui2.spec;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.gui2.spec.impl.*;
import codes.wasabi.xclaim.gui2.spec.impl.derived.*;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class GuiSpecs {

    // Non-stateful specs

    private static final GuiSpec MAIN = new MainGuiSpec();
    private static final GuiSpec VERSION_INFO = new VersionInfoGuiSpec();
    private static final GuiSpec NEW_CLAIM = new NewClaimGuiSpec();

    @SuppressWarnings("ConfusingMainMethod")
    public static @NotNull GuiSpec main() {
        return MAIN;
    }

    public static @NotNull GuiSpec versionInfo() {
        return VERSION_INFO;
    }

    public static @NotNull GuiSpec newClaim() {
        return NEW_CLAIM;
    }

    // Stateful specs

    public static @NotNull GuiSpec editTrust() {
        return new EditTrustGuiSpec();
    }

    public static @NotNull GuiSpec editChunks() {
        return new EditChunksGuiSpec();
    }

    public static @NotNull GuiSpec renameClaim() {
        return new RenameClaimGuiSpec();
    }

    public static @NotNull GuiSpec editPerms() {
        return new EditPermsGuiSpec();
    }

    public static @NotNull GuiSpec permissionOverview(@NotNull Claim claim) {
        return new PermissionOverviewGuiSpec(claim);
    }

    public static @NotNull GuiSpec globalPermissionList(@NotNull Claim claim) {
        return new GlobalPermissionListGuiSpec(claim);
    }

    public static @NotNull GuiSpec permissionLevels(@NotNull Claim claim, @NotNull Permission permission) {
        return new PermissionLevelsGuiSpec(claim, permission);
    }

    public static @NotNull GuiSpec individualPermissionList(@NotNull Claim claim, @NotNull OfflinePlayer subject) {
        return new IndividualPermissionListGuiSpec(claim, subject);
    }

    public static @NotNull GuiSpec permissiblePlayerList(@NotNull Claim claim) {
        return new PermissiblePlayerListGuiSpec(claim);
    }

    public static @NotNull GuiSpec transferableClaimSelector() {
        return new TransferableClaimSelectorGuiSpec();
    }

    public static @NotNull GuiSpec transferOwner(@NotNull Claim claim, @NotNull OfflinePlayer target) {
        return new TransferOwnerGuiSpec(claim, target);
    }

    public static @NotNull GuiSpec clearAll() {
        return new ClearAllGuiSpec();
    }

    public static @NotNull GuiSpec deletingClaimSelector() {
        return new DeletingClaimSelectorGuiSpec();
    }

}
