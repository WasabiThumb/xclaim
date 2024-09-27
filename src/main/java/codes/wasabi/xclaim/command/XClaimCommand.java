package codes.wasabi.xclaim.command;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.sub.*;
import codes.wasabi.xclaim.debug.Debug;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class XClaimCommand implements Command {

    private final Collection<Command> subCommands;
    private final Command guiCommmand;
    public XClaimCommand() {
        this.guiCommmand = new GUICommand();
        HelpCommand helpCommand = new HelpCommand();
        this.subCommands = allocateSubCommands(helpCommand, this.guiCommmand);
        helpCommand.setCommands(this.subCommands);
    }

    private static @NotNull List<Command> allocateSubCommands(HelpCommand help, Command gui) {
        Command[] sub = new Command[] {
                help, gui,
                // START Subcommands
                new InfoCommand(),
                new CurrentCommand(),
                new UpdateCommand(),
                new RestartCommand(),
                new ChunksCommand(),
                new ClearCommand(),
                new ListCommand(),
                // END Subcommands
                new DebugCommand()
        };
        List<Command> ret = Arrays.asList(sub);
        if (!Debug.isEnabled()) ret = ret.subList(0, ret.size() - 1);
        return ret;
    }

    @Override
    public @NotNull String getName() {
        return "xclaim";
    }

    @Override
    public @NotNull String getDescription() {
        return XClaim.lang.get("cmd-xc-description");
    }

    private final Argument[] args = new Argument[0];

    @Override
    public @NotNull Argument @NotNull [] getArguments() {
        return args;
    }

    @Override
    public int getNumRequiredArguments() {
        return 0;
    }

    @Override
    public boolean requiresPlayerExecutor() {
        return true;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull Object @NotNull ... arguments) throws Exception {
        guiCommmand.execute(sender, alias, arguments);
    }

    @Override
    public @Nullable Collection<Command> getSubCommands() {
        return subCommands;
    }

}
