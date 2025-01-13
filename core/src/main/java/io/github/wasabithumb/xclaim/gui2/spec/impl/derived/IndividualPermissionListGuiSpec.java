package io.github.wasabithumb.xclaim.gui2.spec.impl.derived;

import io.github.wasabithumb.xclaim.api.enums.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui2.GuiInstance;
import io.github.wasabithumb.xclaim.gui2.action.GuiAction;
import io.github.wasabithumb.xclaim.gui2.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui2.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.gui2.spec.impl.PermissionListGuiSpec;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import io.github.wasabithumb.xclaim.util.ColorTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class IndividualPermissionListGuiSpec extends PermissionListGuiSpec {

    private final PlatformUser subject;
    private final Set<Permission> granted;
    public IndividualPermissionListGuiSpec(@NotNull Claim claim, @NotNull PlatformUser subject) {
        super(claim);
        this.subject = subject;
        this.granted = claim.getUserPermissions(subject);
    }

    @Override
    protected @NotNull PlatformItem populatePermission(@NotNull GuiInstance instance, @NotNull Permission perm) {
        final boolean value = this.granted.contains(perm);
        final String text = instance.runtime().lang(value ? "gui-perm-enabled" : "gui-perm-disabled");
        final ColorTag col = value ? ColorTag.GRAY : ColorTag.RED;
        final PlatformMaterial mat = value ? NamedPlatformMaterial.LIME_DYE : NamedPlatformMaterial.RED_DYE;
        return DisplayItem.format(
                instance.platform().createItem(mat),
                perm.getPrintName(instance.runtime().lang()),
                col,
                ColorTag.GRAY.format(text)
        );
    }

    @Override
    protected @NotNull GuiAction onClickPermission(@NotNull GuiInstance instance, @NotNull Permission permission) {
        final boolean value = !this.granted.contains(permission);
        if (value == this.claim.getUserPermissions(this.subject).contains(permission)) {
            // De-sync
            if (value) {
                this.granted.add(permission);
            } else {
                this.granted.remove(permission);
            }
            return GuiAction.repopulate();
        }

        if (value) {
            this.granted.add(permission);
        } else {
            this.granted.remove(permission);
        }

        // TODO: Handle failure better
        boolean success = this.claim.modifyPermissions(instance.player())
                .setUserPermission(this.subject, permission, value)
                .commit()
                .isSuccess();

        return success ? GuiAction.repopulate() : GuiAction.exit();
    }

    @Override
    protected @NotNull GuiSpec exitDestination() {
        return GuiSpecs.permissiblePlayerList(this.claim);
    }

}
