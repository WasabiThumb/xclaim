package codes.wasabi.xclaim.gui2.spec.impl.derived;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.event.XClaimEvent;
import codes.wasabi.xclaim.api.event.XClaimGrantUserPermissionEvent;
import codes.wasabi.xclaim.api.event.XClaimRevokeUserPermissionEvent;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.spec.GuiSpec;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
import codes.wasabi.xclaim.gui2.spec.impl.PermissionListGuiSpec;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumSet;

public final class IndividualPermissionListGuiSpec extends PermissionListGuiSpec {

    private final OfflinePlayer subject;
    private final EnumSet<Permission> granted;
    public IndividualPermissionListGuiSpec(@NotNull Claim claim, @NotNull OfflinePlayer subject) {
        super(claim);
        this.subject = subject;

        final EnumSet<Permission> granted = claim.getUserPermissions().get(XCPlayer.of(subject));
        if (granted == null) {
            this.granted = EnumSet.noneOf(Permission.class);
        } else {
            this.granted = EnumSet.copyOf(granted);
        }
    }

    @Override
    protected @NotNull ItemStack populatePermission(@NotNull Permission perm) {
        final boolean value = this.granted.contains(perm);
        final Component text = XClaim.lang.getComponent(value ? "gui-perm-enabled" : "gui-perm-disabled");
        final TextColor tc = (value ? NamedTextColor.GREEN : NamedTextColor.RED);
        final Material mat = (value ? Platform.get().getLimeToken() : Platform.get().getRedToken());
        return DisplayItem.create(
                mat,
                Component.text(perm.getPrintName()).color(tc),
                Collections.singletonList(text.color(NamedTextColor.GRAY))
        );
    }

    @Override
    protected @NotNull GuiAction onClickPermission(@NotNull GuiInstance instance, @NotNull Permission permission) {
        final boolean value = !this.granted.contains(permission);
        if (value == this.claim.getUserPermission(this.subject, permission)) {
            // De-sync
            if (value) {
                this.granted.add(permission);
            } else {
                this.granted.remove(permission);
            }
            return GuiAction.repopulate();
        }

        if (!XClaimEvent.dispatch(value ?
                new XClaimGrantUserPermissionEvent(
                        instance.player(),
                        this.claim,
                        permission,
                        XCPlayer.of(this.subject)
                ) :
                new XClaimRevokeUserPermissionEvent(
                        instance.player(),
                        this.claim,
                        permission,
                        XCPlayer.of(this.subject)
                )
        )) return GuiAction.exit();

        if (value) {
            this.granted.add(permission);
        } else {
            this.granted.remove(permission);
        }
        this.claim.setUserPermission(this.subject, permission, value);
        return GuiAction.repopulate();
    }

    @Override
    protected @NotNull GuiSpec exitDestination() {
        return GuiSpecs.permissiblePlayerList(this.claim);
    }

}
