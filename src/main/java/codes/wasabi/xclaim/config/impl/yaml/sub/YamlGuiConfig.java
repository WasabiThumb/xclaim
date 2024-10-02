package codes.wasabi.xclaim.config.impl.yaml.sub;

import codes.wasabi.xclaim.config.impl.yaml.YamlConfig;
import codes.wasabi.xclaim.config.struct.sub.GuiConfig;
import org.jetbrains.annotations.UnknownNullability;

public final class YamlGuiConfig extends YamlConfig implements GuiConfig {

    public YamlGuiConfig() {
        super(null);
    }

    @Override
    public @UnknownNullability Integer versionRaw() {
        return 0;
    }

    @Override
    public @UnknownNullability Integer height() {
        return null;
    }

    @Override
    public @UnknownNullability String basisRaw() {
        return null;
    }

}
