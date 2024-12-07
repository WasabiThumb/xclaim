package io.github.wasabithumb.xclaim.gui2.spec.impl.derived;

import io.github.wasabithumb.xclaim.api.Claim;
import io.github.wasabithumb.xclaim.api.enums.Permission;
import io.github.wasabithumb.xclaim.api.enums.TrustLevel;
import io.github.wasabithumb.xclaim.gui2.GuiInstance;
import io.github.wasabithumb.xclaim.gui2.action.GuiAction;
import io.github.wasabithumb.xclaim.gui2.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.gui2.spec.impl.PermissionListGuiSpec;
import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import io.github.wasabithumb.xclaim.util.WordWrap;
import net.kyori.adventure.text.Component;
import io.github.wasabithumb.xclaim.util.ColorTag;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class GlobalPermissionListGuiSpec extends PermissionListGuiSpec {

    public GlobalPermissionListGuiSpec(@NotNull Claim claim) {
        super(claim);
    }

    @Override
    protected @NotNull ItemStack populatePermission(@NotNull Permission perm) {
        TrustLevel tl = this.claim.getPermission(perm);
        Material mat;
        TextColor col;
        switch (tl) {
            case NONE:
                mat = Platform.get().getRedToken();
                col = ColorTag.RED;
                break;
            case TRUSTED:
                mat = Platform.get().getOrangeToken();
                col = ColorTag.GOLD;
                break;
            case VETERANS:
                mat = Platform.get().getYellowToken();
                col = ColorTag.YELLOW;
                break;
            case ALL:
                mat = Platform.get().getLimeToken();
                col = ColorTag.GREEN;
                break;
            default:
                throw new AssertionError();
        }

        List<Component> lore = new ArrayList<>();
        for (String s : WordWrap.wrap(perm.getDescription(), 25).split(System.lineSeparator())) {
            lore.add(Component.text(s).color(ColorTag.GRAY).decoration(TextDecoration.ITALIC, false));
        }

        return DisplayItem.create(mat, Component.text(perm.getPrintName()).color(col), lore);
    }

    @Override
    protected @NotNull GuiAction onClickPermission(@NotNull GuiInstance instance, @NotNull Permission permission) {
        return GuiAction.transfer(GuiSpecs.permissionLevels(this.claim, permission));
    }

}
