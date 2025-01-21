package io.github.wasabithumb.xclaim.gui.spec.impl;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.i18n.I18N;
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
                instance.runtime().lang(I18N.GUI_PERM_GENERAL),
                instance.runtime().lang(I18N.GUI_PERM_GENERAL_LINE1),
                instance.runtime().lang(I18N.GUI_PERM_GENERAL_LINE2),
                instance.runtime().lang(I18N.GUI_PERM_GENERAL_LINE3)
        ));
        instance.set(1, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.SKELETON_SKULL),
                instance.runtime().lang(I18N.GUI_PERM_PLAYER),
                instance.runtime().lang(I18N.GUI_PERM_PLAYER_LINE1),
                instance.runtime().lang(I18N.GUI_PERM_PLAYER_LINE2),
                instance.runtime().lang(I18N.GUI_PERM_PLAYER_LINE3),
                instance.runtime().lang(I18N.GUI_PERM_PLAYER_LINE4),
                instance.runtime().lang(I18N.GUI_PERM_PLAYER_LINE5)
        ));
        instance.set(2, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.BARRIER),
                instance.runtime().lang(I18N.GUI_PERM_BACK)
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
