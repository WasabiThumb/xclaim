package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.ChoiceType;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.AutoUpdater;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class UpdateCommand implements Command {

    private static final Map<UUID, Optional<AutoUpdater.UpdateOption>> map = new ConcurrentHashMap<>();
    public static String initialCheck() {
        AutoUpdater.UpdateOption opt;
        try {
            opt = AutoUpdater.check();
        } catch (Exception ignored) {
            return null;
        }
        if (opt == null) return null;
        map.put(new UUID(0L, 0L), Optional.of(opt));
        return opt.updateOption();
    }

    public UpdateCommand() {
        map.clear();
    }

    @Override
    public @NotNull String getName() {
        return XClaim.lang.get("cmd-update-name");
    }

    @Override
    public @NotNull String getDescription() {
        return XClaim.lang.get("cmd-update-description");
    }

    private final Argument[] args = new Argument[] {
            new Argument(
                    new ChoiceType(
                            XClaim.lang.get("cmd-update-arg-proceed-yes"),
                            XClaim.lang.get("cmd-update-arg-proceed-no")
                    ),
                    "proceed",
                    XClaim.lang.get("cmd-update-arg-proceed-description")
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
    public void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) {
        Audience audience = Platform.getAdventure().sender(sender);
        if (!(sender.hasPermission("xclaim.update") || sender.isOp())) {
            audience.sendMessage(XClaim.lang.getComponent("cmd-update-err-perms"));
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean console = true;
            boolean permitted = true;
            UUID uuid = new UUID(0L, 0L);
            if (sender instanceof Player) {
                Player ply = (Player) sender;
                console = false;
                permitted = ply.isOp() || ply.hasPermission("xclaim.update");
                uuid = ply.getUniqueId();
            }
            if (!permitted) {
                audience.sendMessage(XClaim.lang.getComponent("cmd-update-err-perms2"));
            }
            String yesno = null;
            if (arguments.length > 0) {
                yesno = (String) arguments[0];
            }
            if (yesno != null) {
                if (yesno.equalsIgnoreCase(XClaim.lang.get("cmd-update-arg-proceed-no"))) {
                    map.remove(uuid);
                    audience.sendMessage(XClaim.lang.getComponent("cmd-update-declined"));
                    return;
                }
                AutoUpdater.UpdateOption opt;
                if (!map.containsKey(uuid)) {
                    audience.sendMessage(XClaim.lang.getComponent("cmd-update-searching"));
                    try {
                        opt = AutoUpdater.check();
                    } catch (Exception e) {
                        e.printStackTrace();
                        audience.sendMessage(XClaim.lang.getComponent("cmd-update-err-check"));
                        return;
                    }
                } else {
                    opt = map.get(uuid).orElse(null);
                }
                if (opt == null) {
                    audience.sendMessage(XClaim.lang.getComponent("cmd-update-none"));
                    return;
                }
                audience.sendMessage(XClaim.lang.getComponent("cmd-update-start"));
                try {
                    opt.update();
                } catch (Exception e) {
                    e.printStackTrace();
                    audience.sendMessage(XClaim.lang.getComponent("cmd-update-err-unexpected"));
                }
                audience.sendMessage(XClaim.lang.getComponent("cmd-update-success"));
                if (sender instanceof Player) {
                    audience.sendMessage(Component.empty()
                            .append(XClaim.lang.getComponent("cmd-update-promote-restart-player-pre"))
                            .append(XClaim.lang.getComponent("cmd-update-promote-restart-player-click").clickEvent(ClickEvent.runCommand("/xc restart yes")))
                            .append(XClaim.lang.getComponent("cmd-update-promote-restart-player-post"))
                    );
                } else {
                    audience.sendMessage(XClaim.lang.getComponent("cmd-update-promote-restart-console"));
                }
            } else {
                AutoUpdater.UpdateOption opt;
                try {
                    audience.sendMessage(XClaim.lang.getComponent("cmd-update-searching"));
                    opt = AutoUpdater.check();
                } catch (Exception e) {
                    e.printStackTrace();
                    audience.sendMessage(XClaim.lang.getComponent("cmd-update-err-check"));
                    return;
                }
                map.put(uuid, Optional.ofNullable(opt));
                if (opt == null) {
                    audience.sendMessage(XClaim.lang.getComponent("cmd-update-redundant"));
                    return;
                }
                audience.sendMessage(XClaim.lang.getComponent("cmd-update-found", opt.updateOption()));
                if (console) {
                    audience.sendMessage(XClaim.lang.getComponent("cmd-update-confirm-console"));
                } else {
                    audience.sendMessage(Component.empty()
                            .append(Component.text("* " + XClaim.lang.get("cmd-update-confirm-player-prompt") + " (").color(NamedTextColor.GRAY))
                            .append(XClaim.lang.getComponent("cmd-update-confirm-player-yes").clickEvent(ClickEvent.runCommand("/xclaim update " + XClaim.lang.get("cmd-update-arg-proceed-yes"))))
                            .append(Component.text("/").color(NamedTextColor.GRAY))
                            .append(XClaim.lang.getComponent("cmd-update-confirm-player-no").clickEvent(ClickEvent.runCommand("/xclaim update " + XClaim.lang.get("cmd-update-arg-proceed-no"))))
                            .append(Component.text(")").color(NamedTextColor.GRAY))
                    );
                }
            }
        });
    }

}
