package io.github.wasabithumb.xclaim.command;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.command.impl.RootCommand;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandManager {

    private final XClaim runtime;
    private final RootCommand root = new RootCommand();
    private final Map<Command<?>, CommandAdapter<?>> adapters = Collections.synchronizedMap(new HashMap<>());

    @ApiStatus.Internal
    public CommandManager(@NotNull XClaim runtime) {
        this.runtime = runtime;
    }

    //

    private @NotNull CommandAdapter<?> getAdapter(@NotNull Command<?> command) {
        return this.adapters.computeIfAbsent(
                command,
                (Command<?> ignored) -> CommandAdapter.create(this.runtime, command)
        );
    }

    //

    public void execute(@NotNull PlatformUser user, @NotNull String @NotNull ... args) {
        this.execute(user, new LinkedList<>(Arrays.asList(args)));
    }

    public void execute(@NotNull PlatformUser user, @NotNull Queue<String> args) {
        this.execute(this.root, user, args);
    }

    private void execute(@NotNull Command<?> root, @NotNull PlatformUser user, @NotNull Queue<String> args) {
        String next = args.peek();
        if (next != null) {
            Command<?> sub = null;
            for (Command<?> candidate : root.subCommands()) {
                if (candidate.name(this.runtime.lang()).equalsIgnoreCase(next)) {
                    sub = candidate;
                    break;
                }
            }
            if (sub != null) {
                args.poll();
                this.execute(sub, user, args);
                return;
            }
        }
        if (root.requiresPlayerExecutor() && !user.isPlayer()) {
            user.sendMessage(this.runtime.lang("cmdmgr-err-player"));
            return;
        }
        this.getAdapter(root)
                .execute(user, args);
    }

    //

    public @NotNull List<String> suggest(@NotNull PlatformUser user, @NotNull String @NotNull ... args) {
        return this.suggest(user, new LinkedList<>(Arrays.asList(args)));
    }

    public @NotNull List<String> suggest(@NotNull PlatformUser user, @NotNull Queue<String> args) {
        return this.suggest(this.root, user, args);
    }

    private @NotNull List<String> suggest(
            @NotNull Command<?> root,
            @NotNull PlatformUser user,
            @NotNull Queue<String> args
    ) {
        Set<String> set = new HashSet<>();

        String next = args.peek();
        if (next != null) {
            Command<?> sub = null;
            for (Command<?> candidate : root.subCommands()) {
                String name = candidate.name(this.runtime.lang());
                if (args.size() < 2) set.add(name);
                if (name.equalsIgnoreCase(next)) {
                    sub = candidate;
                    break;
                }
            }
            if (sub != null) {
                args.poll();
                return this.suggest(sub, user, args);
            }
        }

        set.addAll(this.getAdapter(root).suggest(user, args.size()));
        List<String> ret = new ArrayList<>(set);
        Collections.sort(ret);
        return ret;
    }

}
