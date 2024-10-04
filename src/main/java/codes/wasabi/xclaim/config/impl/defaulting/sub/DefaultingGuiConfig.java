package codes.wasabi.xclaim.config.impl.defaulting.sub;

import codes.wasabi.xclaim.config.impl.filter.sub.FilterGuiConfig;
import codes.wasabi.xclaim.config.struct.sub.GuiConfig;
import codes.wasabi.xclaim.gui2.dialog.GuiDialogType;
import codes.wasabi.xclaim.gui2.layout.GuiBasis;
import org.jetbrains.annotations.NotNull;

public final class DefaultingGuiConfig extends FilterGuiConfig {

    public DefaultingGuiConfig(@NotNull GuiConfig backing) {
        super(backing);
    }

    @Override
    public @NotNull Integer versionRaw() {
        return this.nullFallback(this.backing().versionRaw(), 1);
    }

    @Override
    public @NotNull Version version() {
        return this.nullFallback(this.backing().version(), Version.V1);
    }

    @Override
    public @NotNull Integer height() {
        return this.nullFallback(this.backing().height(), 3);
    }

    @Override
    public @NotNull String basisRaw() {
        return this.nullFallback(this.backing().basisRaw(), "LEFT");
    }

    @Override
    public @NotNull GuiBasis basis() {
        return this.nullFallback(this.backing().basis(), GuiBasis.LEFT);
    }

    @Override
    public @NotNull String dialogRaw() {
        return this.nullFallback(this.backing().dialogRaw(), "ACTION_BAR");
    }

    @Override
    public @NotNull GuiDialogType dialog() {
        return this.nullFallback(this.backing().dialog(), GuiDialogType.ACTION_BAR);
    }

}
