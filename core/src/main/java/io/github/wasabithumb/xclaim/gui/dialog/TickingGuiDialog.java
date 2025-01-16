package io.github.wasabithumb.xclaim.gui.dialog;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.scheduler.task.PlatformSchedulerTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
abstract class TickingGuiDialog extends AbstractGuiDialog {

    private final Object mutex = new Object();
    private PlatformSchedulerTask task = null;
    private boolean closing = false;

    protected TickingGuiDialog(
            @NotNull XClaim runtime,
            @NotNull PlatformPlayer player,
            @NotNull String message
    ) {
        super(runtime, player, message);
    }

    //

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
            this.task = this.runtime.platform().scheduler()
                    .newTask()
                    .targetEntity(this.player)
                    .executor(this::tickOrLast)
                    .delayTicks(this.delay())
                    .periodTicks(this.period())
                    .build();
        }
    }

    @Override
    public final void close() {
        synchronized (this.mutex) {
            this.closing = true;
        }
    }

}
