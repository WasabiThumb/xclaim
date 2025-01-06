package io.github.wasabithumb.xclaim.config.impl.toml.sub;

import io.github.wasabithumb.xclaim.config.impl.toml.TomlConfig;
import io.github.wasabithumb.xclaim.config.struct.sub.GuiConfig;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlGuiConfig extends TomlConfig implements GuiConfig {

    public TomlGuiConfig(@Nullable Toml table) {
        super(table);
    }

    @Override
    public @NotNull Integer versionRaw() {
        return 2;
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
