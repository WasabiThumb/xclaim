package codes.wasabi.xclaim.gui2.dialog;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class ActionBarGuiDialog extends TickingGuiDialog {

    public ActionBarGuiDialog(@NotNull Player player, @NotNull Component message) {
        super(player, message);
    }

    @Override
    protected void tick() {
        this.audience.sendActionBar(this.message);
    }

    @Override
    protected void lastTick() {
        this.audience.sendActionBar(Component.empty());
    }

    @Override
    protected long period() {
        return 5L;
    }

}
