package io.github.wasabithumb.xclaim.config.impl.toml.sub;

import io.github.wasabithumb.jtoml.value.table.TomlTable;
import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.sub.GuiConfig;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlGuiConfig extends TomlConfig implements GuiConfig {

    public TomlGuiConfig(@Nullable TomlTable table) {
        super(table);
    }

    @Override
    public @UnknownNullability Integer height() {
        Integer ret = this.getInt("height");
        if (ret == null) ret = this.getInt("v2.height"); // Old key
        return ret;
    }

    @Override
    public @UnknownNullability String basisRaw() {
        String ret = this.getString("basis");
        if (ret == null) ret = this.getString("v2.basis"); // Old key
        return ret;
    }

    @Override
    public @UnknownNullability String dialogRaw() {
        String ret = this.getString("dialog");
        if (ret == null) ret = this.getString("v2.dialog"); // Old key
        return ret;
    }

}
