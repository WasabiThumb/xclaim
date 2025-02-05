package io.github.wasabithumb.xclaim.platform;

import io.github.wasabithumb.xclaim.platform.entity.PaperPlatformPlayer;
import io.github.wasabithumb.xclaim.platform.inventory.BukkitPlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PaperPlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PaperPlatformItem;
import io.github.wasabithumb.xclaim.platform.user.BukkitPlatformConsoleUser;
import io.github.wasabithumb.xclaim.platform.user.BukkitPlatformUser;
import io.github.wasabithumb.xclaim.platform.user.PaperPlatformConsoleUser;
import io.github.wasabithumb.xclaim.platform.user.PaperPlatformUser;
import io.github.wasabithumb.xclaim.util.annotations.ParamCasts;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PaperPlatformTypeAdapter extends BukkitPlatformTypeAdapter {

    PaperPlatformTypeAdapter(@NotNull PaperPlatform platform) {
        super(platform);
    }

    protected @NotNull PaperPlatform platform() {
        return (PaperPlatform) this.platform;
    }

    @Override
    public PaperPlatformPlayer player(
            @ParamCasts(Player.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return new PaperPlatformPlayer(this.platform(), this.handleCast(handle, Player.class));
    }

    @Override
    public BukkitPlatformInventory inventory(
            @ParamCasts(Inventory.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return PaperPlatformInventory.of(this.platform(), this.handleCast(handle, Inventory.class));
    }

    @Override
    public PaperPlatformItem item(
            @ParamCasts(ItemStack.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return new PaperPlatformItem(this.platform(), this.handleCast(handle, ItemStack.class));
    }

    @Override
    public BukkitPlatformUser user(
            @ParamCasts(CommandSender.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return PaperPlatformUser.of(this.platform(), this.handleCast(handle, CommandSender.class));
    }

    @Override
    public BukkitPlatformConsoleUser consoleUser(
            @ParamCasts(ConsoleCommandSender.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return new PaperPlatformConsoleUser(this.platform(), this.handleCast(handle, ConsoleCommandSender.class));
    }

}
