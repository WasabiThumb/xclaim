package io.github.wasabithumb.xclaim.gui.spec.impl;

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

import java.util.Collection;
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
                instance.runtime().lang(this.stage == 0 ? I18N.GUI_CLEAR_YES : I18N.GUI_CLEAR_YES2),
                instance.runtime().lang(I18N.GUI_CLEAR_YES_LINE1),
                instance.runtime().lang(I18N.GUI_CLEAR_YES_LINE2)
        );
        PlatformItem no = DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.RED_CONCRETE),
                instance.runtime().lang(I18N.GUI_CLEAR_NO),
                instance.runtime().lang(I18N.GUI_CLEAR_NO_LINE1),
                instance.runtime().lang(I18N.GUI_CLEAR_NO_LINE2)
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
            Collection<Claim> claims = instance.runtime().claims().getByOwner(instance.player());
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
