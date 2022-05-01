package codes.wasabi.xclaim.command;

import codes.wasabi.xclaim.command.argument.Argument;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface Command {

    @NotNull String getName();
    @NotNull String getDescription();
    @NotNull Argument @NotNull [] getArguments();
    @Range(from=0, to=Integer.MAX_VALUE) int getNumRequiredArguments();
    boolean requiresPlayerExecutor();
    void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) throws Exception;

}
