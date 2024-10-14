package codes.wasabi.xclaim.gui2.spec.impl;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import codes.wasabi.xclaim.gui2.spec.GuiSpec;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class MainGuiSpec implements GuiSpec {

    private static final ItemStack NEW_STACK = DisplayItem.create(
            Material.NETHER_STAR,
            XClaim.lang.getComponent("gui-main-new")
    );

    private static final ItemStack EDIT_TRUST_STACK = DisplayItem.create(
            Platform.get().getSkeletonSkullMaterial(),
            XClaim.lang.getComponent("gui-main-edit-trust")
    );

    private static final ItemStack EDIT_CHUNK_STACK = DisplayItem.create(
            Platform.get().getCraftingTableMaterial(),
            XClaim.lang.getComponent("gui-main-edit-chunk")
    );

    private static final ItemStack RENAME_CHUNK_STACK = DisplayItem.create(
            Material.NAME_TAG,
            XClaim.lang.getComponent("gui-main-rename-chunk")
    );

    private static final ItemStack EDIT_PERM_STACK = DisplayItem.create(
            Platform.get().getShieldMaterial(),
            XClaim.lang.getComponent("gui-main-edit-perm")
    );

    private static final ItemStack TRANSFER_OWNER_STACK = DisplayItem.create(
            Platform.get().getChestMinecartMaterial(),
            XClaim.lang.getComponent("gui-main-transfer-owner")
    );

    private static final ItemStack CLEAR_ALL_STACK = DisplayItem.create(
            Material.TNT,
            XClaim.lang.getComponent("gui-main-clear-all")
    );

    private static final ItemStack DELETE_STACK = DisplayItem.create(
            Material.BARRIER,
            XClaim.lang.getComponent("gui-main-delete")
    );

    private static final ItemStack VERSION_STACK = DisplayItem.create(
            Platform.get().getEnchantingTableMaterial(),
            XClaim.lang.getComponent("gui-main-version")
    );

    private static final ItemStack EXIT_STACK = DisplayItem.create(
            Material.ARROW,
            XClaim.lang.getComponent("gui-main-exit")
    );

    //

    @Override
    public @NotNull String layout() {
        return "main";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        instance.set(0, NEW_STACK);
        instance.set(1, EDIT_TRUST_STACK);
        instance.set(2, EDIT_CHUNK_STACK);
        instance.set(3, RENAME_CHUNK_STACK);
        instance.set(4, EDIT_PERM_STACK);
        instance.set(5, TRANSFER_OWNER_STACK);
        instance.set(6, CLEAR_ALL_STACK);
        instance.set(7, DELETE_STACK);
        instance.set(8, VERSION_STACK);
        instance.set(9, EXIT_STACK);
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        switch (slot.index()) {
            case 0:
                return GuiAction.transfer(GuiSpecs.newClaim());
            case 1:
                return GuiAction.transfer(GuiSpecs.editTrust());
            case 2:
                return GuiAction.transfer(GuiSpecs.editChunks());
            case 3:
                return GuiAction.transfer(GuiSpecs.renameClaim());
            case 4:
                return GuiAction.transfer(GuiSpecs.editPerms());
            case 5:
                return GuiAction.transfer(GuiSpecs.transferableClaimSelector());
            case 6:
                return GuiAction.transfer(GuiSpecs.clearAll());
            case 7:
                return GuiAction.transfer(GuiSpecs.deletingClaimSelector());
            case 8:
                return GuiAction.transfer(GuiSpecs.versionInfo());
            case 9:
                return GuiAction.exit();
        }
        return GuiAction.nothing();
    }

}
