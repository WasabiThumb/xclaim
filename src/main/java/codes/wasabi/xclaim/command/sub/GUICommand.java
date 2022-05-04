package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.gui.GUIHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class GUICommand implements Command {

    @Override
    public @NotNull String getName() {
        return "gui";
    }

    @Override
    public @NotNull String getDescription() {
        return "An acessible gui for all XClaim functions";
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
    public void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) {
        Player ply = (Player) sender;
        if (ChunkEditor.getEditing(ply) != null) {
            sender.sendMessage(Component.text("* You must exit the chunk editor before using the GUI.").color(NamedTextColor.RED));
            return;
        }
        new GUIHandler((Player) sender);
    }

}
