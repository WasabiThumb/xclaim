package io.github.wasabithumb.xclaim.command.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.command.Command;
import io.github.wasabithumb.xclaim.command.argument.CommandArgument;
import io.github.wasabithumb.xclaim.command.argument.type.CommandArgumentType;
import io.github.wasabithumb.xclaim.gui.editor.ClaimEditor;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import org.jetbrains.annotations.NotNull;

public final class EditCommand implements Command<EditCommand.Args> {

    @Override
    public @NotNull Translatable name() {
        return I18N.CMD_CHUNKS_NAME;
    }

    @Override
    public @NotNull Translatable description() {
        return I18N.CMD_CHUNKS_DESCRIPTION;
    }

    @Override
    public @NotNull Class<Args> argsClass() {
        return Args.class;
    }

    @Override
    public @NotNull Args createNewArgs() {
        return new Args(
                CommandArgument.builder(CommandArgumentType.MANAGEABLE_CLAIM)
                        .name(I18N.CMD_CHUNKS_ARG_NAME)
                        .description(I18N.CMD_CHUNKS_ARG_DESCRIPTION)
                        .optional()
                        .build()
        );
    }

    @Override
    public boolean requiresPlayerExecutor() {
        return true;
    }

    @Override
    public void execute(@NotNull XClaim runtime, @NotNull PlatformUser user, @NotNull Args args) {
        PlatformPlayer ply = user.asPlayer();
        PlatformLocation loc = ply.location();
        Claim c = args.claim.get();
        if (c == null) {
            c = runtime.claims().getByChunk(loc.chunk());
            if (c == null) {
                user.sendMessage(runtime.lang(I18N.CMD_CHUNKS_ERR_404));
                return;
            }
        }
        if (!c.checkPermission(ply, Permission.MANAGE)) {
            user.sendMessage(runtime.lang(I18N.CMD_CHUNKS_ERR_PERM));
            return;
        }
        if (!runtime.rootConfig().worlds().checkLists(loc.world())) {
            user.sendMessage(runtime.lang(I18N.CMD_CHUNKS_ERR_DISALLOWED));
            return;
        }
        ClaimEditor editor = runtime.gui().editor();
        if (editor.enter(ply, c)) {
            user.sendMessage(runtime.lang(I18N.CMD_CHUNKS_SUCCESS.with(c.name())));
        } else {
            user.sendMessage(runtime.lang(I18N.CMD_CHUNKS_ERR_STATE));
        }
    }

    //

    public record Args(
            @NotNull CommandArgument<Claim> claim
    ) { }

}
