package codes.wasabi.xclaim.config.impl.defaulting.sub;

import codes.wasabi.xclaim.config.impl.filter.sub.FilterEditorConfig;
import codes.wasabi.xclaim.config.struct.sub.EditorConfig;
import org.jetbrains.annotations.NotNull;

public final class DefaultingEditorConfig extends FilterEditorConfig {

    public DefaultingEditorConfig(@NotNull EditorConfig backing) {
        super(backing);
    }

    @Override
    public @NotNull Boolean startOnCreate() {
        return this.nullFallback(this.backing().startOnCreate(), true);
    }

    @Override
    public @NotNull Boolean stopOnShutdown() {
        return this.nullFallback(this.backing().stopOnShutdown(), false);
    }

    @Override
    public @NotNull Boolean stopOnLeave() {
        return this.nullFallback(this.backing().stopOnLeave(), true);
    }

}
