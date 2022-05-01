package codes.wasabi.xclaim.command;

import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.gui.GUIHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClaimGUICommand implements Command {

    @Override
    public @NotNull String getName() {
        return "claimgui";
    }

    @Override
    public @NotNull String getDescription() {
        return "Manage chunk claims in an accessible GUI";
    }

    @Override
    public @NotNull Argument @NotNull [] getArguments() {
        return new Argument[0];
    }

    @Override
    public int getNumRequiredArguments() {
        return 0;
    }

    @Override
    public boolean requiresPlayerExecutor() {
        return true;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) throws Exception {
        Player ply = (Player) sender;
        if (ChunkEditor.getEditing(ply) != null) {
            sender.sendMessage(Component.text("* You must exit the chunk editor before using the GUI.").color(NamedTextColor.RED));
            return;
        }
        new GUIHandler((Player) sender);
    }

}
