package io.github.wasabithumb.xclaim.util.identity;

import io.github.wasabithumb.xclaim.claim.struct.Permission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public sealed interface Identity {

    static @NotNull Identity real(@NotNull Player handle) {
        return new Real(handle);
    }

    static @NotNull Fake.Builder fake() {
        return new Fake.Builder();
    }

    //

    @NotNull UUID uuid();

    @NotNull String name();

    @NotNull String displayName();

    boolean isOp();

    boolean hasPermission(@NotNull String permission);

    boolean isReal();

    //

    record Real(
            @NotNull Player handle
    ) implements Identity {

        private static final MiniMessage MM = MiniMessage.miniMessage();

        @Override
        public @NotNull UUID uuid() {
            return this.handle.getUniqueId();
        }

        @Override
        public @NotNull String name() {
            return this.handle.getName();
        }

        @Override
        public @NotNull String displayName() {
            return MM.serialize(this.handle.displayName());
        }

        @Override
        public boolean isOp() {
            return this.handle.isOp();
        }

        @Override
        public boolean hasPermission(@NotNull String permission) {
            return this.handle.hasPermission(permission);
        }

        @Override
        public boolean isReal() {
            return true;
        }

    }

    //

    final class Fake implements Identity {

        private final UUID uuid;
        private final String name;
        private final boolean op;
        private final boolean admin;

        public Fake(@NotNull String name, boolean op, boolean admin) {
            this.uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
            this.name = name;
            this.op = op;
            this.admin = admin;
        }

        @Override
        public @NotNull UUID uuid() {
            return this.uuid;
        }

        @Override
        public @NotNull String name() {
            return this.name;
        }

        @Override
        public @NotNull String displayName() {
            return "<b><grey>[<dark_purple>FAKE</dark_purple>]</grey></b> <light_purple><i>" + this.name + "</i></light_purple>";
        }

        @Override
        public boolean isOp() {
            return this.op;
        }

        @Override
        public boolean hasPermission(@NotNull String permission) {
            return this.admin && permission.equals(Permission.ADMIN_OVERRIDE);
        }

        @Override
        public boolean isReal() {
            return false;
        }

        //

        public static final class Builder {

            private String name = null;
            private boolean op = false;
            private boolean admin = false;

            //

            @Contract("_ -> this")
            public @NotNull Builder name(@NotNull String name) {
                this.name = name;
                return this;
            }

            @Contract("_ -> this")
            public @NotNull Builder op(boolean op) {
                this.op = op;
                return this;
            }

            @Contract("_ -> this")
            public @NotNull Builder admin(boolean admin) {
                this.admin = admin;
                return this;
            }

            @Contract("-> new")
            public @NotNull Fake build() {
                if (this.name == null)
                    throw new IllegalStateException("Cannot create identity without name");
                return new Fake(this.name, this.op, this.admin);
            }

        }

    }

}
