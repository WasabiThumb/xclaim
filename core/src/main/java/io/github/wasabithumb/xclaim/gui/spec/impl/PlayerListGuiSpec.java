package io.github.wasabithumb.xclaim.gui.spec.impl;

import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui.spec.helper.PaginatedGuiSpec;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import io.github.wasabithumb.xclaim.util.ColorTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerListGuiSpec extends PaginatedGuiSpec<PlatformUser> {

    @Override
    public @NotNull String layout() {
        return "player-list";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        super.populate(instance);
        instance.set(1, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.EMERALD),
                instance.runtime().lang(I18N.GUI_COMB_ADD)
        ));
    }

    @Override
    protected @Nullable PlatformItem populateEntry(@NotNull GuiInstance instance, @NotNull PlatformUser player) {
        String realName = player.displayName();
        if (player.isPlayer()) {
            realName = player.asPlayer().name();
        }

        return instance.platform()
                .createItem(NamedPlatformMaterial.PLAYER_HEAD)
                .skullOwner(player)
                .displayName(player.displayName())
                .lore(ColorTag.GRAY.format(realName))
                .hideExtra();
    }

    @Override
    protected @NotNull GuiAction onClickExtra(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        if (slot.index() == 1) {
            return GuiAction.prompt(instance.runtime().lang(I18N.GUI_COMB_PROMPT));
        }
        return GuiAction.nothing();
    }

    @Override
    public @NotNull GuiAction onResponse(@NotNull GuiInstance instance, @NotNull String response) {
        PlatformUser ply = instance.platform().users().matchUser(response);
        if (ply == null) {
            instance.player().sendMessage(instance.runtime().lang(I18N.GUI_COMB_PROMPT_FAIL));
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

    @Override
    public boolean asyncResponse() {
        return true;
    }

    //

    protected abstract boolean addPlayer(@NotNull GuiInstance instance, @NotNull PlatformUser player);

}
