package io.github.wasabithumb.xclaim.gui.spec.impl;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.sound.NamedPlatformSound;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import org.jetbrains.annotations.NotNull;

public final class NewClaimGuiSpec implements GuiSpec {

    @Override
    public @NotNull String layout() {
        return "new-claim";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        instance.set(0, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.GREEN_CONCRETE),
                instance.runtime().lang(I18N.GUI_NEW_CONFIRM),
                instance.runtime().lang(I18N.GUI_NEW_CONFIRM_LINE1),
                instance.runtime().lang(I18N.GUI_NEW_CONFIRM_LINE2),
                instance.runtime().lang(I18N.GUI_NEW_CONFIRM_LINE3)
        ));
        instance.set(1, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.RED_CONCRETE),
                instance.runtime().lang(I18N.GUI_NEW_CANCEL),
                instance.runtime().lang(I18N.GUI_NEW_CANCEL_LINE1),
                instance.runtime().lang(I18N.GUI_NEW_CANCEL_LINE2)
        ));
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        if (slot.index() == 0) {
            this.confirm(instance);
            return GuiAction.exit();
        } else if (slot.index() == 1) {
            return GuiAction.transfer(GuiSpecs.main());
        }
        return GuiAction.nothing();
    }

    //

    private void confirm(@NotNull GuiInstance instance) {
        PlatformPlayer ply = instance.player();
        Claim created = instance.runtime().claims().create(ply, ply.location().chunk());
        if (created == null) return;
        ply.playSound(NamedPlatformSound.LEVEL);
        if (instance.runtime().rootConfig().editor().startOnCreate()) {
            instance.runtime().gui().editor().enter(ply, created);
        }
    }

}
