package io.github.wasabithumb.xclaim.gui2.dialog;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class ActionBarGuiDialog extends TickingGuiDialog {

    public ActionBarGuiDialog(
            @NotNull XClaim runtime,
            @NotNull PlatformPlayer player,
            @NotNull String message
    ) {
        super(runtime, player, message);
    }

    @Override
    protected void tick() {
        this.player.sendActionBar(this.message);
    }

    @Override
    protected void lastTick() {
        this.player.sendActionBar("");
    }

    @Override
    protected long period() {
        return 5L;
    }

}
