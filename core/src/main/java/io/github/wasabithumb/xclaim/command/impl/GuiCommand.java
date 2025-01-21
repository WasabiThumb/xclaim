package io.github.wasabithumb.xclaim.command.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.command.NullaryCommand;
import io.github.wasabithumb.xclaim.gui.GuiManager;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;

public final class GuiCommand implements NullaryCommand {

    @Override
    public @NotNull Translatable name() {
        return I18N.CMD_GUI_NAME;
    }

    @Override
    public @NotNull Translatable description() {
        return I18N.CMD_GUI_DESCRIPTION;
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
            user.sendMessage(runtime.lang(I18N.CMD_GUI_ERR_RESTRICTED));
            return;
        }

        gui.openGui(ply);
    }

}
