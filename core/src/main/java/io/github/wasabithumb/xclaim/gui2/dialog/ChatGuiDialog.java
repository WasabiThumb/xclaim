package io.github.wasabithumb.xclaim.gui2.dialog;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class ChatGuiDialog extends TickingGuiDialog {

    private static final String SCREEN_CLEAR = "<black> <br></black><dark_gray> <br></dark_gray>";
    public ChatGuiDialog(
            @NotNull XClaim runtime,
            @NotNull PlatformPlayer player,
            @NotNull String message
    ) {
        super(runtime, player, message);
    }

    @Override
    protected void tick() {
        this.sendClear();
        this.player.sendMessage(this.message + "<br>");
    }

    @Override
    protected void lastTick() {
        this.sendClear();
    }

    private void sendClear() {
        for (int i=0; i < 64; i++)
            this.player.sendMessage(SCREEN_CLEAR);
    }

}
