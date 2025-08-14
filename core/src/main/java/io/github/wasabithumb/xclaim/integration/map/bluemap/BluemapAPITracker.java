package io.github.wasabithumb.xclaim.integration.map.bluemap;

import de.bluecolored.bluemap.api.BlueMapAPI;
import io.github.wasabithumb.xclaim.util.ForeignLoadable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

final class BluemapAPITracker extends ForeignLoadable<BlueMapAPI> {

    private Consumer<BlueMapAPI> enableListener = null;
    private Consumer<BlueMapAPI> disableListener = null;

    public BluemapAPITracker() {
        super();
    }

    @Override
    protected @NotNull Optional<BlueMapAPI> instantValue() {
        return BlueMapAPI.getInstance();
    }

    @Override
    protected void bindEnableListener() {
        BlueMapAPI.onEnable(this.enableListener = this::setEnabled);
    }

    @Override
    protected void bindDisableListener() {
        BlueMapAPI.onDisable(this.disableListener = this::setDisabled);
    }

    public void cleanup() {
        if (this.enableListener != null)
            BlueMapAPI.unregisterListener(this.enableListener);
        if (this.disableListener != null)
            BlueMapAPI.unregisterListener(this.disableListener);
    }

}
