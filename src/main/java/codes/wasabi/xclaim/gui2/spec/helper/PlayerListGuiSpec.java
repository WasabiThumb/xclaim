package codes.wasabi.xclaim.gui2.spec.helper;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import codes.wasabi.xclaim.util.NameToPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public abstract class PlayerListGuiSpec extends PaginatedGuiSpec<OfflinePlayer> {

    private static final ItemStack ADD_STACK = DisplayItem.create(
            Material.EMERALD,
            XClaim.lang.getComponent("gui-comb-add")
    );

    //

    @Override
    public @NotNull String layout() {
        return "player-list";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        super.populate(instance);
        instance.set(1, ADD_STACK);
    }

    @Override
    protected @Nullable ItemStack populateEntry(@NotNull GuiInstance instance, @NotNull OfflinePlayer player) {
        ItemStack is = Platform.get().preparePlayerSkull(new ItemStack(Platform.get().getPlayerHeadMaterial(), 1));
        ItemMeta meta = is.getItemMeta();
        if (meta != null) {
            String realName = player.getName();
            if (realName == null) realName = player.getUniqueId().toString();
            Component niceName;
            if (player instanceof Player) {
                niceName = Platform.get().playerDisplayName((Player) player);
            } else {
                niceName = Component.text(realName);
            }
            meta.addItemFlags(ItemFlag.values());
            Platform.get().metaDisplayName(meta, niceName);
            Platform.get().metaLore(meta, Collections.singletonList(Component.text(realName).color(NamedTextColor.GRAY)));
            if (meta instanceof SkullMeta) Platform.get().setOwningPlayer((SkullMeta) meta, player);
        }
        is.setItemMeta(meta);
        return is;
    }

    @Override
    protected @NotNull GuiAction onClickExtra(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        if (slot.index() == 1) {
            return GuiAction.prompt(XClaim.lang.getComponent("gui-comb-prompt"));
        }
        return GuiAction.nothing();
    }

    @Override
    public @NotNull GuiAction onResponse(@NotNull GuiInstance instance, @NotNull String response) {
        OfflinePlayer ply = NameToPlayer.getPlayer(response);
        if (ply == null) {
            Platform.getAdventure().player(instance.player())
                    .sendMessage(XClaim.lang.getComponent("gui-comb-prompt-fail"));
            return GuiAction.exit();
        }
        return this.addPlayer(instance, ply) ? GuiAction.repopulate() : GuiAction.nothing();
    }

    @Override
    protected int getContentSlot() {
        return 0;
    }

    @Override
    protected int getPreviousSlot() {
        return 3;
    }

    @Override
    protected int getNextSlot() {
        return 4;
    }

    @Override
    protected int getBackSlot() {
        return 2;
    }

    //

    protected abstract boolean addPlayer(@NotNull GuiInstance instance, @NotNull OfflinePlayer player);

}
