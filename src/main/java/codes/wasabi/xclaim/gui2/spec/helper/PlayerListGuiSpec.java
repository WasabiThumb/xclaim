package codes.wasabi.xclaim.gui2.spec.helper;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.layout.GuiPagination;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import codes.wasabi.xclaim.gui2.spec.GuiSpec;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public abstract class PlayerListGuiSpec implements GuiSpec {

    private static final ItemStack PREVIOUS_STACK = DisplayItem.create(
            Material.ARROW,
            XClaim.lang.getComponent("gui-comb-previous")
    );

    private static final ItemStack NEXT_STACK = DisplayItem.create(
            Material.ARROW,
            XClaim.lang.getComponent("gui-comb-next")
    );

    private static final ItemStack ADD_STACK = DisplayItem.create(
            Material.EMERALD,
            XClaim.lang.getComponent("gui-comb-add")
    );

    private static final ItemStack BACK_STACK = DisplayItem.create(
            Material.BARRIER,
            XClaim.lang.getComponent("gui-comb-back")
    );

    //

    protected final GuiPagination<OfflinePlayer> pagination = new GuiPagination<>();

    //

    @Override
    public @NotNull String layout() {
        return "player-list";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        final GuiPagination.State state = this.pagination.setSlot(0)
                .setEntries(this.getList(instance), this.shouldForceUpdate())
                .setSort(this.getSort())
                .populate(instance, this::populatePlayer);

        instance.set(1, ADD_STACK);
        instance.set(2, BACK_STACK);

        if (state.hasPrevious()) instance.set(3, PREVIOUS_STACK);
        if (state.hasNext()) instance.set(4, NEXT_STACK);
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        switch (slot.index()) {
            case 0:
                OfflinePlayer ply = this.pagination.click(instance, slot, index);
                if (ply == null) return GuiAction.nothing();
                return this.onClickPlayer(instance, ply);
            case 1:
                return GuiAction.prompt(XClaim.lang.getComponent("gui-comb-prompt"));
            case 2:
                return GuiAction.transfer(GuiSpecs.MAIN);
            case 3:
                this.pagination.previousPage();
                return GuiAction.repopulate();
            case 4:
                this.pagination.nextPage();
                return GuiAction.repopulate();
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

    //

    protected abstract @NotNull Collection<OfflinePlayer> getList(@NotNull GuiInstance instance);

    protected abstract boolean addPlayer(@NotNull GuiInstance instance, @NotNull OfflinePlayer player);

    protected @Nullable Comparator<OfflinePlayer> getSort() {
        return null;
    }

    protected boolean shouldForceUpdate() {
        return false;
    }

    protected @Nullable ItemStack populatePlayer(@NotNull OfflinePlayer player) {
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

    protected abstract @NotNull GuiAction onClickPlayer(@NotNull GuiInstance instance, @NotNull OfflinePlayer player);

}
