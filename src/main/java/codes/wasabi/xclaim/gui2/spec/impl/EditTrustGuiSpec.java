package codes.wasabi.xclaim.gui2.spec.impl;

import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.spec.helper.PlayerListGuiSpec;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class EditTrustGuiSpec extends PlayerListGuiSpec {

    @Override
    protected @NotNull Collection<OfflinePlayer> getEntries(@NotNull GuiInstance instance) {
        return XCPlayer.of(instance.player()).getTrustedPlayersSet();
    }

    @Override
    protected boolean addPlayer(@NotNull GuiInstance instance, @NotNull OfflinePlayer player) {
        return XCPlayer.of(instance.player()).trustPlayer(player);
    }

    @Override
    protected @NotNull GuiAction onClickEntry(@NotNull GuiInstance instance, @NotNull OfflinePlayer player) {
        return XCPlayer.of(instance.player()).untrustPlayer(player) ? GuiAction.repopulate() : GuiAction.nothing();
    }

}
