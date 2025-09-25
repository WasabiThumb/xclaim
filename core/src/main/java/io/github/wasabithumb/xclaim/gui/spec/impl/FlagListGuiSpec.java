package io.github.wasabithumb.xclaim.gui.spec.impl;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.flags.ClaimFlag;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.sound.NamedPlatformSound;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.util.ColorTag;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import org.jetbrains.annotations.NotNull;

public final class FlagListGuiSpec implements GuiSpec {

    private static final ClaimFlag[] ALL_FLAGS = ClaimFlag.values();

    //

    private final Claim claim;

    public FlagListGuiSpec(@NotNull Claim claim) {
        this.claim = claim;
    }

    //

    @Override
    public @NotNull String layout() {
        return "flag-list";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        instance.set(0, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.BARRIER),
                instance.runtime().lang(I18N.GUI_PERM_BACK)
        ));

        for (int i = 0; i < ALL_FLAGS.length; i++) {
            final ClaimFlag cf = ALL_FLAGS[i];
            instance.set(1, i, this.createItem(instance, cf));
        }
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        int slotIndex = slot.index();
        if (slotIndex <= 0) {
            return GuiAction.transfer(GuiSpecs.main());
        } else if (slotIndex == 1 && index < ALL_FLAGS.length) {
            final ClaimFlag cf = ALL_FLAGS[index];
            boolean value = this.claim.getFlags().contains(cf);
            boolean success = this.claim.modifyFlags(instance.player())
                    .setFlag(cf, value)
                    .commit()
                    .isSuccess();

            if (success) {
                instance.player().playSound(NamedPlatformSound.CLICK);
                return GuiAction.repopulate();
            } else {
                return GuiAction.exit();
            }
        } else {
            return GuiAction.nothing();
        }
    }

    private @NotNull PlatformItem createItem(@NotNull GuiInstance instance, @NotNull ClaimFlag flag) {
        final boolean value = this.claim.getFlags().contains(flag);
        return DisplayItem.format(
                instance.platform().createItem(value ? NamedPlatformMaterial.LIME_DYE : NamedPlatformMaterial.RED_DYE),
                instance.runtime().lang(flag.title()),
                value ? ColorTag.GREEN : ColorTag.RED
        );
    }

}
