package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.StandardTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.Set;

public class ListCommand implements Command {

    @Override
    public @NotNull String getName() {
        return "list";
    }

    @Override
    public @NotNull String getDescription() {
        return "Lists the claims of the specified player";
    }

    private final Argument[] args = new Argument[] {
            new Argument(StandardTypes.OFFLINE_PLAYER, "player", "The player to list the claims of, or yourself if not specified and you are a player"),
            new Argument(StandardTypes.INTEGER, "max chunks", "The maximum chunks to show from each claim, defaults to 3")
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
    public void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) throws Exception {
        OfflinePlayer op = null;
        if (arguments.length > 0) {
            op = (OfflinePlayer) arguments[0];
        }
        if (op == null) {
            if (sender instanceof Player p) {
                op = p;
            } else {
                sender.sendMessage(Component.text("* You need to specify a player (you are not a player)!").color(NamedTextColor.RED));
                return;
            }
        }
        Component ret = Component.empty();
        int maxChunks = 3;
        if (arguments.length > 1) {
            maxChunks = Math.max((Integer) Objects.requireNonNullElse(arguments[1], 3), 0);
        }
        Set<Claim> claims = Claim.getByOwner(op);
        if (claims.size() > 0) {
            int i = 0;
            for (Claim c : claims) {
                if (i > 0) {
                    ret = ret.append(Component.newline()).append(Component.newline());
                }
                ret = ret.append(Component.text("Claim #" + (i + 1) + ": " + c.getName()).color(NamedTextColor.DARK_PURPLE));
                Set<Chunk> chunks = c.getChunks();
                int count = Math.min(maxChunks, chunks.size());
                int z = 0;
                for (Chunk chunk : chunks) {
                    if (z >= count) break;
                    Location cornerLoc = chunk.getBlock(0, chunk.getWorld().getMinHeight(), 0).getLocation();
                    ret = ret.append(Component.newline());
                    ret = ret.append(Component.text("  Chunk at X=" + cornerLoc.getBlockX() + ", Z=" + cornerLoc.getBlockZ()).color(NamedTextColor.LIGHT_PURPLE));
                    z++;
                }
                if (maxChunks > 0) {
                    int remaining = chunks.size() - count;
                    if (remaining > 0) {
                        ret = ret.append(Component.newline());
                        ret = ret.append(Component.text("  and " + remaining + " more...").color(NamedTextColor.DARK_GRAY));
                    }
                }
                i++;
            }
        } else {
            ret = ret.append(op instanceof Player p ? p.displayName() : Component.text(Objects.requireNonNullElse(op.getName(), "Unknown")));
            ret = ret.append(Component.text(" has no claims").color(NamedTextColor.DARK_GRAY));
        }
        sender.sendMessage(ret);
    }

}
