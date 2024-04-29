package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.ChoiceType;
import codes.wasabi.xclaim.platform.Platform;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

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
            Object[] removalSources = getRemovalSources(pm);

            audience.sendMessage(XClaim.lang.getComponent("cmd-restart-status-disabling"));
            File jarFile = XClaim.jarFile;
            HandlerList.unregisterAll(XClaim.instance);
            pm.disablePlugin(XClaim.instance);

            Exception ex = null;
            boolean any = false;
            for (int i=0; i < removalSources.length; i++) {
                try {
                    this.tryRemoveFromPluginManager(removalSources[i]);
                    any = true;
                } catch (Exception ex1) {
                    if (ex != null) ex1.addSuppressed(ex);
                    if ((!any) && i == (removalSources.length - 1)) {
                        ex1.printStackTrace();
                        audience.sendMessage(XClaim.lang.getComponent("cmd-restart-warn-pm"));
                    } else {
                        ex = ex1;
                    }
                }
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

    @NotNull
    private static Object[] getRemovalSources(PluginManager pm) {
        Object[] removalSources = new Object[] { pm, null, null };
        int len = 1;
        if (PaperLib.isPaper()) {
            try {
                Field subField = pm.getClass().getDeclaredField("paperPluginManager");
                subField.setAccessible(true);
                Object ppm = subField.get(pm);
                removalSources[len++] = ppm;

                Field field = ppm.getClass().getDeclaredField("instanceManager");
                field.setAccessible(true);
                Object instanceManager = field.get(ppm);
                removalSources[len++] = instanceManager;
            } catch (Exception ignored) { }
        }
        if (len < 3) {
            Object[] shrunk = new Object[len];
            System.arraycopy(removalSources, 0, shrunk, 0, len);
            return shrunk;
        }
        return removalSources;
    }

    private void tryRemoveFromPluginManager(Object source) throws Exception {
        Class<?> clazz = source.getClass();
        boolean any = false;
        Exception e = null;

        try {
            Field field1 = clazz.getDeclaredField("plugins");
            field1.setAccessible(true);
            List<?> list = (List<?>) field1.get(source);
            list.remove(XClaim.instance);
            any = true;
        } catch (Exception e1) {
            e = e1;
        }

        try {
            Field field3 = clazz.getField("lookupNames");
            field3.setAccessible(true);
            Map<?, ?> lookupNames = (Map<?, ?>) field3.get(source);
            lookupNames.remove(XClaim.instance.getName().toLowerCase(Locale.ENGLISH));
            any = true;
        } catch (Exception e2) {
            if (e != null) e2.addSuppressed(e);
            e = e2;
        }

        if (!any) throw e;
    }

}
