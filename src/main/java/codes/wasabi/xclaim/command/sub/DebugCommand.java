package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.ChoiceType;
import codes.wasabi.xclaim.command.argument.type.LazyType;
import codes.wasabi.xclaim.debug.Debug;
import codes.wasabi.xclaim.debug.goal.DebugGoalInstance;
import codes.wasabi.xclaim.debug.writer.DebugWriter;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.ProxyList;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class DebugCommand implements Command {

    private final Argument goalArg = new Argument(
            new LazyType<>(() -> new ChoiceType(
                    new ProxyList<>(
                            Debug.getGoals(),
                            DebugGoalInstance::label
                    )
            )),
            "goal",
            ""
    );

    @Override
    public @NotNull String getName() {
        return "debug";
    }

    @Override
    public @NotNull String getDescription() {
        return "";
    }

    @Override
    public @NotNull Argument @NotNull [] getArguments() {
        return new Argument[] { this.goalArg };
    }

    @Override
    public @Range(from = 0, to = Integer.MAX_VALUE) int getNumRequiredArguments() {
        return 1;
    }

    @Override
    public boolean requiresPlayerExecutor() {
        return false;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) throws Exception {
        final String label = (String) arguments[0];
        DebugGoalInstance goal = Debug.getGoalByLabel(label);
        Audience audience = Platform.getAdventure().sender(sender);

        if (goal == null) {
            audience.sendMessage(Component.text("* Invalid goal").color(NamedTextColor.RED));
            return;
        }

        goal.execute(DebugWriter.of(audience));
    }

}
