package io.github.wasabithumb.xclaim.command;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;

public interface NullaryCommand extends Command<NullaryCommand.Args> {

    @Override
    default @NotNull Class<Args> argsClass() {
        return Args.class;
    }

    @Override
    default @NotNull NullaryCommand.Args createNewArgs() {
        return new Args();
    }

    @Override
    default void execute(@NotNull XClaim runtime, @NotNull PlatformUser user, @NotNull Args args) {
        this.execute(runtime, user);
    }

    void execute(@NotNull XClaim runtime, @NotNull PlatformUser user);

    //

    record Args() { }

}
