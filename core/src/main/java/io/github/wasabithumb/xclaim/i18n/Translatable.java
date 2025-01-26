package io.github.wasabithumb.xclaim.i18n;

import io.github.wasabithumb.xclaim.XClaim;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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

    @NotNull String format(@NotNull Lang lang);

    default @NotNull String format(@NotNull XClaim runtime) {
        return this.format(runtime.lang());
    }

    @Contract("_ -> new")
    default @NotNull Translatable with(@NotNull Object @NotNull ... args) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot set args for complex Translatable");
    }

    @Contract("_ -> new")
    default @NotNull Translatable concat(@NotNull Translatable other) {
        return new Concat(this, other);
    }

    @Contract("_ -> new")
    default @NotNull Translatable concat(@NotNull String other) {
        return this.concat(Translatable.literal(other));
    }

    //

    record Literal(@NotNull String value) implements Translatable {

        @Override
        public @NotNull String format(@NotNull Lang lang) {
            return this.value;
        }

    }

    //

    final class Keyed implements Translatable {

        private final String key;
        private final Object[] args;

        Keyed(@NotNull String key) {
            this.key = key;
            this.args = new Object[0];
        }

        private Keyed(@NotNull String key, @NotNull Object @NotNull [] args) {
            this.key = key;
            this.args = Arrays.copyOf(args, args.length);
        }

        //

        public @NotNull String key() {
            return this.key;
        }

        public @NotNull Object @NotNull [] args() {
            return Arrays.copyOf(this.args, this.args.length);
        }

        //

        @Override
        public @NotNull String format(@NotNull Lang lang) {
            return lang.get(this.key, this.args);
        }

        @Override
        public @NotNull Translatable with(@NotNull Object @NotNull ... args) {
            return new Keyed(this.key, args);
        }

    }

    //

    final class Concat implements Translatable {

        private final Translatable[] sub;
        private Concat(@NotNull Translatable @NotNull [] sub) {
            this.sub = sub;
        }

        Concat(@NotNull Translatable a, @NotNull Translatable b) {
            this(new Translatable[] { a, b });
        }

        //

        @Override
        public @NotNull String format(@NotNull Lang lang) {
            StringBuilder ret = new StringBuilder();
            for (Translatable sub : this.sub) {
                ret.append(sub.format(lang));
            }
            return ret.toString();
        }

        @Override
        public @NotNull Translatable concat(@NotNull Translatable other) {
            Translatable[] combined;

            if (other instanceof Concat concat) {
                Translatable[] flat = concat.sub;
                combined = new Translatable[this.sub.length + flat.length];
                System.arraycopy(flat, 0, combined, this.sub.length, flat.length);
            } else {
                combined = new Translatable[this.sub.length + 1];
                combined[this.sub.length] = other;
            }
            System.arraycopy(this.sub, 0, combined, 0, this.sub.length);

            return new Concat(combined);
        }

    }

}
