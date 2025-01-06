package io.github.wasabithumb.xclaim.gui2.dialog;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public interface GuiDialog {

    static @NotNull GuiDialog show(
            @NotNull XClaim runtime,
            @NotNull PlatformPlayer player,
            @NotNull String message
    ) {
        GuiDialog ret = switch (runtime.rootConfig().gui().dialog()) {
            case ACTION_BAR -> new ActionBarGuiDialog(runtime, player, message);
            case BOSS_BAR -> new BossBarGuiDialog(runtime, player, message);
            case CHAT -> new ChatGuiDialog(runtime, player, message);
        };
        ret.show();
        return ret;
    }

    //

    @ApiStatus.Internal
    void show();

    /**
     * Cancels any task that may be updating the dialog visually, reverting visual effects when supported.
     */
    void close();

}
