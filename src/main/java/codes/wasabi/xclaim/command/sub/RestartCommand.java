package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.ChoiceType;
import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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
        return XClaim.lang.get("cmd-restart-name");
    }

    @Override
    public @NotNull String getDescription() {
        return XClaim.lang.get("cmd-restart-description");
    }

    private final Argument[] args = new Argument[] {
            new Argument(
                    new ChoiceType(
                            XClaim.lang.get("cmd-restart-arg-confirm-yes"),
                            XClaim.lang.get("cmd-restart-arg-confirm-no")
                    ),
                    "confirm",
                    XClaim.lang.get("cmd-restart-arg-confirm-description")
            )
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
        Audience audience = Platform.getAdventure().sender(sender);
        if (!(sender.hasPermission("xclaim.restart") || sender.isOp())) {
            audience.sendMessage(XClaim.lang.getComponent("cmd-restart-err-perm"));
            return;
        }
        boolean confirmed = false;
        if (arguments.length > 0) {
            String yesno = (String) arguments[0];
            if (!Objects.equals(yesno, null)) {
                if (yesno.equalsIgnoreCase(XClaim.lang.get("cmd-restart-arg-confirm-yes"))) confirmed = true;
            }
        }
        if (confirmed) {
            PluginManager pm = Bukkit.getPluginManager();
            audience.sendMessage(XClaim.lang.getComponent("cmd-restart-status-disabling"));
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
                audience.sendMessage(XClaim.lang.getComponent("cmd-restart-warn-pm"));
            }
            audience.sendMessage(XClaim.lang.getComponent("cmd-restart-status-enabling"));
            Plugin plugin;
            try {
                plugin = Objects.requireNonNull(pm.loadPlugin(jarFile));
            } catch (Exception e) {
                e.printStackTrace();
                audience.sendMessage(XClaim.lang.getComponent("cmd-restart-err-load"));
                return;
            }
            try {
                pm.enablePlugin(plugin);
            } catch (Exception e) {
                audience.sendMessage(XClaim.lang.getComponent("cmd-restart-err-enable"));
                return;
            }
            audience.sendMessage(XClaim.lang.getComponent("cmd-restart-status-success", plugin.getDescription().getVersion()));
        } else {
            boolean isPlayer = (sender instanceof Player);
            audience.sendMessage(Component.empty()
                    .append(XClaim.lang.getComponent("cmd-restart-warn-header"))
                    .append(isPlayer ? Component.newline() : Component.text(" "))
                    .append(XClaim.lang.getComponent("cmd-restart-warn-body"))
            );
            if (isPlayer) {
                audience.sendMessage(Component.empty()
                        .append(XClaim.lang.getComponent("cmd-restart-confirm-player-pre"))
                        .append(XClaim.lang.getComponent("cmd-restart-confirm-player-click").clickEvent(ClickEvent.runCommand("/xclaim restart " + XClaim.lang.get("cmd-restart-arg-confirm-yes"))))
                        .append(XClaim.lang.getComponent("cmd-restart-confirm-player-post"))
                );
            } else {
                audience.sendMessage(XClaim.lang.getComponent("cmd-restart-confirm-console"));
            }
        }
    }

}
