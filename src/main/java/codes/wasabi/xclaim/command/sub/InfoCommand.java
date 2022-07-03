package codes.wasabi.xclaim.command.sub;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.command.Command;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.platform.Platform;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class InfoCommand implements Command {

    @Override
    public @NotNull String getName() {
        return XClaim.lang.get("cmd-info-name");
    }

    @Override
    public @NotNull String getDescription() {
        return XClaim.lang.get("cmd-info-description");
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
        Audience audience = Platform.getAdventure().sender(sender);
        PluginDescriptionFile description = XClaim.instance.getDescription();
        String apiVersion = description.getAPIVersion();
        if (apiVersion == null) apiVersion = "1." + PaperLib.getMinecraftVersion() + "(?)";
        int claimCount = 0;
        int chunkCount = 0;
        for (Claim c : Claim.getAll()) {
            claimCount++;
            chunkCount += c.getChunks().size();
        }
        String pluralType = (claimCount == 1 ? (
                chunkCount == 1 ? "not" : "chunk"
        ) : "both");
        Component claimText = XClaim.lang.getComponent("cmd-info-claims-" + pluralType + "-plural", claimCount, chunkCount);
        audience.sendMessage(Component.text()
                .append(Component.text("XClaim").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD))
                .append(Component.newline())
                .append(XClaim.lang.getComponent(
                        "cmd-info-author",
                        Component.text("WasabiThumbs").color(NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("Boy, I sure do love Wasabi and think he makes some pretty great plugins!"))
                ))
                .append(Component.newline())
                .append(XClaim.lang.getComponent("cmd-info-version", description.getVersion()))
                .append(Component.newline())
                .append(XClaim.lang.getComponent("cmd-info-apiVersion", apiVersion))
                .append(Component.newline())
                .append(claimText)
        );
    }

}
