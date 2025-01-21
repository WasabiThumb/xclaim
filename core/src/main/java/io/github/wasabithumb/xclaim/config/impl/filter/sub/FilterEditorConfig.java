package io.github.wasabithumb.xclaim.config.impl.filter.sub;

import io.github.wasabithumb.xclaim.config.impl.filter.FilterConfig;
import io.github.wasabithumb.xclaim.config.sub.EditorConfig;
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
