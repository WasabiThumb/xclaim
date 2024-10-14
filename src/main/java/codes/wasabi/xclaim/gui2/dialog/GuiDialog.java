package codes.wasabi.xclaim.gui2.dialog;

import codes.wasabi.xclaim.XClaim;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;

@ApiStatus.NonExtendable
public interface GuiDialog extends Closeable {

    static @NotNull GuiDialog show(@NotNull GuiDialogType type, @NotNull Player player, @NotNull Component message) {
        GuiDialog ret;
        switch (type) {
            case ACTION_BAR:
                ret = new ActionBarGuiDialog(player, message);
                break;
            case BOSS_BAR:
                ret = new BossBarGuiDialog(player, message);
                break;
            case CHAT:
                ret = new ChatGuiDialog(player, message);
                break;
            default:
                throw new AssertionError("Enum value \"" + type.name() + "\" not handled");
        }
        ret.show();
        return ret;
    }

    static @NotNull GuiDialog show(@NotNull Player player, @NotNull Component message) {
        return show(XClaim.mainConfig.gui().dialog(), player, message);
    }

    //

    /**
     * Called internally by {@link GuiDialog#show(GuiDialogType, Player, Component) GuiDialog.show}.
     */
    @ApiStatus.Internal
    void show();

    /**
     * Cancels any task that may be updating the dialog visually, reverting visual effects when supported.
     */
    @Override
    void close();

}
