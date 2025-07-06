package io.github.wasabithumb.xclaim.config.impl.defaulting;

import io.github.wasabithumb.xclaim.config.impl.defaulting.sub.*;
import io.github.wasabithumb.xclaim.config.impl.filter.FilterRootConfig;
import io.github.wasabithumb.xclaim.config.RootConfig;
import io.github.wasabithumb.xclaim.config.sub.FlagsConfig;
import io.github.wasabithumb.xclaim.config.sub.PermissionsConfig;
import org.jetbrains.annotations.NotNull;

public final class DefaultingRootConfig extends FilterRootConfig {

    private final DefaultingAutoSaveConfig autoSave;
    private final DefaultingEditorConfig editor;
    private final DefaultingRulesConfig rules;
    private final DefaultingWorldsConfig worlds;
    private final DefaultingIntegrationsConfig integrations;
    private final DefaultingGuiConfig gui;
    private final DefaultingPermissionsConfig permissions;
    private final DefaultingFlagsConfig flags;

    public DefaultingRootConfig(@NotNull RootConfig backing) {
        super(backing);
        this.autoSave     = new DefaultingAutoSaveConfig(     backing.autoSave()     );
        this.editor       = new DefaultingEditorConfig(       backing.editor()       );
        this.rules        = new DefaultingRulesConfig(        backing.rules()        );
        this.worlds       = new DefaultingWorldsConfig(       backing.worlds()       );
        this.integrations = new DefaultingIntegrationsConfig( backing.integrations() );
        this.gui          = new DefaultingGuiConfig(          backing.gui()          );
        this.permissions  = new DefaultingPermissionsConfig(  backing.permissions()  );
        this.flags        = new DefaultingFlagsConfig(        backing.flags()        );
    }

    @Override
    public @NotNull String language() {
        return this.nullFallback(this.backing().language(), "en-US");
    }

    @Override
    public @NotNull Long veteranTime() {
        return this.nullFallback(this.backing().veteranTime(), 604800L);
    }

    @Override
    public @NotNull DefaultingAutoSaveConfig autoSave() {
        return this.autoSave;
    }

    @Override
    public @NotNull DefaultingEditorConfig editor() {
        return this.editor;
    }

    @Override
    public @NotNull DefaultingRulesConfig rules() {
        return this.rules;
    }

    @Override
    public @NotNull DefaultingWorldsConfig worlds() {
        return this.worlds;
    }

    @Override
    public @NotNull DefaultingIntegrationsConfig integrations() {
        return this.integrations;
    }

    @Override
    public @NotNull DefaultingGuiConfig gui() {
        return this.gui;
    }

    @Override
    public @NotNull DefaultingPermissionsConfig permissions() {
        return this.permissions;
    }

    @Override
    public @NotNull DefaultingFlagsConfig flags() {
        return this.flags;
    }

}
