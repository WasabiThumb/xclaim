package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.ChoiceType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class RestartCommand implements Command {

    public RestartCommand() {

    }

    @Override
    public @NotNull String getName() {
        return "restart";
    }

    @Override
    public @NotNull String getDescription() {
        return "Restarts XClaim without restarting the server";
    }

    private final Argument[] args = new Argument[] {
            new Argument(new ChoiceType("yes", "no"), "confirm", "If yes, restarts without confirming")
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
    public void execute(@NotNull CommandSender sender, @Nullable Object @NotNull ... arguments) throws Exception {
        if (!(sender.hasPermission("xclaim.restart") || sender.isOp())) {
            sender.sendMessage(Component.text("* You don't have permission to run this command!").color(NamedTextColor.RED));
            return;
        }
        boolean confirmed = false;
        if (arguments.length > 0) {
            String yesno = (String) arguments[0];
            if (!Objects.equals(yesno, null)) {
                if (yesno.equalsIgnoreCase("yes")) confirmed = true;
            }
        }
        if (confirmed) {
            PluginManager pm = Bukkit.getPluginManager();
            sender.sendMessage(Component.text("Disabling XClaim...").color(NamedTextColor.GREEN));
            File jarFile = XClaim.jarFile;
            HandlerList.unregisterAll(XClaim.instance);
            pm.disablePlugin(XClaim.instance);
            try {
                if (pm instanceof SimplePluginManager spm) {
                    Class<? extends SimplePluginManager> clazz = spm.getClass();
                    Field field = clazz.getDeclaredField("plugins");
                    field.setAccessible(true);
                    List<?> list = (List<?>) field.get(spm);
                    list.remove(XClaim.instance);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(Component.text("Failed to remove XClaim from plugin manager. Continuing...").color(NamedTextColor.YELLOW));
            }
            sender.sendMessage(Component.text("Enabling XClaim...").color(NamedTextColor.GREEN));
            Plugin plugin;
            try {
                plugin = Objects.requireNonNull(pm.loadPlugin(jarFile));
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(Component.text("Failed to load XClaim").color(NamedTextColor.RED));
                return;
            }
            try {
                pm.enablePlugin(plugin);
            } catch (Exception e) {
                sender.sendMessage(Component.text("Failed to enable XClaim").color(NamedTextColor.RED));
                return;
            }
            sender.sendMessage(Component.text("Enabled XClaim version " + plugin.getDescription().getVersion()).color(NamedTextColor.GOLD));
        } else {
            boolean isPlayer = (sender instanceof Player);
            sender.sendMessage(Component.empty()
                    .append(Component.text("WARNING!").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .append(isPlayer ? Component.newline() : Component.text(" "))
                    .append(Component.text("This feature is ").color(NamedTextColor.RED))
                    .append(Component.text("experimental").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                    .append(Component.text(".").color(NamedTextColor.RED))
            );
            if (isPlayer) {
                sender.sendMessage(Component.empty()
                        .append(Component.text("Click ").color(NamedTextColor.YELLOW))
                        .append(Component.text("here").clickEvent(ClickEvent.runCommand("/xclaim restart yes")).color(NamedTextColor.GOLD))
                        .append(Component.text(" to continue anyway.").color(NamedTextColor.YELLOW))
                );
            } else {
                sender.sendMessage(Component.empty()
                        .append(Component.text("Run ").color(NamedTextColor.YELLOW))
                        .append(Component.text("/xclaim restart yes").color(NamedTextColor.GOLD))
                        .append(Component.text(" to continue anyway.").color(NamedTextColor.YELLOW))
                );
            }
        }
    }

}
