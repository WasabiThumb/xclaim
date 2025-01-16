package io.github.wasabithumb.xclaim.command;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.command.argument.CommandArgument;
import io.github.wasabithumb.xclaim.command.argument.type.CommandArgumentType;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

@ApiStatus.Internal
final class CommandAdapter<A extends Record> {

    static <Q extends Record> @NotNull CommandAdapter<Q> create(
            @NotNull XClaim runtime,
            @NotNull Command<Q> command
    ) {
        Class<? extends Q> argsClass = command.argsClass();
        RecordComponent[] components = argsClass.getRecordComponents();
        assert components != null;
        int count = components.length;
        if (count == 0) {
            return new CommandAdapter<>(runtime, command, new Method[0], new CommandArgumentType<?>[0], 0, 0);
        }

        Method[] accessors = new Method[count];
        CommandArgumentType<?>[] types = new CommandArgumentType<?>[count];
        int min = 0;

        Q instance = command.createNewArgs();
        boolean allowRequired = true;
        Method accessor;
        Object value;
        for (int i=0; i < count; i++) {
            accessors[i] = accessor = components[i].getAccessor();
            try {
                value = accessor.invoke(instance);
            } catch (ReflectiveOperationException | SecurityException e) {
                throw new AssertionError("Failed to invoke record accessor", e);
            }

            if (!(value instanceof CommandArgument<?> arg)) {
                throw new AssertionError("Arguments record class " + argsClass.getName() +
                        " has component \"" + components[i].getName() + "\" that is not a CommandArgument");
            }

            types[i] = arg.type();

            if (arg.optional()) {
                allowRequired = false;
            } else {
                min++;
                if (!allowRequired)
                    throw new AssertionError("Arguments record class " + argsClass.getName() +
                            " has required argument \"" + components[i].getName() + "\" which follows an optional" +
                            " argument");
            }
        }

        return new CommandAdapter<>(
                runtime,
                command,
                accessors,
                types,
                min,
                count
        );
    }

    //

    private final XClaim runtime;
    private final Command<A> command;
    private final Method[] accessors;
    private final CommandArgumentType<?>[] types;
    private final int min;
    private final int max;

    private CommandAdapter(
            @NotNull XClaim runtime,
            @NotNull Command<A> command,
            @NotNull Method @NotNull [] accessors,
            @NotNull CommandArgumentType<?> @NotNull [] types,
            int min,
            int max
    ) {
        this.runtime = runtime;
        this.command = command;
        this.accessors = accessors;
        this.types = types;
        this.min = min;
        this.max = max;
    }

    public void execute(@NotNull PlatformUser user, @NotNull Queue<String> args) {
        A boxed = this.adapt(user, args);
        if (boxed == null) return;
        try {
            this.command.execute(this.runtime, user, boxed);
        } catch (Throwable t) {
            this.runtime.logger().log(Level.WARNING, "An error occurred while dispatching a command", t);
            user.sendMessage(this.runtime.lang("cmdmgr-err-unexpected", t.getClass().getSimpleName()));
        }
    }

    public @NotNull List<String> suggest(@NotNull PlatformUser user, int index) {
        index = Math.max(index - 1, 0);
        if (index >= this.max) return Collections.emptyList();

        CommandArgumentType<?> type = this.types[index];
        return type.suggest(this.runtime, user);
    }

    public @Nullable A adapt(@NotNull PlatformUser user, @NotNull Queue<String> args) {
        A boxed = this.command.createNewArgs();

        int count = 0;
        String next;
        while ((next = args.poll()) != null) {
            if (count >= this.max) {
                user.sendMessage(this.runtime.lang("cmdmgr-err-max-args", this.max));
                return null;
            }

            CommandArgumentType<?> type = this.types[count];
            CommandArgumentType.ParseResult<?> result = type.parse(this.runtime, user, next);
            if (!result.isSuccess()) {
                // TODO: The error message should be forwarded
                user.sendMessage(this.runtime.lang("cmdmgr-err-malformed"));
                return null;
            }

            Method accessor = this.accessors[count++];
            CommandArgument<?> arg;
            try {
                arg = (CommandArgument<?>) accessor.invoke(boxed);
            } catch (ReflectiveOperationException | SecurityException e) {
                throw new AssertionError("Failed to invoke record accessor", e);
            }
            arg.set(result.value());
        }

        if (count < this.min) {
            user.sendMessage(this.runtime.lang("cmdmgr-err-min-args", this.min));
            return null;
        }

        return boxed;
    }

}
