package io.github.wasabithumb.xclaim.gui.spec.impl;

import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.claim.permission.TrustLevel;
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

public final class PermissionLevelsGuiSpec implements GuiSpec {

    private static final TrustLevel[] ALL_LEVELS = TrustLevel.ascending();

    private final Claim claim;
    private final Permission permission;
    public PermissionLevelsGuiSpec(@NotNull Claim claim, @NotNull Permission permission) {
        this.claim = claim;
        this.permission = permission;
    }

    @Override
    public @NotNull String layout() {
        return "permission-levels";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        TrustLevel current = this.claim.getGlobalPermission(this.permission);
        for (int i=0; i < ALL_LEVELS.length; i++) {
            TrustLevel level = ALL_LEVELS[i];
            PlatformItem item = this.getDisplayItem(instance, level);
            instance.set(i, level == current ? item.holographic() : item);
        }
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        final int slotIndex = slot.index();
        if (0 <= slotIndex && slotIndex <= 3) {
            final TrustLevel trustLevel = ALL_LEVELS[slotIndex];
            boolean success = this.claim.modifyPermissions(instance.player())
                    .setGlobalPermission(this.permission, trustLevel)
                    .commit()
                    .isSuccess();
            return success ?
                    GuiAction.transfer(GuiSpecs.globalPermissionList(this.claim)) :
                    GuiAction.exit();
        }
        return GuiAction.nothing();
    }

    private @NotNull PlatformItem getDisplayItem(@NotNull GuiInstance instance, @NotNull TrustLevel tl) {
        return switch (tl) {
            case NONE -> DisplayItem.format(
                    instance.platform().createItem(NamedPlatformMaterial.RED_DYE),
                    instance.runtime().lang(I18N.GUI_PERM_TL_NONE),
                    instance.runtime().lang(I18N.GUI_PERM_TL_NONE_LINE1),
                    instance.runtime().lang(I18N.GUI_PERM_TL_NONE_LINE2)
            );
            case TRUSTED -> DisplayItem.format(
                    instance.platform().createItem(NamedPlatformMaterial.ORANGE_DYE),
                    instance.runtime().lang(I18N.GUI_PERM_TL_TRUSTED),
                    instance.runtime().lang(I18N.GUI_PERM_TL_TRUSTED_LINE1),
                    instance.runtime().lang(I18N.GUI_PERM_TL_TRUSTED_LINE2),
                    instance.runtime().lang(I18N.GUI_PERM_TL_TRUSTED_LINE3)
            );
            case VETERANS -> DisplayItem.format(
                    instance.platform().createItem(NamedPlatformMaterial.YELLOW_DYE),
                    instance.runtime().lang(I18N.GUI_PERM_TL_VETERANS),
                    instance.runtime().lang(I18N.GUI_PERM_TL_VETERANS_LINE1),
                    instance.runtime().lang(I18N.GUI_PERM_TL_VETERANS_LINE2),
                    instance.runtime().lang(I18N.GUI_PERM_TL_VETERANS_LINE3)
            );
            case ALL -> DisplayItem.format(
                    instance.platform().createItem(NamedPlatformMaterial.LIME_DYE),
                    instance.runtime().lang(I18N.GUI_PERM_TL_ALL),
                    instance.runtime().lang(I18N.GUI_PERM_TL_ALL_LINE1),
                    instance.runtime().lang(I18N.GUI_PERM_TL_ALL_LINE2)
            );
        };
    }

}
