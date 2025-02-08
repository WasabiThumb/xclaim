package io.github.wasabithumb.xclaim.command.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.command.Command;
import io.github.wasabithumb.xclaim.command.NullaryCommand;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class RootCommand implements NullaryCommand {

    private final GuiCommand guiCommand = new GuiCommand();
    private final InfoCommand infoCommand = new InfoCommand();
    private final EditCommand editCommand = new EditCommand();

    //

    @Override
    public @NotNull Translatable name() {
        return Translatable.literal("xclaim");
    }

    @Override
    public @NotNull Translatable description() {
        return I18N.CMD_XC_DESCRIPTION;
    }

    @Override
    public @NotNull @Unmodifiable List<Command<?>> subCommands() {
        return List.of(this.guiCommand, this.infoCommand, this.editCommand);
    }

    @Override
    public void execute(@NotNull XClaim runtime, @NotNull PlatformUser user) {
        if (!user.isPlayer()) {
            user.sendMessage(runtime.lang(I18N.CMDMGR_ERR_PLAYER));
            return;
        }
        this.guiCommand.execute(runtime, user);
    }

}
