package io.github.wasabithumb.xclaim.gui2.spec.impl;

import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.claim.struct.TrustLevel;
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
                    instance.runtime().lang("gui-perm-tl-none"),
                    instance.runtime().lang("gui-perm-tl-none-line1"),
                    instance.runtime().lang("gui-perm-tl-none-line2")
            );
            case TRUSTED -> DisplayItem.format(
                    instance.platform().createItem(NamedPlatformMaterial.ORANGE_DYE),
                    instance.runtime().lang("gui-perm-tl-trusted"),
                    instance.runtime().lang("gui-perm-tl-trusted-line1"),
                    instance.runtime().lang("gui-perm-tl-trusted-line2"),
                    instance.runtime().lang("gui-perm-tl-trusted-line1")
            );
            case VETERANS -> DisplayItem.format(
                    instance.platform().createItem(NamedPlatformMaterial.YELLOW_DYE),
                    instance.runtime().lang("gui-perm-tl-veterans"),
                    instance.runtime().lang("gui-perm-tl-veterans-line1"),
                    instance.runtime().lang("gui-perm-tl-veterans-line2"),
                    instance.runtime().lang("gui-perm-tl-veterans-line1")
            );
            case ALL -> DisplayItem.format(
                    instance.platform().createItem(NamedPlatformMaterial.GREEN_DYE),
                    instance.runtime().lang("gui-perm-tl-all"),
                    instance.runtime().lang("gui-perm-tl-all-line1"),
                    instance.runtime().lang("gui-perm-tl-all-line2")
            );
        };
    }

}
