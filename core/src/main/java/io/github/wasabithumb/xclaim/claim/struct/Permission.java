package io.github.wasabithumb.xclaim.claim.struct;

import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public enum Permission {
    BUILD                 (I18N.PERM_BUILD_NAME                 , I18N.PERM_BUILD_DESCRIPTION                 , TrustLevel.TRUSTED ),
    BREAK                 (I18N.PERM_BREAK_NAME                 , I18N.PERM_BREAK_DESCRIPTION                 , TrustLevel.TRUSTED ),
    ENTER                 (I18N.PERM_ENTER_NAME                 , I18N.PERM_ENTER_DESCRIPTION                 , TrustLevel.ALL     ),
    INTERACT              (I18N.PERM_INTERACT_NAME              , I18N.PERM_INTERACT_DESCRIPTION              , TrustLevel.VETERANS),
    CHEST_OPEN            (I18N.PERM_CHEST_OPEN_NAME            , I18N.PERM_CHEST_OPEN_DESCRIPTION            , TrustLevel.TRUSTED ),
    ENT_PLACE             (I18N.PERM_ENT_PLACE_NAME             , I18N.PERM_ENT_PLACE_DESCRIPTION             , TrustLevel.VETERANS),
    VEHICLE_PLACE         (I18N.PERM_VEHICLE_PLACE_NAME         , I18N.PERM_VEHICLE_PLACE_DESCRIPTION         , TrustLevel.VETERANS),
    FIRE_USE              (I18N.PERM_FIRE_USE_NAME              , I18N.PERM_FIRE_USE_DESCRIPTION              , TrustLevel.TRUSTED ),
    ENTITY_DAMAGE_FRIENDLY(I18N.PERM_ENTITY_DAMAGE_FRIENDLY_NAME, I18N.PERM_ENTITY_DAMAGE_FRIENDLY_DESCRIPTION, TrustLevel.VETERANS),
    ENTITY_DAMAGE_HOSTILE (I18N.PERM_ENTITY_DAMAGE_HOSTILE_NAME , I18N.PERM_ENTITY_DAMAGE_HOSTILE_DESCRIPTION , TrustLevel.VETERANS),
    ENTITY_DAMAGE_VEHICLE (I18N.PERM_ENTITY_DAMAGE_VEHICLE_NAME , I18N.PERM_ENTITY_DAMAGE_VEHICLE_DESCRIPTION , TrustLevel.VETERANS),
    ENTITY_DAMAGE_NL      (I18N.PERM_ENTITY_DAMAGE_NL_NAME      , I18N.PERM_ENTITY_DAMAGE_NL_DESCRIPTION      , TrustLevel.VETERANS),
    ENTITY_DAMAGE_MISC    (I18N.PERM_ENTITY_DAMAGE_MISC_NAME    , I18N.PERM_ENTITY_DAMAGE_MISC_DESCRIPTION    , TrustLevel.ALL     ),
    EXPLODE               (I18N.PERM_EXPLODE_NAME               , I18N.PERM_EXPLODE_DESCRIPTION               , TrustLevel.TRUSTED ),
    ITEM_DROP             (I18N.PERM_ITEM_DROP_NAME             , I18N.PERM_ITEM_DROP_DESCRIPTION             , TrustLevel.ALL     ),
    MANAGE                (I18N.PERM_MANAGE_NAME                , I18N.PERM_MANAGE_DESCRIPTION                , TrustLevel.NONE    ),
    DELETE                (I18N.PERM_DELETE_NAME                , I18N.PERM_DELETE_DESCRIPTION                , TrustLevel.NONE    );

    //

    public static final String ADMIN_OVERRIDE = "xclaim.admin";

    public static @NotNull Permission fromSQLName(@NotNull String sqlName) {
        if (sqlName.equals("DEL")) return Permission.DELETE;
        return Permission.valueOf(sqlName);
    }

    //

    private final Translatable name;
    private final Translatable description;
    private final TrustLevel defaultTrust;
    Permission(
            @NotNull Translatable name,
            @NotNull Translatable description,
            @NotNull TrustLevel defaultTrust
    ) {
        this.name = name;
        this.description = description;
        this.defaultTrust = defaultTrust;
    }

    public @NotNull Translatable getPrintName() {
        return this.name;
    }

    public @NotNull Translatable getDescription() {
        return this.description;
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