package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.StandardTypes;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Objects;

public class ChunksCommand implements Command {

    @Override
    public @NotNull String getName() {
        return XClaim.lang.get("cmd-chunks-name");
    }

    @Override
    public @NotNull String getDescription() {
        return XClaim.lang.get("cmd-chunks-description");
    }

    private final Argument[] args = new Argument[] {
            new Argument(
                    StandardTypes.STRING,
                    XClaim.lang.get("cmd-chunks-arg-name"),
                    XClaim.lang.get("cmd-chunks-arg-description")
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
        return true;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) {
        Audience audience = Platform.getAdventure().sender(sender);
        Player ply = (Player) sender;
        if (ChunkEditor.getEditing(ply) != null) {
            audience.sendMessage(XClaim.lang.getComponent("cmd-chunks-err-state"));
        }
        Claim claim = null;
        if (arguments.length > 0) {
            String name;
            try {
                name = Objects.requireNonNull((String) arguments[0]);
                claim = Objects.requireNonNull(Claim.getByName(name));
            } catch (Exception ignored) { }
        }
        if (claim == null) {
            Location loc = ply.getLocation();
            for (Claim c : Claim.getAll()) {
                if (c.contains(loc)) {
                    claim = c;
                    break;
                }
            }
            if (claim == null) {
                audience.sendMessage(XClaim.lang.getComponent("cmd-chunks-err-404"));
                return;
            }
        }
        if (!claim.hasPermission(ply, Permission.MANAGE)) {
            audience.sendMessage(XClaim.lang.getComponent("cmd-chunks-err-perm"));
            return;
        }
        Platform.get().sendActionBar(ply, XClaim.lang.getComponent(
                "cmd-chunks-success",
                claim.getName()
        ));
        ply.playSound(ply.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
        ChunkEditor.startEditing(ply, claim);
    }

}
