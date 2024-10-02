package codes.wasabi.xclaim.config.impl.toml.sub;

import codes.wasabi.xclaim.config.impl.toml.TomlConfig;
import codes.wasabi.xclaim.config.struct.sub.GuiConfig;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlGuiConfig extends TomlConfig implements GuiConfig {

    public TomlGuiConfig(@Nullable Toml table) {
        super(table);
    }

    @Override
    public @UnknownNullability Integer versionRaw() {
        return this.getInt("version");
    }

    @Override
    public @UnknownNullability Integer height() {
        return this.versionRaw() == 2 ? this.getInt("v2.height") : null;
    }

    @Override
    public @UnknownNullability String basisRaw() {
        return this.versionRaw() == 2 ? this.getString("v2.basis") : null;
    }

}
