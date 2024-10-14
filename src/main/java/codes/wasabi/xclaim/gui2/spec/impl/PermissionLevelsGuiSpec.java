package codes.wasabi.xclaim.gui2.spec.impl;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.TrustLevel;
import codes.wasabi.xclaim.api.event.XClaimEvent;
import codes.wasabi.xclaim.api.event.XClaimSetPermissionEvent;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import codes.wasabi.xclaim.gui2.spec.GuiSpec;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class PermissionLevelsGuiSpec implements GuiSpec {

    private static final ItemStack[] LEVEL_STACKS = new ItemStack[] {
            DisplayItem.create(
                    Platform.get().getRedToken(),
                    XClaim.lang.getComponent("gui-perm-tl-none"),
                    Arrays.asList(
                            XClaim.lang.getComponent("gui-perm-tl-none-line1"),
                            XClaim.lang.getComponent("gui-perm-tl-none-line2")
                    )
            ),
            DisplayItem.create(
                    Platform.get().getOrangeToken(),
                    XClaim.lang.getComponent("gui-perm-tl-trusted"),
                    Arrays.asList(
                            XClaim.lang.getComponent("gui-perm-tl-trusted-line1"),
                            XClaim.lang.getComponent("gui-perm-tl-trusted-line2"),
                            XClaim.lang.getComponent("gui-perm-tl-trusted-line3")
                    )
            ),
            DisplayItem.create(
                    Platform.get().getYellowToken(),
                    XClaim.lang.getComponent("gui-perm-tl-veterans"),
                    Arrays.asList(
                            XClaim.lang.getComponent("gui-perm-tl-veterans-line1"),
                            XClaim.lang.getComponent("gui-perm-tl-veterans-line2"),
                            XClaim.lang.getComponent("gui-perm-tl-veterans-line3")
                    )
            ),
            DisplayItem.create(
                    Platform.get().getLimeToken(),
                    XClaim.lang.getComponent("gui-perm-tl-all"),
                    Arrays.asList(
                            XClaim.lang.getComponent("gui-perm-tl-all-line1"),
                            XClaim.lang.getComponent("gui-perm-tl-all-line2")
                    )
            )
    };
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
        final TrustLevel current = this.claim.getPermission(this.permission);
        ItemStack item;
        for (int i=0; i < LEVEL_STACKS.length; i++) {
            item = LEVEL_STACKS[i];
            if (current == ALL_LEVELS[i]) {
                item = item.clone();
                ItemMeta meta = item.getItemMeta();
                if (meta != null) meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                item.setItemMeta(meta);
            }
            instance.set(i, item);
        }
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        final int slotIndex = slot.index();
        if (0 <= slotIndex && slotIndex <= 3) {
            final TrustLevel trustLevel = ALL_LEVELS[slotIndex];

            if (!XClaimEvent.dispatch(new XClaimSetPermissionEvent(
                    instance.player(),
                    this.claim,
                    this.permission,
                    this.claim.getPermission(this.permission),
                    trustLevel
            ))) return GuiAction.exit();

            this.claim.setPermission(this.permission, trustLevel);
            return GuiAction.transfer(GuiSpecs.globalPermissionList(this.claim));
        }
        return GuiAction.nothing();
    }

}
