package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.data.sound.PlatformSound;
import io.github.wasabithumb.xclaim.platform.misc.PlatformBossBar;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlatformPlayer extends PlatformEntity, PlatformUser {

    @NotNull String name();

    @NotNull PlatformInventory getInventory();

    int getHeldItemSlot();

    @Nullable PlatformItem getItemInUse();

    void sendActionBar(@NotNull String text);

    @NotNull PlatformBossBar createBossBar(
            @NotNull String text,
            float progress,
            @NotNull PlatformBossBar.Color color,
            @NotNull PlatformBossBar.Overlay overlay
    );

    void playSound(@NotNull PlatformSound sound);

    boolean isGliding();

    boolean canBoostElytra();

    void boostElytra(@Nullable PlatformItem item);

    void openBook(@Nullable PlatformItem item);

    void openInventory(@NotNull PlatformInventory inventory);

    void closeInventory();

    long getFirstPlayed();

    void sendRedstoneParticle(int rgb, double x, double y, double z);

    // START PlatformEntity

    @Override
    default @NotNull PlatformEntityType type() {
        return NamedPlatformEntityType.PLAYER;
    }

    @Override
    default @Nullable PlatformPlayer sourcePlayer() {
        return this;
    }

    // END PlatformEntity

    // START PlatformUser

    @Override
    default boolean isOffline() {
        return false;
    }

    // END PlatformUser

}
