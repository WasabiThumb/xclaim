package io.github.wasabithumb.xclaim;

import com.google.inject.Inject;
import io.github.wasabithumb.xclaim.asset.AssetManager;
import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.util.LoggerAdapter;
import io.github.wasabithumb.xclaim.util.WorldDataStore;
import org.bstats.sponge.Metrics;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.nio.file.Path;

@Plugin("xclaim")
public class XClaimPlugin implements XClaimBootstrap {

    private final PluginContainer pluginContainer;
    private final ConfigManager configManager;
    private final Metrics metrics;

    @SuppressWarnings("SpongeLogging")
    private final java.util.logging.Logger logger;

    private WorldDataStore worldDataStore;
    private SpongePlatform platform;

    //

    @Inject
    XClaimPlugin(PluginContainer pluginContainer, ConfigManager configManager, Metrics.Factory metricsFactory) {
        this.pluginContainer = pluginContainer;
        this.configManager = configManager;
        this.metrics = metricsFactory.make(24566);
        this.logger = LoggerAdapter.wrap(pluginContainer.logger());
    }

    //

    @Listener
    public void onStart(final StartedEngineEvent<Server> event) {
        this.worldDataStore = new WorldDataStore(event.engine(), this.pluginContainer, this.configManager);
        this.platform = new SpongePlatform(this.pluginContainer, event.engine(), this.metrics, this.worldDataStore);
    }

    @Listener
    public void onStop(final StoppingEngineEvent<Server> event) {
        this.platform.destroy();
        this.worldDataStore.close();
    }

    //

    @Override
    public @NotNull java.util.logging.Logger logger() {
       return this.logger;
    }

    @Override
    public @NotNull Platform platform() {
        return this.platform;
    }

    @Override
    public @NotNull AssetManager assets() {
        Path dir = this.configManager.pluginConfig(this.pluginContainer).directory();
        // TODO
        return null;
    }

}
