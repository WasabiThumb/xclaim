package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class CurrentCommand implements Command {

    @Override
    public @NotNull String getName() {
        return "current";
    }

    @Override
    public @NotNull String getDescription() {
        return "Gets info about the current claim you are in";
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
            sender.sendMessage(Component.text("* You are not in a claim!").color(NamedTextColor.RED));
            return;
        }
        Component ownerName;
        OfflinePlayer owner = claim.getOwner();
        Player player = owner.getPlayer();
        if (player != null) {
            ownerName = player.displayName();
        } else {
            String name = owner.getName();
            if (name == null) name = owner.getUniqueId().toString();
            ownerName = Component.text(name);
        }
        int chunkCount = claim.getChunks().size();
        String worldName = "Unset";
        World w = claim.getWorld();
        if (w != null) worldName = w.getName();
        sender.sendMessage(Component.empty()
                .append(Component.text("= ").color(NamedTextColor.GOLD))
                .append(Component.text(claim.getName()).color(NamedTextColor.DARK_AQUA))
                .append(Component.text(" =").color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("Owned by ").color(NamedTextColor.DARK_GRAY))
                .append(ownerName.color(NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.text("In world ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(worldName).color(NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.text("With ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(chunkCount).color(NamedTextColor.GRAY))
                .append(Component.text(" chunk" + (chunkCount == 1 ? "" : "s")).color(NamedTextColor.DARK_GRAY))
        );
    }

}
