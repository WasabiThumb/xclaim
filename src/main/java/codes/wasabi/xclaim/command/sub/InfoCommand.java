package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class InfoCommand implements Command {

    @Override
    public @NotNull String getName() {
        return "info";
    }

    @Override
    public @NotNull String getDescription() {
        return "Prints out basic info about XClaim";
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
        return false;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) throws Exception {
        PluginDescriptionFile description = XClaim.instance.getDescription();
        String apiVersion = description.getAPIVersion();
        if (apiVersion == null) apiVersion = Bukkit.getMinecraftVersion() + "(?)";
        int claimCount = 0;
        int chunkCount = 0;
        for (Claim c : Claim.getAll()) {
            claimCount++;
            chunkCount += c.getChunks().size();
        }
        sender.sendMessage(Component.text()
                .append(Component.text("XClaim").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("Made by ").color(NamedTextColor.GOLD))
                .append(Component.text("WasabiThumbs").color(NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("Boy, I sure do love Wasabi and think he makes some pretty great plugins!")))
                .append(Component.newline())
                .append(Component.text("Version " + description.getVersion()).color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("API Version " + apiVersion).color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text(claimCount).color(NamedTextColor.WHITE))
                .append(Component.text(" total claim" + (claimCount == 1 ? "" : "s") + " covering ").color(NamedTextColor.GOLD))
                .append(Component.text(chunkCount).color(NamedTextColor.WHITE))
                .append(Component.text(" chunk" + (chunkCount == 1 ? "" : "s")).color(NamedTextColor.GOLD))
        );
    }

}
