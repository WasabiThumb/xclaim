package io.github.wasabithumb.xclaim.gui2.dialog;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
abstract class AbstractGuiDialog implements GuiDialog {

    protected final XClaim runtime;
    protected final PlatformPlayer player;
    protected final String message;

    protected AbstractGuiDialog(
            @NotNull XClaim runtime,
            @NotNull PlatformPlayer player,
            @NotNull String message
    ) {
        this.runtime = runtime;
        this.player = player;
        this.message = message;
    }

    @Contract(" -> fail")
    protected final void throwDoubleShow() throws IllegalStateException {
        throw new IllegalStateException("Cannot show() dialog twice");
    }

}
