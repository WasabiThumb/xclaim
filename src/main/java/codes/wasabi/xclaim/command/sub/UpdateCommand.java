package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.ChoiceType;
import codes.wasabi.xclaim.util.AutoUpdater;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

public class UpdateCommand implements Command {

    @Override
    public @NotNull String getName() {
        return "update";
    }

    @Override
    public @NotNull String getDescription() {
        return "Searches for updates for XClaim online";
    }

    private final Argument[] args = new Argument[] {
            new Argument(new ChoiceType("yes", "no"), "proceed", "Whether or not to proceed with the update once found")
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

    private final Map<UUID, AutoUpdater.UpdateOption> map = new HashMap<>();
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) {
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean console = true;
            boolean permitted = true;
            UUID uuid = new UUID(0L, 0L);
            if (sender instanceof Player ply) {
                console = false;
                permitted = ply.isOp() || ply.hasPermission("xclaim.update");
                uuid = ply.getUniqueId();
            }
            if (!permitted) {
                sender.sendMessage(Component.text("* You do not have permission to update XClaim!").color(NamedTextColor.RED));
            }
            String yesno = null;
            if (arguments.length > 0) {
                yesno = (String) arguments[0];
            }
            if (yesno != null) {
                if (yesno.equalsIgnoreCase("no")) {
                    map.remove(uuid);
                    sender.sendMessage(Component.text("* Declined update.").color(NamedTextColor.GREEN));
                    return;
                }
                AutoUpdater.UpdateOption opt;
                if (!map.containsKey(uuid)) {
                    sender.sendMessage(Component.text("* Looking for updates...").color(NamedTextColor.YELLOW));
                    try {
                        opt = AutoUpdater.check();
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage(Component.text("* Failed to find any version to update to. See console for more details.").color(NamedTextColor.RED));
                        return;
                    }
                } else {
                    opt = map.get(uuid);
                }
                if (opt == null) {
                    sender.sendMessage(Component.text("* No valid versions to update to found.").color(NamedTextColor.RED));
                    return;
                }
                sender.sendMessage(Component.text("* Installing update...").color(NamedTextColor.YELLOW));
                try {
                    opt.update();
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(Component.text("* Failed to update. See console for more details.").color(NamedTextColor.RED));
                }
                sender.sendMessage(Component.text("* Updated successfully! Changes will reflect on next restart. Restarting soon is recommended to avoid any unpredictable bugs").color(NamedTextColor.GREEN));
            } else {
                AutoUpdater.UpdateOption opt;
                try {
                    sender.sendMessage(Component.text("* Looking for updates...").color(NamedTextColor.YELLOW));
                    opt = AutoUpdater.check();
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(Component.text("* Failed to find any version to update to. See console for more details.").color(NamedTextColor.RED));
                    return;
                }
                map.put(uuid, opt);
                if (opt == null) {
                    sender.sendMessage(Component.text("* You are already using the latest compatible version of XClaim!").color(NamedTextColor.GREEN));
                    return;
                }
                sender.sendMessage(
                        Component.empty()
                                .append(Component.text("* Found version ").color(NamedTextColor.GREEN))
                                .append(Component.text(opt.updateOption()).color(NamedTextColor.GOLD))
                );
                if (console) {
                    sender.sendMessage(Component.text("* Use /xclaim update yes to install this version.").color(NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.empty()
                            .append(Component.text("* Install this version? (").color(NamedTextColor.GRAY))
                            .append(Component.text("Yes").color(NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("/xclaim update yes")))
                            .append(Component.text("/").color(NamedTextColor.GRAY))
                            .append(Component.text("No").color(NamedTextColor.RED).clickEvent(ClickEvent.runCommand("/xclaim update no")))
                            .append(Component.text(")").color(NamedTextColor.GRAY))
                    );
                }
            }
        });
    }

}
