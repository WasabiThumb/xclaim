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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
            AtomicBoolean awaitEnabled = new AtomicBoolean(false);
            Executors.newSingleThreadExecutor().execute(() -> {
                boolean waiting = true;
                while (waiting) {
                    Plugin plugin = pm.getPlugin("XClaim");
                    if (plugin != null) {
                        if (awaitEnabled.get()) {
                            if (plugin.isEnabled()) {
                                sender.sendMessage(Component.text("Enabled XClaim version " + plugin.getDescription().getVersion()).color(NamedTextColor.GOLD));
                                break;
                            }
                        } else {
                            if (!plugin.isEnabled()) {
                                sender.sendMessage(Component.text("Enabling XClaim...").color(NamedTextColor.GREEN));
                                pm.enablePlugin(plugin);
                                awaitEnabled.set(true);
                            }
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1L);
                    } catch (InterruptedException e) {
                        sender.sendMessage(Component.text("Restart stopped abruptly! Is the server shutting down?").color(NamedTextColor.RED));
                        waiting = false;
                    }
                }
            });
            HandlerList.unregisterAll(XClaim.instance);
            pm.disablePlugin(XClaim.instance);
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
