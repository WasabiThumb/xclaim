package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.ChoiceType;
import codes.wasabi.xclaim.command.argument.type.StandardTypes;
import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Objects;

public class ClearCommand implements Command {

    @Override
    public @NotNull String getName() {
        return "clear";
    }

    @Override
    public @NotNull String getDescription() {
        return "Removes all existing claims for a player";
    }

    private final Argument[] args = new Argument[] {
            new Argument(StandardTypes.OFFLINE_PLAYER, "player", "The player to clear the claims of, or yourself if not specified and you are a player"),
            new Argument(new ChoiceType("yes", "no"), "confirm", "If yes, then this command will execute without confirmation")
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
        OfflinePlayer target;
        Object protoPlayer = (arguments.length < 1 ? null : arguments[0]);
        boolean permitted = false;
        if (protoPlayer != null) {
            target = (OfflinePlayer) arguments[0];
            if (sender instanceof Player ply) {
                permitted = ply.getUniqueId().equals(target.getUniqueId());
            }
        } else {
            if (sender instanceof Player ply) {
                target = ply;
                permitted = true;
            } else {
                audience.sendMessage(Component.text("* You need to specify a player (you are not a player)!").color(NamedTextColor.RED));
                return;
            }
        }
        if (!permitted) {
            if (!(sender.hasPermission("xclaim.clear") || sender.isOp())) {
                audience.sendMessage(Component.text("* You don't have permission to clear other players' commands!").color(NamedTextColor.RED));
                return;
            }
        }
        Object protoConfirm = (arguments.length < 2 ? null : arguments[1]);
        if (protoConfirm != null) {
            if (((String) protoConfirm).equalsIgnoreCase("yes")) {
                Claim.getByOwner(target).forEach(Claim::unclaim);
                audience.sendMessage(Component.empty()
                        .append(Component.text("* Cleared all of ").color(NamedTextColor.GREEN))
                        .append(target instanceof Player targetPly ? Platform.get().playerDisplayName(targetPly) : Component.text(Objects.requireNonNullElse(target.getName(), "Unknown")))
                        .append(Component.text("'s claims").color(NamedTextColor.GREEN))
                );
                if ((!permitted) && target instanceof Player targetPly) {
                    Platform.getAdventure().player(targetPly).sendMessage(
                            Component.empty()
                                    .append(Component.text("* Your claims were cleared by ").color(NamedTextColor.GRAY))
                                    .append(sender instanceof Player ply ? Platform.get().playerDisplayName(ply) : Component.text("CONSOLE").color(NamedTextColor.DARK_GRAY))
                    );
                }
                return;
            }
        }
        audience.sendMessage(
                Component.empty()
                        .append(Component.text("Are you sure you want to clear all of ").color(NamedTextColor.RED))
                        .append(target instanceof Player targetPly ? Platform.get().playerDisplayName(targetPly) : Component.text(Objects.requireNonNullElse(target.getName(), "Unknown")))
                        .append(Component.text("'s claims?").color(NamedTextColor.RED))
        );
        if (sender instanceof Player) {
            audience.sendMessage(
                    Component.empty()
                            .append(Component.text("Click ").color(NamedTextColor.GOLD))
                            .append(Component.text("here").color(NamedTextColor.YELLOW).clickEvent(ClickEvent.runCommand("/xc clear " + target.getName() + " yes")).decorate(TextDecoration.UNDERLINED))
                            .append(Component.text(" to confirm").color(NamedTextColor.GOLD))
            );
        } else {
            audience.sendMessage(
                    Component.empty()
                            .append(Component.text("Run ").color(NamedTextColor.GOLD))
                            .append(Component.text("/xc clear " + target.getName() + " yes").color(NamedTextColor.YELLOW))
                            .append(Component.text(" to confirm").color(NamedTextColor.GOLD))
            );
        }
    }

}
