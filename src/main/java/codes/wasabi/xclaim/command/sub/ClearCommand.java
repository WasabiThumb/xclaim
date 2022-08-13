package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.XClaim;
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
        return XClaim.lang.get("cmd-clear-name");
    }

    @Override
    public @NotNull String getDescription() {
        return XClaim.lang.get("cmd-clear-description");
    }

    private final Argument[] args = new Argument[] {
            new Argument(StandardTypes.OFFLINE_PLAYER, "player", XClaim.lang.get("cmd-clear-arg-player-description")),
            new Argument(
                    new ChoiceType(
                            XClaim.lang.get("cmd-clear-arg-confirm-yes"),
                            XClaim.lang.get("cmd-clear-arg-confirm-no")
                    ),
                    "confirm",
                    XClaim.lang.get("cmd-clear-arg-confirm-description")
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
        OfflinePlayer target;
        Object protoPlayer = (arguments.length < 1 ? null : arguments[0]);
        boolean permitted = false;
        if (protoPlayer != null) {
            target = (OfflinePlayer) arguments[0];
            if (sender instanceof Player) {
                permitted = ((Player) sender).getUniqueId().equals(target.getUniqueId());
            }
        } else {
            if (sender instanceof Player) {
                target = (Player) sender;
                permitted = true;
            } else {
                audience.sendMessage(XClaim.lang.getComponent("cmd-clear-err-missing"));
                return;
            }
        }
        if (!permitted) {
            if (!(sender.hasPermission("xclaim.clear") || sender.isOp())) {
                audience.sendMessage(XClaim.lang.getComponent("cmd-clear-err-perm"));
                return;
            }
        }
        Object protoConfirm = (arguments.length < 2 ? null : arguments[1]);
        if (protoConfirm != null) {
            if (((String) protoConfirm).equalsIgnoreCase(XClaim.lang.get("cmd-clear-arg-confirm-yes"))) {
                Component name = (target instanceof Player ? Platform.get().playerDisplayName((Player) target) : Component.text(Objects.requireNonNullElse(target.getName(), XClaim.lang.get("cmd-clear-player-unknown"))));
                Claim.getByOwner(target).forEach(Claim::unclaim);
                audience.sendMessage(XClaim.lang.getComponent("cmd-clear-success", name));
                if ((!permitted) && target instanceof Player) {
                    Component name2 = (sender instanceof Player ? Platform.get().playerDisplayName((Player) sender) : Component.text(XClaim.lang.get("cmd-clear-player-console")).color(NamedTextColor.DARK_GRAY));
                    Platform.getAdventure().player((Player) target).sendMessage(XClaim.lang.getComponent("cmd-clear-notify", name2));
                }
                return;
            }
        }
        Component name3 = (target instanceof Player ? Platform.get().playerDisplayName((Player) target) : Component.text(Objects.requireNonNullElse(target.getName(), "Unknown")));
        audience.sendMessage(XClaim.lang.getComponent("cmd-clear-prompt", name3));
        if (sender instanceof Player) {
            audience.sendMessage(
                    Component.empty()
                            .append(XClaim.lang.getComponent("cmd-clear-prompt-player-pre"))
                            .append(XClaim.lang.getComponent("cmd-clear-prompt-player-click").clickEvent(ClickEvent.runCommand("/xc clear " + target.getName() + " " + XClaim.lang.get("cmd-clear-arg-confirm-yes"))))
                            .append(XClaim.lang.getComponent("cmd-clear-prompt-player-post"))
            );
        } else {
            audience.sendMessage(XClaim.lang.getComponent(
                    "cmd-clear-prompt-console",
                    "/xc clear " + target.getName() + " " + XClaim.lang.get("cmd-clear-arg-confirm-yes")
            ));
        }
    }

}
