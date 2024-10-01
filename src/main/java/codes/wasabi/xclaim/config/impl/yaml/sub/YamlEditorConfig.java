package codes.wasabi.xclaim.config.impl.yaml.sub;

import codes.wasabi.xclaim.config.impl.yaml.YamlConfig;
import codes.wasabi.xclaim.config.struct.sub.EditorConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class YamlEditorConfig extends YamlConfig implements EditorConfig {

    public YamlEditorConfig(@Nullable ConfigurationSection section) {
        super(section);
    }

    @Override
    public @UnknownNullability Boolean startOnCreate() {
        return this.getBoolean("enter-chunk-editor-on-create");
    }

    @Override
    public @UnknownNullability Boolean stopOnShutdown() {
        return this.getBoolean("stop-editing-on-shutdown");
    }

    @Override
    public @UnknownNullability Boolean stopOnLeave() {
        return this.getBoolean("stop-editing-on-leave");
    }

}
