package io.github.wasabithumb.xclaim.command.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.command.Command;
import io.github.wasabithumb.xclaim.command.NullaryCommand;
import io.github.wasabithumb.xclaim.i18n.Lang;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class RootCommand implements NullaryCommand {

    private final GuiCommand guiCommand = new GuiCommand();

    //

    @Override
    public @NotNull String name(@NotNull Lang lang) {
        return "xclaim";
    }

    @Override
    public @NotNull String description(@NotNull Lang lang) {
        return lang.get("cmd-xc-description");
    }

    @Override
    public @NotNull @Unmodifiable List<Command<?>> subCommands() {
        return List.of(this.guiCommand);
    }

    @Override
    public void execute(@NotNull XClaim runtime, @NotNull PlatformUser user) {
        if (!user.isPlayer()) {
            user.sendMessage(runtime.lang("cmdmgr-err-player"));
            return;
        }
        this.guiCommand.execute(runtime, user);
    }

}
