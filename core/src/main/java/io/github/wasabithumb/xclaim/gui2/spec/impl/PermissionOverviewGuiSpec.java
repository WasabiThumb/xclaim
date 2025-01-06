package io.github.wasabithumb.xclaim.gui2.spec.impl;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui2.GuiInstance;
import io.github.wasabithumb.xclaim.gui2.action.GuiAction;
import io.github.wasabithumb.xclaim.gui2.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui2.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui2.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import org.jetbrains.annotations.NotNull;

public final class PermissionOverviewGuiSpec implements GuiSpec {

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
        instance.set(0, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.BUCKET),
                instance.runtime().lang("gui-perm-general"),
                instance.runtime().lang("gui-perm-general-line1"),
                instance.runtime().lang("gui-perm-general-line2"),
                instance.runtime().lang("gui-perm-general-line3")
        ));
        instance.set(1, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.SKELETON_SKULL),
                instance.runtime().lang("gui-perm-player"),
                instance.runtime().lang("gui-perm-player-line1"),
                instance.runtime().lang("gui-perm-player-line2"),
                instance.runtime().lang("gui-perm-player-line3"),
                instance.runtime().lang("gui-perm-player-line4"),
                instance.runtime().lang("gui-perm-player-line5")
        ));
        instance.set(2, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.BARRIER),
                instance.runtime().lang("gui-perm-back")
        ));
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        return switch (slot.index()) {
            case 0 -> GuiAction.transfer(GuiSpecs.globalPermissionList(this.claim));
            case 1 -> GuiAction.transfer(GuiSpecs.permissiblePlayerList(this.claim));
            case 2 -> GuiAction.transfer(GuiSpecs.editPerms());
            default -> GuiAction.nothing();
        };
    }

}
