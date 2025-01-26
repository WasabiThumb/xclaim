package io.github.wasabithumb.xclaim.command;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

public interface Command<A extends Record> {

    @NotNull Translatable name();

    @NotNull Translatable description();

    @NotNull Class<A> argsClass();

    void execute(@NotNull XClaim runtime, @NotNull PlatformUser user, @NotNull A args);

    @Contract(" -> new")
    default @NotNull A createNewArgs() {
        Class<A> clazz = this.argsClass();
        try {
            Constructor<A> con = clazz.getConstructor();
            return con.newInstance();
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new AssertionError("Failed to invoke primary constructor of args class", e);
        }
    }

    default @NotNull @Unmodifiable List<Command<?>> subCommands() {
        return Collections.emptyList();
    }

    default boolean requiresPlayerExecutor() {
        return false;
    }

}
