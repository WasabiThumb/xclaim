package io.github.wasabithumb.xclaim.gui2.spec.impl;

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

import java.util.Set;

public final class ClearAllGuiSpec implements GuiSpec {

    private int stage = 0;

    @Override
    public @NotNull String layout() {
        return "clear-all";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        PlatformItem yes = DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.GREEN_CONCRETE),
                instance.runtime().lang(this.stage == 0 ? "gui-clear-yes" : "gui-clear-yes2"),
                instance.runtime().lang("gui-clear-yes-line1"),
                instance.runtime().lang("gui-clear-yes-line2")
        );
        PlatformItem no = DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.RED_CONCRETE),
                instance.runtime().lang("gui-clear-no"),
                instance.runtime().lang("gui-clear-no-line1"),
                instance.runtime().lang("gui-clear-no-line2")
        );
        instance.set(this.stage, yes);
        instance.set(1 - this.stage, no);
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        if (slot.index() == this.stage) {
            if (this.stage == 0) {
                this.stage = 1;
                return GuiAction.repopulate();
            }
            Set<Claim> claims = instance.runtime().claims().getByOwner(instance.player());
            for (Claim c : claims) {
                c.modifyChunks(instance.player())
                        .silent(true)
                        .clear()
                        .commit()
                        .unwrap();
            }
            return GuiAction.transfer(GuiSpecs.main());
        } else if (slot.index() == (1 - this.stage)) {
            return GuiAction.transfer(GuiSpecs.main());
        } else {
            return GuiAction.nothing();
        }
    }

}
