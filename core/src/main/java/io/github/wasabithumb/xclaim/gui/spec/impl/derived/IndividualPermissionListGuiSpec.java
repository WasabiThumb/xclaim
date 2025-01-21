package io.github.wasabithumb.xclaim.gui.spec.impl.derived;

import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.gui.spec.impl.PermissionListGuiSpec;
import io.github.wasabithumb.xclaim.i18n.I18N;
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
        final String text = instance.runtime().lang(value ? I18N.GUI_PERM_ENABLED : I18N.GUI_PERM_DISABLED);
        final ColorTag col = value ? ColorTag.GREEN : ColorTag.RED;
        final PlatformMaterial mat = value ? NamedPlatformMaterial.GREEN_CONCRETE : NamedPlatformMaterial.RED_CONCRETE;
        return DisplayItem.format(
                instance.platform().createItem(mat),
                perm.getPrintName().format(instance.runtime().lang()),
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
