package codes.wasabi.xclaim.command;

import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.sub.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

public class XClaimCommand implements Command {

    private final Collection<Command> subCommands;
    private final Command guiCommmand;
    public XClaimCommand() {
        guiCommmand = new GUICommand();
        HelpCommand helpCommand = new HelpCommand();
        subCommands = Arrays.asList(
                helpCommand,
                new InfoCommand(),
                new CurrentCommand(),
                new UpdateCommand(),
                new RestartCommand(),
                guiCommmand,
                new ChunksCommand()
        );
        helpCommand.setCommands(subCommands);
    }

    @Override
    public @NotNull String getName() {
        return "xclaim";
    }

    @Override
    public @NotNull String getDescription() {
        return "XClaim main command";
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
        if (alias.equalsIgnoreCase("claimgui") || alias.equalsIgnoreCase("claims") || alias.equalsIgnoreCase("cg")) {
            sender.sendMessage(Component.text("Warning: The /" + alias + " command is being replaced with /xclaim (alias: /xc) or /xclaim gui. This command may be deleted in a future update.").color(NamedTextColor.YELLOW));
        }
        guiCommmand.execute(sender, alias, arguments);
    }

    @Override
    public @Nullable Collection<Command> getSubCommands() {
        return subCommands;
    }

}
