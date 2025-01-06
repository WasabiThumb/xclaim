package io.github.wasabithumb.xclaim.gui2.spec.impl;

import io.github.wasabithumb.xclaim.api.enums.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui2.GuiInstance;
import io.github.wasabithumb.xclaim.gui2.action.GuiAction;
import io.github.wasabithumb.xclaim.gui2.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui2.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui2.spec.GuiSpecs;
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
                instance.runtime().lang("gui-perm-back")
        ));
        for (int i=0; i < ALL_PERMISSIONS.length; i++) {
            instance.set(1 + i, this.populatePermission(instance, ALL_PERMISSIONS[i]));
        }
    }

    protected abstract @NotNull PlatformItem populatePermission(@NotNull GuiInstance instance, @NotNull Permission perm);

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        int slotIndex = slot.index();
        if (slotIndex <= 0) return GuiAction.transfer(GuiSpecs.permissionOverview(this.claim));

        if (slotIndex > ALL_PERMISSIONS.length) return GuiAction.nothing();
        return this.onClickPermission(instance, ALL_PERMISSIONS[slotIndex - 1]);
    }

    protected @NotNull GuiSpec exitDestination() {
        return GuiSpecs.permissionOverview(this.claim);
    }

    protected abstract @NotNull GuiAction onClickPermission(@NotNull GuiInstance instance, @NotNull Permission permission);

}
