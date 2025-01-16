package io.github.wasabithumb.xclaim.gui2.spec.impl.derived;

import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.claim.struct.TrustLevel;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui2.GuiInstance;
import io.github.wasabithumb.xclaim.gui2.action.GuiAction;
import io.github.wasabithumb.xclaim.gui2.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.gui2.spec.impl.PermissionListGuiSpec;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import io.github.wasabithumb.xclaim.util.WordWrap;
import io.github.wasabithumb.xclaim.util.ColorTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class GlobalPermissionListGuiSpec extends PermissionListGuiSpec {

    public GlobalPermissionListGuiSpec(@NotNull Claim claim) {
        super(claim);
    }

    @Override
    protected @NotNull PlatformItem populatePermission(@NotNull GuiInstance instance, @NotNull Permission perm) {
        TrustLevel tl = this.claim.getGlobalPermission(perm);
        PlatformMaterial mat;
        ColorTag col = switch (tl) {
            case NONE -> {
                mat = NamedPlatformMaterial.RED_DYE;
                yield ColorTag.RED;
            }
            case TRUSTED -> {
                mat = NamedPlatformMaterial.ORANGE_DYE;
                yield ColorTag.GOLD;
            }
            case VETERANS -> {
                mat = NamedPlatformMaterial.YELLOW_DYE;
                yield ColorTag.YELLOW;
            }
            case ALL -> {
                mat = NamedPlatformMaterial.GREEN_DYE;
                yield ColorTag.GREEN;
            }
        };

        List<String> lore = new ArrayList<>();
        for (String s : WordWrap.wrap(perm.getDescription(instance.runtime().lang()), 25).split("\\r?\\n")) {
            lore.add(ColorTag.GRAY.format(s));
        }

        return DisplayItem.format(
                instance.platform().createItem(mat),
                col.format(perm.getPrintName(instance.runtime().lang())),
                lore
        );
    }

    @Override
    protected @NotNull GuiAction onClickPermission(@NotNull GuiInstance instance, @NotNull Permission permission) {
        return GuiAction.transfer(GuiSpecs.permissionLevels(this.claim, permission));
    }

}
