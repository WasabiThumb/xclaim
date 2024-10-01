package codes.wasabi.xclaim.config.impl.toml.sub;

import codes.wasabi.xclaim.config.impl.toml.TomlConfig;
import codes.wasabi.xclaim.config.struct.sub.AutoSaveConfig;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlAutoSaveConfig extends TomlConfig implements AutoSaveConfig {

    public TomlAutoSaveConfig(@Nullable Toml table) {
        super(table);
    }

    @Override
    public @UnknownNullability Long interval() {
        return this.getLong("interval");
    }

    @Override
    public @UnknownNullability Boolean silent() {
        return this.getBoolean("silent");
    }

}
