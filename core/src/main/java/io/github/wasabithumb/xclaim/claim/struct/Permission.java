package io.github.wasabithumb.xclaim.claim.struct;

import io.github.wasabithumb.xclaim.i18n.Lang;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public enum Permission {
    BUILD                 ("build",                  TrustLevel.TRUSTED ),
    BREAK                 ("break",                  TrustLevel.TRUSTED ),
    ENTER                 ("enter",                  TrustLevel.ALL     ),
    INTERACT              ("interact",               TrustLevel.VETERANS),
    CHEST_OPEN            ("chest-open",             TrustLevel.TRUSTED ),
    ENT_PLACE             ("ent-place",              TrustLevel.VETERANS),
    VEHICLE_PLACE         ("vehicle-place",          TrustLevel.VETERANS),
    FIRE_USE              ("fire-use",               TrustLevel.TRUSTED ),
    ENTITY_DAMAGE_FRIENDLY("entity-damage-friendly", TrustLevel.VETERANS),
    ENTITY_DAMAGE_HOSTILE ("entity-damage-hostile",  TrustLevel.VETERANS),
    ENTITY_DAMAGE_VEHICLE ("entity-damage-vehicle",  TrustLevel.VETERANS),
    ENTITY_DAMAGE_NL      ("entity-damage-nl",       TrustLevel.VETERANS),
    ENTITY_DAMAGE_MISC    ("entity-damage-misc",     TrustLevel.ALL     ),
    EXPLODE               ("explode",                TrustLevel.TRUSTED ),
    ITEM_DROP             ("item-drop",              TrustLevel.ALL     ),
    MANAGE                ("manage",                 TrustLevel.NONE    ),
    DELETE                ("delete",                 TrustLevel.NONE    );

    //

    public static final String ADMIN_OVERRIDE = "xclaim.admin";

    public static @NotNull Permission fromSQLName(@NotNull String sqlName) {
        if (sqlName.equals("DEL")) return Permission.DELETE;
        return Permission.valueOf(sqlName);
    }

    //

    private final String key;
    private final TrustLevel defaultTrust;
    Permission(@NotNull String key, @NotNull TrustLevel defaultTrust) {
        this.key = key;
        this.defaultTrust = defaultTrust;
    }

    public @NotNull String getPrintName(@NotNull Lang lang) {
        return lang.get("perm-" + this.key + "-name");
    }

    public @NotNull String getDescription(@NotNull Lang lang) {
        return lang.get("perm-" + this.key + "-description");
    }

    public @NotNull TrustLevel getDefaultTrust() {
        return this.defaultTrust;
    }

    @ApiStatus.Internal
    public @NotNull String sqlName() {
        if (this == DELETE) return "DEL";
        return this.name();
    }

}