package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.data.sound.PlatformSound;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlatformPlayer extends PlatformEntity, PlatformUser {

    @NotNull PlatformInventory getInventory();

    @Nullable PlatformItem getItemInUse();

    void sendActionBar(@NotNull String text);

    void playSound(@NotNull PlatformSound sound);

    boolean isGliding();

    void boostElytra(@Nullable PlatformItem item);

    void openBook(@Nullable PlatformItem item);

    long getFirstPlayed();

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
