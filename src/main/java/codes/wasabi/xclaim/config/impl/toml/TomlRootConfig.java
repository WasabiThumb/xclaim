package codes.wasabi.xclaim.config.impl.toml;

import codes.wasabi.xclaim.config.impl.toml.sub.*;
import codes.wasabi.xclaim.config.struct.RootConfig;
import com.moandjiezana.toml.Toml;
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
    public TomlRootConfig(@NotNull Toml table) {
        super(table);
        this.autoSave     = new TomlAutoSaveConfig(    this.getTable("auto-save"));
        this.editor       = new TomlEditorConfig(      this.getTable("editor"));
        this.rules        = new TomlRulesConfig(       this.getTable("rules"));
        this.worlds       = new TomlWorldsConfig(      this.getTable("worlds"));
        this.integrations = new TomlIntegrationsConfig(this.getTable("integrations"));
        this.gui          = new TomlGuiConfig(         this.getTable("gui"));
        this.permissions  = new TomlPermissionsConfig( this.getTable("permissions"));
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
        return this.getBoolean("no-paper-nag");
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
    public @NotNull TomlPermissionsConfig permissions() {
        return this.permissions;
    }

}
