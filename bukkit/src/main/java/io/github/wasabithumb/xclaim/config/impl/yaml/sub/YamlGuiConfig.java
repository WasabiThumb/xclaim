package io.github.wasabithumb.xclaim.config.impl.yaml.sub;

import io.github.wasabithumb.xclaim.config.impl.yaml.YamlConfig;
import io.github.wasabithumb.xclaim.config.struct.sub.GuiConfig;
import org.jetbrains.annotations.UnknownNullability;

public final class YamlGuiConfig extends YamlConfig implements GuiConfig {

    public YamlGuiConfig() {
        super(null);
    }

    @Override
    public @UnknownNullability Integer height() {
        return null;
    }

    @Override
    public @UnknownNullability String basisRaw() {
        return null;
    }

    @Override
    public @UnknownNullability String dialogRaw() {
        return null;
    }

}
