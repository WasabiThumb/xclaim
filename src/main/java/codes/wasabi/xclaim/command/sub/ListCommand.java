package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.StandardTypes;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.ChunkReference;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Set;

public class ListCommand implements Command {

    @Override
    public @NotNull String getName() {
        return XClaim.lang.get("cmd-list-name");
    }

    @Override
    public @NotNull String getDescription() {
        return XClaim.lang.get("cmd-list-description");
    }

    private final Argument[] args = new Argument[] {
            new Argument(StandardTypes.OFFLINE_PLAYER, "player", XClaim.lang.get("cmd-list-arg-player-description")),
            new Argument(StandardTypes.INTEGER, "max chunks", XClaim.lang.get("cmd-list-arg-chunks-description"))
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
        Audience audience = Platform.getAdventure().sender(sender);
        OfflinePlayer op = null;
        if (arguments.length > 0) {
            op = (OfflinePlayer) arguments[0];
        }
        if (op == null) {
            if (sender instanceof Player) {
                op = (Player) sender;
            } else {
                audience.sendMessage(XClaim.lang.getComponent("cmd-list-err-player"));
                return;
            }
        }
        Component ret = Component.empty();
        int maxChunks = 3;
        if (arguments.length > 1) {
            Object ob = arguments[1];
            int iv;
            if (ob != null) {
                iv = (Integer) ob;
            } else {
                iv = 3;
            }
            maxChunks = Math.max(iv, 0);
        }
        Set<Claim> claims = Claim.getByOwner(op);
        if (claims.size() > 0) {
            int i = 0;
            for (Claim c : claims) {
                if (i > 0) {
                    ret = ret.append(Component.newline()).append(Component.newline());
                }
                ret = ret.append(XClaim.lang.getComponent("cmd-list-claim-header", Integer.toString(i + 1), c.getName()));
                Set<ChunkReference> chunks = c.getChunks();
                int count = Math.min(maxChunks, chunks.size());
                int z = 0;
                for (ChunkReference chunk : chunks) {
                    if (z >= count) break;
                    Location cornerLoc = chunk.getLocation(0, Platform.get().getWorldMinHeight(chunk.world), 0);
                    ret = ret.append(Component.newline());
                    ret = ret.append(Component.text("  "));
                    ret = ret.append(XClaim.lang.getComponent("cmd-list-claim-chunk", cornerLoc.getBlockX(), cornerLoc.getBlockZ()));
                    z++;
                }
                if (maxChunks > 0) {
                    int remaining = chunks.size() - count;
                    if (remaining > 0) {
                        ret = ret.append(Component.newline());
                        ret = ret.append(Component.text("  "));
                        ret = ret.append(XClaim.lang.getComponent("cmd-list-claim-more", remaining));
                    }
                }
                i++;
            }
        } else {
            Component name;
            if (op instanceof Player) {
                name = Platform.get().playerDisplayName((Player) op);
            } else {
                String bare = op.getName();
                if (bare == null) bare = XClaim.lang.get("unknown");
                name = Component.text(bare);
            }
            ret = ret.append(XClaim.lang.getComponent("cmd-list-none", name));
        }
        audience.sendMessage(ret);
    }

}
