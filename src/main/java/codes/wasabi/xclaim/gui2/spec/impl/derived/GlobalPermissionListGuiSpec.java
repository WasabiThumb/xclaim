package codes.wasabi.xclaim.gui2.spec.impl.derived;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.TrustLevel;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
import codes.wasabi.xclaim.gui2.spec.impl.PermissionListGuiSpec;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import codes.wasabi.xclaim.util.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                col = NamedTextColor.RED;
                break;
            case TRUSTED:
                mat = Platform.get().getOrangeToken();
                col = NamedTextColor.GOLD;
                break;
            case VETERANS:
                mat = Platform.get().getYellowToken();
                col = NamedTextColor.YELLOW;
                break;
            case ALL:
                mat = Platform.get().getLimeToken();
                col = NamedTextColor.GREEN;
                break;
            default:
                throw new AssertionError();
        }

        List<Component> lore = new ArrayList<>();
        for (String s : WordWrap.wrap(perm.getDescription(), 25).split(System.lineSeparator())) {
            lore.add(Component.text(s).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        }

        return DisplayItem.create(mat, Component.text(perm.getPrintName()).color(col), lore);
    }

    @Override
    protected @NotNull GuiAction onClickPermission(@NotNull GuiInstance instance, @NotNull Permission permission) {
        return GuiAction.transfer(GuiSpecs.permissionLevels(this.claim, permission));
    }

}
