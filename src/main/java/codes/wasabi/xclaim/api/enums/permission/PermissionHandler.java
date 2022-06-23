package codes.wasabi.xclaim.api.enums.permission;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class PermissionHandler implements Listener {

    private final Claim claim;
    public PermissionHandler(@NotNull Claim claim) {
        this.claim = claim;
    }

    protected final @NotNull Claim getClaim() {
        return claim;
    }

    protected void onRegister() {

    }

    protected void onUnregister() {

    }

    protected void stdError(@NotNull Player ply) {
        Platform.getAdventure().player(ply).sendMessage(
                Component.empty()
                        .append(Component.text("Hey!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                        .append(Component.text(" You can't do that here.").color(NamedTextColor.GRAY))
        );
    }

    private boolean registered = false;

    public final boolean isRegistered() {
        return registered;
    }

    public final boolean register() {
        if (registered) return false;
        Bukkit.getPluginManager().registerEvents(this, XClaim.instance);
        registered = true;
        onRegister();
        return true;
    }

    public final boolean unregister() {
        if (!registered) return false;
        HandlerList.unregisterAll(this);
        registered = false;
        onUnregister();
        return true;
    }

}
