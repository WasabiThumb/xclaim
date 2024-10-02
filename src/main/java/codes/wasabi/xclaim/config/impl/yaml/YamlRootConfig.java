package codes.wasabi.xclaim.config.impl.yaml;

import codes.wasabi.xclaim.config.impl.yaml.helpers.YamlLimits;
import codes.wasabi.xclaim.config.impl.yaml.sub.*;
import codes.wasabi.xclaim.config.struct.RootConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class YamlRootConfig extends YamlConfig implements RootConfig {

    private final YamlAutoSaveConfig autoSave;
    private final YamlEditorConfig editor;
    private final YamlRulesConfig rules;
    private final YamlWorldsConfig worlds;
    private final YamlIntegrationsConfig integrations;
    private final YamlGuiConfig gui;
    public YamlRootConfig(@NotNull ConfigurationSection section) {
        super(section);
        final YamlLimits limits = YamlLimits.of(this.getSection("limits"));

        this.autoSave     = new YamlAutoSaveConfig(    this.getSection("auto-save")       );
        this.editor       = new YamlEditorConfig(      section                                 );
        this.rules        = new YamlRulesConfig(       section,                          limits);
        this.worlds       = new YamlWorldsConfig(      this.getSection("worlds")          );
        this.integrations = new YamlIntegrationsConfig(section,                          limits);
        this.gui          = new YamlGuiConfig(); // Stub
    }

    @Override
    public @UnknownNullability String language() {
        return this.getString("language");
    }

    @Override
    public @UnknownNullability Long veteranTime() {
        return this.getLong("veteran-time");
    }

    @Override
    public @UnknownNullability Boolean noPaperNag() {
        return this.getBoolean("disable-paper-warning");
    }

    @Override
    public @NotNull YamlAutoSaveConfig autoSave() {
        return this.autoSave;
    }

    @Override
    public @NotNull YamlEditorConfig editor() {
        return this.editor;
    }

    @Override
    public @NotNull YamlRulesConfig rules() {
        return this.rules;
    }

    @Override
    public @NotNull YamlWorldsConfig worlds() {
        return this.worlds;
    }

    @Override
    public @NotNull YamlIntegrationsConfig integrations() {
        return this.integrations;
    }

    @Override
    public @NotNull YamlGuiConfig gui() {
        return this.gui;
    }

    @Override
    public boolean isLegacy() {
        return true;
    }

}
