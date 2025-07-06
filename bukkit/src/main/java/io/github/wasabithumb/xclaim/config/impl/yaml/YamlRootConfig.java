package io.github.wasabithumb.xclaim.config.impl.yaml;

import io.github.wasabithumb.xclaim.config.impl.yaml.helpers.YamlLimits;
import io.github.wasabithumb.xclaim.config.impl.yaml.sub.*;
import io.github.wasabithumb.xclaim.config.RootConfig;
import io.github.wasabithumb.xclaim.config.sub.FlagsConfig;
import io.github.wasabithumb.xclaim.config.sub.PermissionsConfig;
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
    private final YamlPermissionsConfig permissions;
    private final YamlFlagsConfig flags;

    public YamlRootConfig(@NotNull ConfigurationSection section) {
        super(section);
        final YamlLimits limits = YamlLimits.of(this.getSection("limits"));

        this.autoSave     = new YamlAutoSaveConfig(    this.getSection("auto-save")       );
        this.editor       = new YamlEditorConfig(      section                                 );
        this.rules        = new YamlRulesConfig(       section,                          limits);
        this.worlds       = new YamlWorldsConfig(      this.getSection("worlds")          );
        this.integrations = new YamlIntegrationsConfig(section,                          limits);
        this.gui          = new YamlGuiConfig(); // Stub
        this.permissions  = new YamlPermissionsConfig(); // Stub
        this.flags        = new YamlFlagsConfig(); // Stub
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
    public @NotNull PermissionsConfig permissions() {
        return this.permissions;
    }

    @Override
    public @NotNull FlagsConfig flags() {
        return this.flags;
    }

    @Override
    public boolean isLegacy() {
        return true;
    }

}
