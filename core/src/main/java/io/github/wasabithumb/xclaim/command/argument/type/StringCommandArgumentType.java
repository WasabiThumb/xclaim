package io.github.wasabithumb.xclaim.command.argument.type;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

@ApiStatus.Internal
final class StringCommandArgumentType implements CommandArgumentType<String> {

    @Override
    public @NotNull Class<String> typeClass() {
        return String.class;
    }

    @Override
    public @NotNull ParseResult<String> parse(@NotNull XClaim runtime, @NotNull PlatformUser user, @NotNull String input) {
        return ParseResult.success(input);
    }

    @Override
    public @NotNull @Unmodifiable List<String> suggest(@NotNull XClaim runtime, @NotNull PlatformUser user) {
        return Collections.emptyList();
    }

}
