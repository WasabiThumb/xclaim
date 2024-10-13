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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class ClearAllGuiSpec implements GuiSpec {

    private static final ItemStack[] YES_STACKS = new ItemStack[] {
            DisplayItem.create(
                    Platform.get().getGreenConcreteMaterial(),
                    XClaim.lang.getComponent("gui-clear-yes"),
                    Arrays.asList(
                            XClaim.lang.getComponent("gui-clear-yes-line1"),
                            XClaim.lang.getComponent("gui-clear-yes-line2")
                    )
            ),
            DisplayItem.create(
                    Platform.get().getGreenConcreteMaterial(),
                    XClaim.lang.getComponent("gui-clear-yes2"),
                    Arrays.asList(
                            XClaim.lang.getComponent("gui-clear-yes-line1"),
                            XClaim.lang.getComponent("gui-clear-yes-line2")
                    )
            )
    };

    private static final ItemStack NO_STACK = DisplayItem.create(
            Platform.get().getRedConcreteMaterial(),
            XClaim.lang.getComponent("gui-clear-no"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-clear-no-line1"),
                    XClaim.lang.getComponent("gui-clear-no-line2")
            )
    );

    private int stage = 0;

    @Override
    public @NotNull String layout() {
        return "clear-all";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        instance.set(this.stage, YES_STACKS[this.stage]);
        instance.set(1 - this.stage, NO_STACK);
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        if (slot.index() == this.stage) {
            if (this.stage == 0) {
                this.stage = 1;
                return GuiAction.repopulate();
            }
            Claim.getByOwner(instance.player()).forEach(Claim::unclaim);
            return GuiAction.transfer(GuiSpecs.main());
        } else if (slot.index() == (1 - this.stage)) {
            return GuiAction.transfer(GuiSpecs.main());
        } else {
            return GuiAction.nothing();
        }
    }

}
