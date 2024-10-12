package codes.wasabi.xclaim.gui2.spec.impl;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import codes.wasabi.xclaim.gui2.spec.GuiSpec;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
import codes.wasabi.xclaim.util.DisplayItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class PermissionListGuiSpec implements GuiSpec {

    private static final Permission[] ALL_PERMISSIONS = Permission.values();
    private static final ItemStack BACK_STACK = DisplayItem.create(
            Material.BARRIER,
            XClaim.lang.getComponent("gui-perm-back")
    );

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
        instance.set(0, BACK_STACK);
        for (int i=0; i < ALL_PERMISSIONS.length; i++) {
            instance.set(1 + i, this.populatePermission(ALL_PERMISSIONS[i]));
        }
    }

    protected abstract @NotNull ItemStack populatePermission(@NotNull Permission perm);

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
