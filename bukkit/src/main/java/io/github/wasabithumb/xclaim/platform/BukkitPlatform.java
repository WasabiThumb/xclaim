package io.github.wasabithumb.xclaim.platform;

import io.github.wasabithumb.xclaim.AbstractXClaimPlugin;
import io.github.wasabithumb.xclaim.platform.event.BukkitPlatformEventManager;
import io.github.wasabithumb.xclaim.platform.inventory.*;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.scheduler.BukkitPlatformScheduler;
import io.github.wasabithumb.xclaim.platform.user.BukkitPlatformUserManager;
import io.github.wasabithumb.xclaim.platform.world.BukkitPlatformWorldManager;
import org.bstats.bukkit.Metrics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public abstract class BukkitPlatform implements Platform {

    protected final AbstractXClaimPlugin plugin;
    protected final BukkitPlatformMetrics metrics;
    protected final BukkitPlatformTypeAdapter adapter;
    protected final BukkitPlatformScheduler scheduler;
    protected final BukkitPlatformUserManager users;
    protected final BukkitPlatformWorldManager worlds;
    protected final BukkitPlatformEventManager events;
    public BukkitPlatform(@NotNull AbstractXClaimPlugin plugin) {
        this.plugin = plugin;
        this.metrics   = this.createMetrics();
        this.adapter   = this.createAdapter();
        this.scheduler = this.createScheduler();
        this.users     = this.createUsers();
        this.worlds    = this.createWorlds();
        this.events    = this.createEvents();
    }

    //

    public final @NotNull AbstractXClaimPlugin plugin() {
        return this.plugin;
    }

    protected abstract @NotNull BukkitPlatformTypeAdapter createAdapter();

    protected @NotNull BukkitPlatformMetrics createMetrics() {
        return new BukkitPlatformMetrics(new Metrics(this.plugin, 16129));
    }

    protected abstract @NotNull BukkitPlatformScheduler createScheduler();

    protected abstract @NotNull BukkitPlatformUserManager createUsers();

    protected @NotNull BukkitPlatformWorldManager createWorlds() {
        return new BukkitPlatformWorldManager(this);
    }

    protected abstract @NotNull BukkitPlatformEventManager createEvents();

    //

    @Override
    public @NotNull BukkitPlatformTypeAdapter adapter() {
        return this.adapter;
    }

    @Override
    public @NotNull BukkitPlatformUserManager users() {
        return this.users;
    }

    @Override
    public @NotNull BukkitPlatformWorldManager worlds() {
        return this.worlds;
    }

    @Override
    public @NotNull BukkitPlatformEventManager events() {
        return this.events;
    }

    @Override
    public @NotNull BukkitPlatformScheduler scheduler() {
        return this.scheduler;
    }

    @Override
    public @NotNull BukkitPlatformMetrics metrics() {
        return this.metrics;
    }

    @Override
    public abstract @NotNull BukkitPlatformItem createItem(@NotNull PlatformMaterial material, int amount);

    @Override
    public abstract @NotNull <D> BukkitPlatformCustomInventory<D> createInventory(
            int size,
            @NotNull String name,
            @NotNull D customData
    );

    //

    @ApiStatus.Internal
    public void destroy() {
        this.metrics.shutdown();
    }

}
