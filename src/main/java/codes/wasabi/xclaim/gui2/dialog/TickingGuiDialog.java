package codes.wasabi.xclaim.gui2.dialog;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformSchedulerTask;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
abstract class TickingGuiDialog extends AbstractGuiDialog {

    private final Object mutex = new Object();
    private PlatformSchedulerTask task = null;
    private boolean closing = false;
    protected TickingGuiDialog(@NotNull Player player, @NotNull Component message) {
        super(player, message);
    }

    protected abstract void tick();

    protected void lastTick() { }

    protected long delay() {
        return 0L;
    }

    protected long period() {
        return 1L;
    }

    private void tickOrLast() {
        boolean closing;
        synchronized (this.mutex) {
            closing = this.closing;
            if (closing) {
                this.task.cancel();
                this.task = null;
            }
        }

        if (closing) {
            this.lastTick();
        } else {
            this.tick();
        }
    }

    @Override
    public final void show() {
        synchronized (this.mutex) {
            if (this.task != null) this.throwDoubleShow();
            this.closing = false;
            this.task = Platform.get().getScheduler().runTaskTimer(
                    XClaim.instance,
                    this::tickOrLast,
                    this.delay(),
                    this.period()
            );
        }
    }

    @Override
    public final void close() {
        synchronized (this.mutex) {
            this.closing = true;
        }
    }

}
