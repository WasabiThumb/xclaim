package io.github.wasabithumb.xclaim.platform;

import io.github.wasabithumb.xclaim.AbstractXClaimPlugin;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventManager;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.scheduler.PlatformScheduler;
import io.github.wasabithumb.xclaim.platform.user.PlatformUserManager;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorldManager;
import org.bstats.bukkit.Metrics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

@ApiStatus.Internal
public abstract class BukkitPlatform implements Platform {

    protected final AbstractXClaimPlugin plugin;
    protected final BukkitPlatformMetrics metrics;
    protected final BukkitPlatformTypeAdapter adapter;
    protected final PlatformScheduler scheduler;
    public BukkitPlatform(@NotNull AbstractXClaimPlugin plugin) {
        this.plugin = plugin;
        this.metrics = this.createMetrics();
        this.adapter = this.createAdapter();
        this.scheduler = this.createScheduler();
    }

    //

    public final @NotNull AbstractXClaimPlugin plugin() {
        return this.plugin;
    }

    protected abstract @NotNull BukkitPlatformTypeAdapter createAdapter();

    protected @NotNull BukkitPlatformMetrics createMetrics() {
        return new BukkitPlatformMetrics(new Metrics(this.plugin, 16129));
    }

    protected abstract @NotNull PlatformScheduler createScheduler();

    //

    @Override
    public @NotNull BukkitPlatformTypeAdapter adapter() {
        return this.adapter;
    }

    @Override
    public @NotNull PlatformUserManager users() {
        return null;
    }

    @Override
    public @NotNull PlatformWorldManager worlds() {
        return null;
    }

    @Override
    public @NotNull PlatformEventManager events() {
        return null;
    }

    @Override
    public @NotNull PlatformScheduler scheduler() {
        return this.scheduler;
    }

    @Override
    public @NotNull BukkitPlatformMetrics metrics() {
        return this.metrics;
    }

    @Override
    public @NotNull PlatformItem createItem(@NotNull PlatformMaterial material, int amount) {
        return null;
    }

    @Override
    public @NotNull <D> PlatformInventory<D> createInventory(int size, @NotNull String name, @UnknownNullability D customData) {
        return null;
    }

    //

    @ApiStatus.Internal
    public void destroy() {
        this.metrics.shutdown();
    }

}
