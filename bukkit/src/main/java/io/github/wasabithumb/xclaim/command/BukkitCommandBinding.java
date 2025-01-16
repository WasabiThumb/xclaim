package io.github.wasabithumb.xclaim.command;

import io.github.wasabithumb.xclaim.XClaim;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BukkitCommandBinding implements CommandExecutor, TabCompleter {

    private XClaim runtime = null;

    //

    public synchronized void bind(@NotNull XClaim runtime) {
        this.runtime = runtime;
    }

    public synchronized void unbind() {
        this.runtime = null;
    }

    private synchronized @Nullable XClaim getRuntime() {
        return this.runtime;
    }

    //

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @Nullable Command command,
            @Nullable String label,
            @NotNull String[] args
    ) {
        XClaim runtime = this.getRuntime();
        if (runtime == null) return false;
        runtime.commands().execute(
                runtime.platform().adapter().user(sender),
                args
        );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @Nullable Command command,
            @Nullable String label,
            @NotNull String[] args
    ) {
        XClaim runtime = this.getRuntime();
        if (runtime == null) return null;
        return runtime.commands().suggest(
                runtime.platform().adapter().user(sender),
                args
        );
    }

}
