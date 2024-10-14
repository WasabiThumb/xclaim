package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class GUICommand implements Command {

    @Override
    public @NotNull String getName() {
        return XClaim.lang.get("cmd-gui-name");
    }

    @Override
    public @NotNull String getDescription() {
        return XClaim.lang.get("cmd-gui-description");
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
        Audience audience = Platform.getAdventure().sender(sender);
        Player ply = (Player) sender;
        if (ChunkEditor.getEditing(ply) != null) {
            audience.sendMessage(XClaim.lang.getComponent("cmd-gui-err-restricted"));
            return;
        }
        XClaim.gui.openGui(ply);
    }

}
