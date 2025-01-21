package io.github.wasabithumb.xclaim.command.argument;

import io.github.wasabithumb.xclaim.command.argument.type.CommandArgumentType;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class CommandArgument<T> {

    public static <R> @NotNull Builder<R> builder(@NotNull CommandArgumentType<R> type) {
        return new Builder<>(type);
    }

    //

    private final CommandArgumentType<T> type;
    private final Translatable name;
    private final Translatable description;
    private final boolean optional;
    private T value;

    //

    CommandArgument(
            @NotNull CommandArgumentType<T> type,
            @NotNull Translatable name,
            @NotNull Translatable description,
            boolean optional
    ) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.optional = optional;
    }

    public @NotNull CommandArgumentType<T> type() {
        return this.type;
    }

    public @NotNull Translatable name() {
        return this.name;
    }

    public @NotNull Translatable description() {
        return this.description;
    }

    public @UnknownNullability T get() {
        if (this.value == null && !this.optional)
            throw new IllegalStateException("Value of non-optional argument is not set");
        return this.value;
    }

    @Contract("!null -> !null")
    public T getElse(T defaultValue) {
        if (this.value == null) {
            if (!this.optional) throw new IllegalStateException("Value of non-optional argument is not set");
            return defaultValue;
        }
        return this.value;
    }

    public void set(@NotNull Object value) {
        if (!this.type.typeClass().isInstance(value))
            throw new IllegalArgumentException("Value is not instance of type class " + this.type.typeClass().getName());
        this.value = this.type.typeClass().cast(value);
    }

    public boolean optional() {
        return this.optional;
    }

    //

    public static final class Builder<Q> {

        private final CommandArgumentType<Q> type;
        private Translatable name;
        private Translatable description;
        private boolean optional;

        Builder(@NotNull CommandArgumentType<Q> type) {
            this.type = type;
            this.name = null;
            this.description = null;
            this.optional = false;
        }

        @Contract("_ -> this")
        public @NotNull Builder<Q> name(@NotNull Translatable name) {
            this.name = name;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder<Q> description(@NotNull Translatable description) {
            this.description = description;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder<Q> optional(boolean optional) {
            this.optional = optional;
            return this;
        }

        @Contract("-> this")
        public @NotNull Builder<Q> optional() {
            return this.optional(true);
        }

        @Contract("-> new")
        public @NotNull CommandArgument<Q> build() {
            if (this.name == null) throw new IllegalStateException("Cannot create argument without name");
            if (this.description == null) throw new IllegalStateException("Cannot create argument without description");
            return new CommandArgument<>(
                    this.type,
                    this.name,
                    this.description,
                    this.optional
            );
        }

    }

}
