package codes.wasabi.xclaim.gui2.dialog;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class ChatGuiDialog extends TickingGuiDialog {

    private static final Component SCREEN_CLEAR;
    static {
        Component base = Component.text(' ').append(Component.newline()); // 1
        base = Component.empty()
                .append(base.color(NamedTextColor.BLACK))
                .append(base.color(NamedTextColor.DARK_GRAY)); // 2

        // 4, 8, 16, 32, 64
        for (int i=0; i < 5; i++) base = Component.empty().append(base).append(base);
        SCREEN_CLEAR = base;
    }

    public ChatGuiDialog(@NotNull Player player, @NotNull Component message) {
        super(player, message);
    }

    @Override
    protected void tick() {
        this.audience.sendMessage(Component.empty()
                .append(SCREEN_CLEAR)
                .append(this.message)
                .append(Component.newline())
        );
    }

    @Override
    protected void lastTick() {
        this.audience.sendMessage(SCREEN_CLEAR);
    }

}
