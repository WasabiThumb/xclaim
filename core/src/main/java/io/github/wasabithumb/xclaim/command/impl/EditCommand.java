package io.github.wasabithumb.xclaim.command.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.command.Command;
import io.github.wasabithumb.xclaim.command.argument.CommandArgument;
import io.github.wasabithumb.xclaim.command.argument.type.CommandArgumentType;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
    public void execute(@NotNull XClaim runtime, @NotNull PlatformUser user, @NotNull Args args) {
        // TODO
        user.sendMessage(Objects.toString(args.claim));
    }

    //

    public record Args(
            @NotNull CommandArgument<Claim> claim
    ) { }

}
