package codes.wasabi.xclaim.gui2.dialog;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class BossBarGuiDialog extends AbstractGuiDialog {

    private BossBar bar = null;
    public BossBarGuiDialog(@NotNull Player player, @NotNull Component message) {
        super(player, message);
    }

    @Override
    public synchronized void show() {
        if (this.bar != null) this.throwDoubleShow();
        this.bar = BossBar.bossBar(this.message, 1f, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
        this.bar.addViewer(this.audience);
    }

    @Override
    public synchronized void close() {
        if (this.bar != null) {
            this.bar.removeViewer(this.audience);
        }
        this.bar = null;
    }

}
