package codes.wasabi.xclaim.config.impl.defaulting.sub;

import codes.wasabi.xclaim.config.impl.filter.sub.FilterGuiConfig;
import codes.wasabi.xclaim.config.struct.sub.GuiConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class DefaultingGuiConfig extends FilterGuiConfig {

    public DefaultingGuiConfig(@NotNull GuiConfig backing) {
        super(backing);
    }

    @Override
    public @UnknownNullability Integer versionRaw() {
        return this.nullFallback(this.backing().versionRaw(), 1);
    }

    @Override
    public @UnknownNullability Version version() {
        return this.nullFallback(this.backing().version(), Version.V1);
    }

    @Override
    public @UnknownNullability Integer height() {
        return this.nullFallback(this.backing().height(), 3);
    }

    @Override
    public @UnknownNullability String basisRaw() {
        return this.nullFallback(this.backing().basisRaw(), "LEFT");
    }

    @Override
    public @UnknownNullability Basis basis() {
        return this.nullFallback(this.backing().basis(), Basis.LEFT);
    }

}
