package codes.wasabi.xclaim.gui2.spec.impl;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
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

import java.util.Arrays;

public final class PermissionOverviewGuiSpec implements GuiSpec {

    private static final ItemStack GLOBAL_STACK = DisplayItem.create(
            Material.BUCKET,
            XClaim.lang.getComponent("gui-perm-general"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-perm-general-line1"),
                    XClaim.lang.getComponent("gui-perm-general-line2"),
                    XClaim.lang.getComponent("gui-perm-general-line3")
            )
    );

    private static final ItemStack PLAYER_STACK = DisplayItem.create(
            Platform.get().getSkeletonSkullMaterial(),
            XClaim.lang.getComponent("gui-perm-player"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-perm-player-line1"),
                    XClaim.lang.getComponent("gui-perm-player-line2"),
                    XClaim.lang.getComponent("gui-perm-player-line3"),
                    XClaim.lang.getComponent("gui-perm-player-line4"),
                    XClaim.lang.getComponent("gui-perm-player-line5")
            )
    );

    private static final ItemStack BACK_STACK = DisplayItem.create(
            Material.BARRIER,
            XClaim.lang.getComponent("gui-perm-back")
    );

    private final Claim claim;
    public PermissionOverviewGuiSpec(@NotNull Claim claim) {
        this.claim = claim;
    }

    @Override
    public @NotNull String layout() {
        return "permission-overview";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        instance.set(0, GLOBAL_STACK);
        instance.set(1, PLAYER_STACK);
        instance.set(2, BACK_STACK);
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        switch (slot.index()) {
            case 0:
                return GuiAction.transfer(GuiSpecs.globalPermissionList(this.claim));
            case 1:
                return GuiAction.transfer(GuiSpecs.permissiblePlayerList(this.claim));
            case 2:
                return GuiAction.transfer(GuiSpecs.editPerms());
        }
        return GuiAction.nothing();
    }

}
