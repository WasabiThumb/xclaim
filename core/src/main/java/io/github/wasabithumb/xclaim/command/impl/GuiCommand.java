package io.github.wasabithumb.xclaim.command.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.command.NullaryCommand;
import io.github.wasabithumb.xclaim.gui2.GuiManager;
import io.github.wasabithumb.xclaim.i18n.Lang;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;

public final class GuiCommand implements NullaryCommand {

    @Override
    public @NotNull String name(@NotNull Lang lang) {
        return lang.get("cmd-gui-name");
    }

    @Override
    public @NotNull String description(@NotNull Lang lang) {
        return lang.get("cmd-gui-description");
    }

    @Override
    public boolean requiresPlayerExecutor() {
        return true;
    }

    @Override
    public void execute(@NotNull XClaim runtime, @NotNull PlatformUser user) {
        PlatformPlayer ply = user.asPlayer();
        GuiManager gui = runtime.gui();

        if (gui.editor().getEditing(ply) != null) {
            user.sendMessage(runtime.lang("cmd-gui-err-restricted"));
            return;
        }

        gui.openGui(ply);
    }

}
