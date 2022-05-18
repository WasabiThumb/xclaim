package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.ChoiceType;
import codes.wasabi.xclaim.command.argument.type.ComboType;
import codes.wasabi.xclaim.command.argument.type.RangeType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
        return "Provides a list of possible commands or detailed info for a specific command";
    }

    @Override
    public @NotNull Argument @NotNull [] getArguments() {
        return new Argument[] {
                new Argument(
                        new ComboType(
                                new RangeType(1, (int) Math.floor(Math.max(commands.size() - 1, 0) / 10d) + 1),
                                new ChoiceType(commands.stream().map(Command::getName).toArray(String[]::new))
                        ),
                        "Page number or command name",
                        "The page of help to view, or the name of the command to view detailed information about"
                )
        };
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
            Object arg = arguments[0];
            if (arg instanceof Integer num){
                pageNum = Objects.requireNonNullElse(num, pageNum);
            } else if (arg instanceof String str) {
                Optional<Command> opt = commands.stream().filter((Command c) -> c.getName().equalsIgnoreCase(str)).findFirst();
                if (opt.isEmpty()) {
                    sender.sendMessage(Component.text("* Can't find that command").color(NamedTextColor.RED));
                } else {
                    Command com = opt.get();
                    int numRequired = com.getNumRequiredArguments();
                    Argument[] args = com.getArguments();
                    StringBuilder argNames = new StringBuilder();
                    Component argDefs = Component.empty();
                    for (int i=0; i < args.length; i++) {
                        boolean required = i < numRequired;
                        Argument _a = args[i];
                        argNames
                                .append(" ")
                                .append(required ? '<' : '[')
                                .append(_a.name().toLowerCase(Locale.ROOT))
                                .append(required ? '>' : ']');
                        if (i > 0) {
                            argDefs = argDefs.append(Component.newline()).append(Component.newline());
                        }
                        argDefs = argDefs
                                .append(Component.text(_a.name().toLowerCase(Locale.ROOT) + " ").color(NamedTextColor.LIGHT_PURPLE))
                                .append(Component.text("(" + _a.type().getTypeName().toLowerCase(Locale.ROOT) + ")").color(NamedTextColor.DARK_PURPLE))
                                .append(Component.newline())
                                .append(Component.text(_a.description()).color(NamedTextColor.DARK_AQUA));
                    }
                    String exec = "/xclaim " + com.getName() + argNames;
                    Component component = Component.empty()
                            .append(Component.text(exec).color(NamedTextColor.GOLD).decorate(TextDecoration.UNDERLINED).clickEvent(ClickEvent.suggestCommand(exec)))
                            .append(Component.newline())
                            .append(Component.text(com.getDescription()).color(NamedTextColor.YELLOW))
                            .append(Component.newline());
                    if (args.length > 0) {
                        component = component.append(Component.newline()).append(argDefs);
                    } else {
                        component = component.append(Component.text("No arguments").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));
                    }
                    sender.sendMessage(component);
                }
                return;
            }
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
