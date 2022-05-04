package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.IntType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.stream.Collectors;

public class HelpCommand implements Command {

    private Set<Command> commands = new LinkedHashSet<>();

    public void setCommands(@NotNull Collection<Command> commands) {
        this.commands = new LinkedHashSet<>(commands);
    }

    public void setCommands(@NotNull Command @NotNull ... commands) {
        this.commands = Arrays.stream(commands).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public boolean addCommand(@NotNull Command command) {
        return commands.add(command);
    }

    public boolean removeCommand(@NotNull Command command) {
        return commands.remove(command);
    }

    @Override
    public @NotNull String getName() {
        return "help";
    }

    @Override
    public @NotNull String getDescription() {
        return "Provides a list of possible commands";
    }

    private final Argument[] args = new Argument[] {
            new Argument(new IntType(), "Page number", "The page of help to view (starts at 1)")
    };
    @Override
    public @NotNull Argument @NotNull [] getArguments() {
        return args;
    }

    @Override
    public @Range(from = 0, to = Integer.MAX_VALUE) int getNumRequiredArguments() {
        return 0;
    }

    @Override
    public boolean requiresPlayerExecutor() {
        return false;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) throws Exception {
        int pageNum = 1;
        if (arguments.length > 0) {
            Integer arg = (Integer) arguments[0];
            pageNum = Objects.requireNonNullElse(arg, pageNum);
        }
        int maxPage = (int) Math.floor(Math.max(commands.size() - 1, 0) / 10d) + 1;
        pageNum = Math.max(Math.min(pageNum, maxPage), 1);
        Component ret = Component.empty();
        ret = ret.append(Component.text("= ").color(NamedTextColor.GOLD));
        ret = ret.append(Component.text("Page " + pageNum).color(NamedTextColor.YELLOW));
        ret = ret.append(Component.text(" =").color(NamedTextColor.GOLD));
        ret = ret.append(Component.newline());
        int fromIndex = (maxPage - 1) * 10;
        int toIndex = fromIndex + 10;
        int i = -1;
        for (Command cmd : commands) {
            i++;
            if (i < fromIndex) continue;
            if (i >= toIndex) break;
            ret = ret.append(Component.text(cmd.getName()).color(NamedTextColor.DARK_PURPLE));
            ret = ret.append(Component.text(" : ").color(NamedTextColor.DARK_AQUA));
            ret = ret.append(Component.text(cmd.getDescription())).color(NamedTextColor.LIGHT_PURPLE);
            ret = ret.append(Component.newline());
        }
        ret = ret.append(Component.text("= ").color(NamedTextColor.GOLD));
        ret = ret.append(Component.text("Page " + pageNum).color(NamedTextColor.YELLOW));
        ret = ret.append(Component.text(" =").color(NamedTextColor.GOLD));
        sender.sendMessage(ret);
    }

}
