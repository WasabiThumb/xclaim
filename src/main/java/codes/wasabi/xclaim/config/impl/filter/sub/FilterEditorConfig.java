package codes.wasabi.xclaim.config.impl.filter.sub;

import codes.wasabi.xclaim.config.impl.filter.FilterConfig;
import codes.wasabi.xclaim.config.struct.sub.EditorConfig;
import org.jetbrains.annotations.NotNull;

public abstract class FilterEditorConfig extends FilterConfig implements EditorConfig {

    public FilterEditorConfig(@NotNull EditorConfig backing) {
        super(backing);
    }

    @Override
    protected @NotNull EditorConfig backing() {
        return (EditorConfig) super.backing();
    }

}
