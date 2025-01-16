package io.github.wasabithumb.xclaim.gui.spec.impl;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.sound.NamedPlatformSound;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import io.github.wasabithumb.xclaim.util.ColorTag;
import org.jetbrains.annotations.NotNull;

public final class TransferOwnerGuiSpec implements GuiSpec {

    private final Claim claim;
    private final PlatformUser target;
    public TransferOwnerGuiSpec(@NotNull Claim claim, @NotNull PlatformUser target) {
        this.claim = claim;
        this.target = target;
    }

    @Override
    public @NotNull String layout() {
        return "transfer-owner";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        instance.set(0, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.GREEN_CONCRETE),
                instance.runtime().lang("gui-tx-yes"),
                instance.runtime().lang("gui-tx-yes-line1"),
                instance.runtime().lang("gui-tx-yes-line2"),
                instance.runtime().lang("gui-tx-yes-line3")
        ));
        instance.set(1, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.RED_CONCRETE),
                instance.runtime().lang("gui-tx-no"),
                instance.runtime().lang("gui-tx-no-line1"),
                instance.runtime().lang("gui-tx-no-line2")
        ));
        instance.set(2, this.getTargetHead(instance));
    }

    private @NotNull PlatformItem getTargetHead(@NotNull GuiInstance instance) {
        String targetName;
        String display = this.target.displayName();

        if (this.target instanceof PlatformPlayer ply) {
            String real = ply.name();
            if (display.equals(real)) {
                targetName = display;
            } else {
                targetName = ColorTag.GRAY.format(
                        ColorTag.WHITE.format(ply.displayName()) +
                                " (" + ply.name() + ")"
                );
            }
        } else {
            targetName = ColorTag.GRAY.format(this.target.displayName());
        }

        return DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.PLAYER_HEAD),
                targetName
        ).skullOwner(this.target);
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        if (slot.index() == 1) {
            return GuiAction.transfer(GuiSpecs.transferableClaimSelector());
        } else if (slot.index() != 0) {
            return GuiAction.nothing();
        }

        // TODO: Handle failure
        this.claim.transferOwner(instance.player())
                .setNewOwner(this.target)
                .commit();

        instance.player().playSound(NamedPlatformSound.LEVEL);
        return GuiAction.transfer(GuiSpecs.transferableClaimSelector());
    }

}
