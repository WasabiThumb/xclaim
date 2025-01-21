package io.github.wasabithumb.xclaim.i18n;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ApiStatus.NonExtendable
public sealed interface Translatable {

    @Contract("_ -> new")
    static @NotNull Translatable literal(@NotNull String value) {
        return new Literal(value);
    }

    @Contract("_ -> new")
    static @NotNull Translatable keyed(@NotNull String key) {
        return new Keyed(key);
    }

    //

    @NotNull String format(@NotNull Lang lang, @NotNull Object @NotNull ... args);

    //

    record Literal(@NotNull String value) implements Translatable {

        @Override
        public @NotNull String format(@NotNull Lang lang, @NotNull Object @NotNull ... args) {
            return this.value;
        }

    }

    //

    record Keyed(@NotNull String key) implements Translatable {

        @Override
        public @NotNull String format(@NotNull Lang lang, @NotNull Object @NotNull ... args) {
            return lang.get(this.key, args);
        }

    }

}
