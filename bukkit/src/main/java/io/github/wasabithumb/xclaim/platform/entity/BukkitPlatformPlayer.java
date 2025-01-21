package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.data.sound.PlatformSound;
import io.github.wasabithumb.xclaim.platform.inventory.BukkitPlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.user.BukkitPlatformUser;
import org.bukkit.Color;
import org.bukkit.Particle;
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

    public @NotNull String name() {
        return this.handle().getName();
    }

    @Override
    public @NotNull BukkitPlatformInventory getInventory() {
        return this.platform.adapter().inventory(this.handle().getInventory());
    }

    @Override
    public int getHeldItemSlot() {
        return this.handle().getInventory().getHeldItemSlot();
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
    public void openInventory(@NotNull PlatformInventory inventory) {
        this.handle().openInventory(this.platform.adapter().inventory(inventory));
    }

    @Override
    public void closeInventory() {
        this.handle().closeInventory();
    }

    @Override
    public long getFirstPlayed() {
        return this.handle().getFirstPlayed();
    }

    private static final Particle DUST_PARTICLE;
    static {
        Particle dust;
        try {
            //noinspection JavaReflectionMemberAccess
            dust = (Particle) Particle.class.getField("DUST").get(null);
        } catch (ReflectiveOperationException e) {
            dust = Particle.REDSTONE;
        }
        DUST_PARTICLE = dust;
    }

    @Override
    public void sendRedstoneParticle(int rgb, double x, double y, double z) {
        Particle.DustOptions opts = new Particle.DustOptions(Color.fromRGB(rgb), 1);
        this.handle().spawnParticle(
                DUST_PARTICLE,
                x, y, z,
                1,
                0.02d, 0.02d, 0.02d,
                opts
        );
    }

}
