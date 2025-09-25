package io.github.wasabithumb.xclaim.gui.spec.impl;

import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import org.jetbrains.annotations.NotNull;

public abstract class PermissionListGuiSpec implements GuiSpec {

    private static final Permission[] ALL_PERMISSIONS = Permission.values();

    protected final Claim claim;
    protected PermissionListGuiSpec(@NotNull Claim claim) {
        this.claim = claim;
    }

    @Override
    public @NotNull String layout() {
        return "permission-list";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        instance.set(0, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.BARRIER),
                instance.runtime().lang(I18N.GUI_PERM_BACK)
        ));

        int head = 0;
        for (Permission permission : ALL_PERMISSIONS) {
            if (this.shouldShowPermission(instance, permission)) {
                instance.set(1, head++, this.populatePermission(instance, permission));
            }
        }
    }

    protected abstract @NotNull PlatformItem populatePermission(@NotNull GuiInstance instance, @NotNull Permission perm);

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        int slotIndex = slot.index();
        if (slotIndex <= 0) {
            return GuiAction.transfer(GuiSpecs.permissionOverview(this.claim));
        } else if (slotIndex == 1 && index < ALL_PERMISSIONS.length) {
            int head = 0;
            Permission selection = null;
            for (Permission candidate : ALL_PERMISSIONS) {
                if (!this.shouldShowPermission(instance, candidate)) continue;
                if ((head++) == index) {
                    selection = candidate;
                    break;
                }
            }
            if (selection != null) {
                return this.onClickPermission(instance, selection);
            } else {
                return GuiAction.nothing();
            }
        } else {
            return GuiAction.nothing();
        }
    }

    protected @NotNull GuiSpec exitDestination() {
        return GuiSpecs.permissionOverview(this.claim);
    }

    protected abstract @NotNull GuiAction onClickPermission(@NotNull GuiInstance instance, @NotNull Permission permission);

    private boolean shouldShowPermission(@NotNull GuiInstance instance, @NotNull Permission permission) {
        return instance.runtime().rootConfig().permissions().configurable(permission) ||
                instance.player().hasPermission(Permission.ADMIN_OVERRIDE) ||
                instance.player().isOp();
    }

}
