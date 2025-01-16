package io.github.wasabithumb.xclaim.gui.dialog;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.misc.PlatformBossBar;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class BossBarGuiDialog extends AbstractGuiDialog {

    private PlatformBossBar bar;
    public BossBarGuiDialog(
            @NotNull XClaim runtime,
            @NotNull PlatformPlayer player,
            @NotNull String message
    ) {
        super(runtime, player, message);
    }

    @Override
    public synchronized void show() {
        if (this.bar != null) this.throwDoubleShow();
        this.bar = this.player.createBossBar(
                this.message,
                1f,
                PlatformBossBar.Color.YELLOW,
                PlatformBossBar.Overlay.PROGRESS
        );
    }

    @Override
    public synchronized void close() {
        if (this.bar != null) {
            this.bar.remove();
        }
        this.bar = null;
    }

}
