package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class CurrentCommand implements Command {

    @Override
    public @NotNull String getName() {
        return XClaim.lang.get("cmd-current-name");
    }

    @Override
    public @NotNull String getDescription() {
        return XClaim.lang.get("cmd-current-description");
    }

    @Override
    public @NotNull Argument @NotNull [] getArguments() {
        return new Argument[0];
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
    public void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) throws Exception {
        Audience audience = Platform.getAdventure().sender(sender);
        Player ply = (Player) sender;
        Location loc = ply.getLocation();
        Claim claim = null;
        for (Claim c : Claim.getAll()) {
            if (c.contains(loc)) {
                claim = c;
                break;
            }
        }
        if (claim == null) {
            audience.sendMessage(XClaim.lang.getComponent("cmd-current-err-404"));
            return;
        }
        Component ownerName;
        XCPlayer owner = claim.getOwner();
        Player player = owner.getPlayer();
        if (player != null) {
            ownerName = Platform.get().playerDisplayName(player);
        } else {
            String name = owner.getName();
            if (name == null) name = owner.getUniqueId().toString();
            ownerName = Component.text(name);
        }
        int chunkCount = claim.getChunks().size();
        String worldName = XClaim.lang.get("cmd-current-world-unset");
        World w = claim.getWorld();
        if (w != null) worldName = w.getName();
        audience.sendMessage(Component.empty()
                .append(XClaim.lang.getComponent("cmd-current-output-line1", claim.getName()))
                .append(Component.newline())
                .append(XClaim.lang.getComponent("cmd-current-output-line2", ownerName))
                .append(Component.newline())
                .append(XClaim.lang.getComponent("cmd-current-output-line3", worldName))
                .append(Component.newline())
                .append(
                        chunkCount == 1 ?
                                XClaim.lang.getComponent("cmd-current-output-line4-singular", chunkCount)
                                :
                                XClaim.lang.getComponent("cmd-current-output-line4-plural", chunkCount)
                )
        );
    }

}
