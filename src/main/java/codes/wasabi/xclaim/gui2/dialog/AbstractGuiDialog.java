package codes.wasabi.xclaim.gui2.dialog;

import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
abstract class AbstractGuiDialog implements GuiDialog {

    protected final Player player;
    protected final Audience audience;
    protected final Component message;

    protected AbstractGuiDialog(@NotNull Player player, @NotNull Component message) {
        this.player = player;
        this.audience = Platform.getAdventure().player(player);
        this.message = this.wrapMessage(message);
    }

    protected @NotNull Component wrapMessage(@NotNull Component message) {
        return message
                .colorIfAbsent(NamedTextColor.WHITE)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    @Contract(" -> fail")
    protected final void throwDoubleShow() throws IllegalStateException {
        throw new IllegalStateException("Cannot show() dialog twice");
    }

}
