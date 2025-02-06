package io.github.wasabithumb.xclaim;

import com.google.inject.Inject;
import io.github.wasabithumb.xclaim.asset.SpongeAssetManager;
import io.github.wasabithumb.xclaim.command.SpongeCommandBinding;
import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.util.LoggerAdapter;
import io.github.wasabithumb.xclaim.util.WorldDataStore;
import org.bstats.sponge.Metrics;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ProvideServiceEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("xclaim")
public class XClaimPlugin implements XClaimBootstrap {

    private final PluginContainer pluginContainer;
    private final ConfigManager configManager;
    private final Metrics metrics;
    private final XClaim instance;
    private final SpongeAssetManager assets;

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
        this.instance = new XClaim(this);
        this.assets = new SpongeAssetManager(pluginContainer, configManager);
    }

    //

    @Listener
    public void onStart(final StartedEngineEvent<Server> event) {
        this.worldDataStore = new WorldDataStore(event.engine(), this.pluginContainer, this.configManager);
        this.platform = new SpongePlatform(this.pluginContainer, event.engine(), this.metrics, this.worldDataStore);
        this.instance.enable();
    }

    @Listener
    public void onStop(final StoppingEngineEvent<Server> event) {
        this.instance.disable();
        this.platform.destroy();
        this.worldDataStore.close();
    }

    @Listener
    public void onProvideService(final ProvideServiceEvent.EngineScoped<XClaim> event) {
        event.suggest(() -> this.instance);
    }

    @Listener
    public void onRegisterRawCommands(final RegisterCommandEvent<Command.Raw> event){
        event.register(
                this.pluginContainer,
                new SpongeCommandBinding(() -> XClaimPlugin.this.instance),
                "xclaim", "xc"
        );
    }

    //

    @Override
    public @NotNull java.util.logging.Logger logger() {
       return this.logger;
    }

    @Override
    public @NotNull SpongePlatform platform() {
        return this.platform;
    }

    @Override
    public @NotNull SpongeAssetManager assets() {
        return this.assets;
    }

}
