package codes.wasabi.xclaim.command;

import codes.wasabi.xclaim.command.argument.Argument;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Collection;

public interface Command {

    @NotNull String getName();
    @NotNull String getDescription();
    @NotNull Argument @NotNull [] getArguments();
    @Range(from=0, to=Integer.MAX_VALUE) int getNumRequiredArguments();
    boolean requiresPlayerExecutor();
    default void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) throws Exception {}
    default void execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull Object @NotNull ... arguments) throws Exception {
        this.execute(sender, arguments);
    }
    default @Nullable Collection<Command> getSubCommands() {
        return null;
    }

}
