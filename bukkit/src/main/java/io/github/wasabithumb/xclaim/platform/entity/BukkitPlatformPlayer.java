package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.data.sound.PlatformSound;
import io.github.wasabithumb.xclaim.platform.inventory.BukkitPlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.BukkitPlatformItem;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.user.BukkitPlatformUser;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BukkitPlatformPlayer extends BukkitPlatformEntity implements BukkitPlatformUser, PlatformPlayer {

    BukkitPlatformPlayer(@NotNull BukkitPlatform platform, @NotNull Player handle) {
        super(platform, handle);
    }

    @Override
    public final boolean isOffline() {
        return false;
    }

    @Override
    public @NotNull Player handle() {
        return (Player) this.handle;
    }

    @Override
    public @NotNull BukkitPlatformInventory getInventory() {
        return this.platform.adapter().inventory(this.handle().getInventory());
    }

    @Override
    public @Nullable BukkitPlatformItem getItemInUse() {
        return this.platform.adapter().item(this.handle().getItemInUse());
    }

    @Override
    public void playSound(@NotNull PlatformSound sound) {
        final Player ply = this.handle();
        ply.playSound(
                ply.getLocation(),
                this.platform.adapter().sound(sound),
                SoundCategory.MASTER,
                1f,
                1f
        );
    }

    @Override
    public boolean isGliding() {
        return this.handle().isGliding();
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public @NotNull BukkitPlatformPlayer asPlayer() {
        return this;
    }

    @Override
    public void openBook(@Nullable PlatformItem item) {
        if (item == null) return;
        this.handle().openBook(this.platform.adapter().item(item));
    }

    @Override
    public long getFirstPlayed() {
        return this.handle().getFirstPlayed();
    }

}
