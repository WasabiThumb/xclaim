package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.command.argument.type.StringType;
import codes.wasabi.xclaim.gui.ChunkEditor;
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
        return "chunks";
    }

    @Override
    public @NotNull String getDescription() {
        return "Opens the claim chunk editor. If a claim name is specified, then it will edit that claim. Otherwise, it uses the current residing claim.";
    }

    private final Argument[] args = new Argument[] {
            new Argument(new StringType(), "Claim name", "Name of the claim to edit. If absent, the current residing claim is assumed.")
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
        Player ply = (Player) sender;
        if (ChunkEditor.getEditing(ply) != null) {
            sender.sendMessage(Component.text("* You are already in the chunk editor! Exit it first!").color(NamedTextColor.RED));
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
                sender.sendMessage(Component.text("* You aren't currently in a claim!").color(NamedTextColor.RED));
                return;
            }
        }
        if (!claim.hasPermission(ply, Permission.MANAGE)) {
            ply.sendMessage(Component.text("* You do not have permission to manage this claim!").color(NamedTextColor.RED));
            return;
        }
        ply.sendActionBar(
                Component.empty()
                        .append(Component.text("Editing ").color(NamedTextColor.GREEN))
                        .append(Component.text(claim.getName()).color(NamedTextColor.GOLD))
        );
        ply.playSound(ply.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
        ChunkEditor.startEditing(ply, claim);
    }

}
