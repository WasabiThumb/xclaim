package io.github.wasabithumb.xclaim.platform;

import io.github.wasabithumb.xclaim.platform.entity.SpigotPlatformPlayer;
import io.github.wasabithumb.xclaim.platform.inventory.BukkitPlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.SpigotPlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.SpigotPlatformItem;
import io.github.wasabithumb.xclaim.platform.user.BukkitPlatformConsoleUser;
import io.github.wasabithumb.xclaim.platform.user.BukkitPlatformUser;
import io.github.wasabithumb.xclaim.platform.user.SpigotPlatformConsoleUser;
import io.github.wasabithumb.xclaim.platform.user.SpigotPlatformUser;
import io.github.wasabithumb.xclaim.util.annotations.ParamCasts;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SpigotPlatformTypeAdapter extends BukkitPlatformTypeAdapter {

    SpigotPlatformTypeAdapter(@NotNull SpigotPlatform platform) {
        super(platform);
    }

    protected @NotNull SpigotPlatform platform() {
        return (SpigotPlatform) this.platform;
    }

    @Override
    public SpigotPlatformPlayer player(
            @ParamCasts(Player.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return new SpigotPlatformPlayer(this.platform(), this.handleCast(handle, Player.class));
    }

    @Override
    public BukkitPlatformInventory inventory(
            @ParamCasts(Inventory.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return SpigotPlatformInventory.of(this.platform(), this.handleCast(handle, Inventory.class));
    }

    @Override
    public SpigotPlatformItem item(
            @ParamCasts(ItemStack.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return new SpigotPlatformItem(this.platform(), this.handleCast(handle, ItemStack.class));
    }

    @Override
    public BukkitPlatformUser user(
            @ParamCasts(CommandSender.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return SpigotPlatformUser.of(this.platform(), this.handleCast(handle, CommandSender.class));
    }

    @Override
    public BukkitPlatformConsoleUser consoleUser(
            @ParamCasts(ConsoleCommandSender.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return new SpigotPlatformConsoleUser(this.platform(), this.handleCast(handle, ConsoleCommandSender.class));
    }

}
