package io.github.wasabithumb.xclaim.command.argument.type;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApiStatus.Internal
final class StringCommandArgumentType implements CommandArgumentType<String> {

    private final List<String> suggestions;
    StringCommandArgumentType() {
        this.suggestions = Collections.emptyList();
    }

    private StringCommandArgumentType(@NotNull List<String> suggestions) {
        this.suggestions = List.copyOf(suggestions);
    }

    //

    @Override
    public @NotNull Translatable name() {
        return I18N.ARG_STRING_NAME;
    }

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
        return this.suggestions;
    }

    @Override
    public @NotNull CommandArgumentType<String> withSuggestions(@NotNull List<String> suggestions) throws UnsupportedOperationException {
        return new StringCommandArgumentType(suggestions);
    }

}
