package io.github.wasabithumb.xclaim.config.impl.toml;

import io.github.wasabithumb.jtoml.value.table.TomlTable;
import io.github.wasabithumb.xclaim.config.impl.toml.sub.*;
import io.github.wasabithumb.xclaim.config.RootConfig;
import io.github.wasabithumb.xclaim.config.sub.FlagsConfig;
import io.github.wasabithumb.xclaim.config.sub.PermissionsConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class TomlRootConfig extends TomlConfig implements RootConfig {

    private final TomlAutoSaveConfig autoSave;
    private final TomlEditorConfig editor;
    private final TomlRulesConfig rules;
    private final TomlWorldsConfig worlds;
    private final TomlIntegrationsConfig integrations;
    private final TomlGuiConfig gui;
    private final TomlPermissionsConfig permissions;
    private final TomlFlagsConfig flags;

    public TomlRootConfig(@NotNull TomlTable table) {
        super(table);
        this.autoSave     = new TomlAutoSaveConfig(    this.getTable("auto-save"));
        this.editor       = new TomlEditorConfig(      this.getTable("editor"));
        this.rules        = new TomlRulesConfig(       this.getTable("rules"));
        this.worlds       = new TomlWorldsConfig(      this.getTable("worlds"));
        this.integrations = new TomlIntegrationsConfig(this.getTable("integrations"));
        this.gui          = new TomlGuiConfig(         this.getTable("gui"));
        this.permissions  = new TomlPermissionsConfig( this.getTable("permissions"));
        this.flags        = new TomlFlagsConfig(       this.getTable("flags"));
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
    public @NotNull TomlAutoSaveConfig autoSave() {
        return this.autoSave;
    }

    @Override
    public @NotNull TomlEditorConfig editor() {
        return this.editor;
    }

    @Override
    public @NotNull TomlRulesConfig rules() {
        return this.rules;
    }

    @Override
    public @NotNull TomlWorldsConfig worlds() {
        return this.worlds;
    }

    @Override
    public @NotNull TomlGuiConfig gui() {
        return this.gui;
    }

    @Override
    public @NotNull TomlIntegrationsConfig integrations() {
        return this.integrations;
    }

    @Override
    public @NotNull PermissionsConfig permissions() {
        return this.permissions;
    }

    @Override
    public @NotNull FlagsConfig flags() {
        return this.flags;
    }

}
