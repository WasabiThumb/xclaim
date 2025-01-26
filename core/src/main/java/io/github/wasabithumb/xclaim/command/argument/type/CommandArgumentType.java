package io.github.wasabithumb.xclaim.command.argument.type;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.command.argument.CommandArgument;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface CommandArgumentType<T> {

    CommandArgumentType<String> STRING = new StringCommandArgumentType();

    CommandArgumentType<PlatformUser> USER = new UserCommandArgumentType();

    CommandArgumentType<Integer> INTEGER = new IntegerCommandArgumentType();

    @Contract("_, _ -> new")
    static @NotNull CommandArgumentType<Integer> integer(int min, int max) throws IllegalArgumentException {
        return new IntegerCommandArgumentType(min, max);
    }

    @Contract("_ -> new")
    static @NotNull CommandArgumentType<Claim> claim(@NotNull BiPredicate<Claim, PlatformUser> condition) {
        return new ClaimCommandArgumentType(condition);
    }

    CommandArgumentType<Claim> MANAGEABLE_CLAIM = claim((Claim c, PlatformUser user) -> c.checkPermission(user, Permission.MANAGE));

    //

    @NotNull Translatable name();

    @NotNull Class<T> typeClass();

    @NotNull ParseResult<T> parse(@NotNull XClaim runtime, @NotNull PlatformUser user, @NotNull String input);

    @NotNull @Unmodifiable List<String> suggest(@NotNull XClaim runtime, @NotNull PlatformUser user);

    @Contract("_ -> new")
    default @NotNull CommandArgumentType<T> withSuggestions(@NotNull List<String> suggestions) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Argument type does not support custom suggestions");
    }

    //

    abstract sealed class ParseResult<R> {

        static <R> @NotNull ParseResult<R> success(@NotNull R value) {
            return new Success<>(value);
        }

        static <R> @NotNull ParseResult<R> error(@NotNull String error) {
            return new Error<>(error);
        }

        static <R> @NotNull ParseResult<R> ofNullable(@Nullable R value, @NotNull String messageIfNull) {
            if (value == null) return error(messageIfNull);
            return success(value);
        }

        //

        public abstract boolean isSuccess();

        public abstract @NotNull R value() throws UnsupportedOperationException;

        public abstract @NotNull String error() throws UnsupportedOperationException;

        //

        private static final class Success<R> extends ParseResult<R> {

            private final R value;
            Success(@NotNull R value) {
                this.value = value;
            }

            @Override
            public boolean isSuccess() {
                return true;
            }

            @Override
            public @NotNull R value() {
                return this.value;
            }

            @Override
            public @NotNull String error() throws UnsupportedOperationException {
                throw new UnsupportedOperationException("ParseResult.Success has no error message");
            }

        }

        private static final class Error<R> extends ParseResult<R> {

            private final String message;
            Error(@NotNull String message) {
                this.message = message;
            }

            @Override
            public boolean isSuccess() {
                return false;
            }

            @Override
            public @NotNull R value() throws UnsupportedOperationException {
                throw new UnsupportedOperationException("ParseResult.Error has no value");
            }

            @Override
            public @NotNull String error() {
                return this.message;
            }

        }

    }

}
