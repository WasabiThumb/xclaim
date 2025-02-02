package io.github.wasabithumb.xclaim.platform;

import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventManager;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformCustomInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.scheduler.PlatformScheduler;
import io.github.wasabithumb.xclaim.platform.user.SpongePlatformUserManager;
import io.github.wasabithumb.xclaim.platform.world.SpongePlatformWorldManager;
import io.github.wasabithumb.xclaim.util.WorldDataStore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.sponge.Metrics;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Server;
import org.spongepowered.plugin.PluginContainer;

public class SpongePlatform implements Platform {

    private final PluginContainer plugin;
    private final Server server;
    private final MiniMessage mm;
    private final WorldDataStore worldData;
    private final SpongePlatformMetrics metrics;
    private final SpongePlatformUserManager users;
    private final SpongePlatformWorldManager worlds;

    @ApiStatus.Internal
    public SpongePlatform(
            @NotNull PluginContainer plugin,
            @NotNull Server server,
            @NotNull Metrics metrics,
            @NotNull WorldDataStore worldData
    ) {
        this.plugin = plugin;
        this.server = server;
        this.mm = MiniMessage.miniMessage();
        this.worldData = worldData;
        this.metrics = new SpongePlatformMetrics(metrics);
        this.users = new SpongePlatformUserManager(this);
        this.worlds = new SpongePlatformWorldManager(this);
    }

    //

    @ApiStatus.Internal
    public final @NotNull PluginContainer plugin() {
        return this.plugin;
    }

    @ApiStatus.Internal
    public final @NotNull Logger logger() {
        return this.plugin.logger();
    }

    @ApiStatus.Internal
    public final @NotNull Server server() {
        return this.server;
    }

    @ApiStatus.Internal
    public final @NotNull MiniMessage mm() {
        return this.mm;
    }

    @ApiStatus.Internal
    public final @NotNull WorldDataStore worldData() {
        return this.worldData;
    }

    //

    // TODO: Lots

    @Override
    public @NotNull PlatformTypeAdapter adapter() {
        return null;
    }

    @Override
    public @NotNull SpongePlatformUserManager users() {
        return this.users;
    }

    @Override
    public @NotNull SpongePlatformWorldManager worlds() {
        return this.worlds;
    }

    @Override
    public @NotNull PlatformEventManager events() {
        return null;
    }

    @Override
    public @NotNull PlatformScheduler scheduler() {
        return null;
    }

    @Override
    public @NotNull SpongePlatformMetrics metrics() {
        return this.metrics;
    }

    @Override
    public @NotNull PlatformItem createItem(@NotNull PlatformMaterial material, int amount) {
        return null;
    }

    @Override
    public @NotNull PlatformItem createItem(byte @NotNull [] bytes) {
        return null;
    }

    @Override
    public @NotNull <D> PlatformCustomInventory<D> createInventory(int size, @NotNull String name, @NotNull D customData) {
        return null;
    }

    //

    @ApiStatus.Internal
    public void destroy() {
        this.metrics.shutdown();
    }

}
