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
        for (int i=0; i < ALL_PERMISSIONS.length; i++) {
            instance.set(1, i, this.populatePermission(instance, ALL_PERMISSIONS[i]));
        }
    }

    protected abstract @NotNull PlatformItem populatePermission(@NotNull GuiInstance instance, @NotNull Permission perm);

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        int slotIndex = slot.index();
        if (slotIndex <= 0) {
            return GuiAction.transfer(GuiSpecs.permissionOverview(this.claim));
        } else if (slotIndex == 1 && index < ALL_PERMISSIONS.length) {
            return this.onClickPermission(instance, ALL_PERMISSIONS[index]);
        } else {
            return GuiAction.nothing();
        }
    }

    protected @NotNull GuiSpec exitDestination() {
        return GuiSpecs.permissionOverview(this.claim);
    }

    protected abstract @NotNull GuiAction onClickPermission(@NotNull GuiInstance instance, @NotNull Permission permission);

}
