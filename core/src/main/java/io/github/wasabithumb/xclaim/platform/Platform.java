package io.github.wasabithumb.xclaim.platform;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventManager;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformCustomInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.scheduler.PlatformScheduler;
import io.github.wasabithumb.xclaim.platform.user.PlatformUserManager;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorldManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * The root interface of all interaction with the server impl.
 */
public interface Platform {

    @NotNull PlatformTypeAdapter adapter();

    @NotNull PlatformUserManager users();

    @NotNull PlatformWorldManager worlds();

    @NotNull PlatformEventManager events();

    @NotNull PlatformScheduler scheduler();

    @NotNull PlatformMetrics metrics();

    @NotNull PlatformItem createItem(@NotNull PlatformMaterial material, int amount);

    default @NotNull PlatformItem createItem(@NotNull PlatformMaterial material) {
        return this.createItem(material, 1);
    }

    @NotNull PlatformItem createItem(byte @NotNull [] bytes);

    <D> @NotNull PlatformCustomInventory<D> createInventory(int size, @NotNull String name, @NotNull D customData);

}
