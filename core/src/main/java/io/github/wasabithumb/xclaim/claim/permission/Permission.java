package io.github.wasabithumb.xclaim.claim.permission;

import io.github.wasabithumb.xclaim.i18n.Translatable;
import static io.github.wasabithumb.xclaim.claim.permission.PermissionImpl.create;

import org.jetbrains.annotations.*;

import java.util.List;

public sealed interface Permission permits PermissionImpl {
    /** Place/modify existing blocks */
    Permission BUILD           = create("build");

    /** Break blocks */
    Permission BREAK           = create("break");

    /** Move into the claim */
    Permission ENTER           = create("enter");

    /** Use chests, barrels, shulker boxes */
    Permission USE_STORAGE     = create("use-storage", "interact");

    /** Activate powerable blocks */
    Permission USE_REDSTONE    = create("use-redstone", "interact");

    /** Rotate item frames, dress armor stands */
    Permission USE_FIXTURES    = create("use-fixtures", "interact");

    /** Use doors, gates, trapdoors */
    Permission USE_DOORS       = create("use-doors", "interact");

    /** Use crafting/enchanting tables, etc. */
    Permission USE_OTHER       = create("use-other", "interact");

    /** Place armor stands, spawn eggs, etc. */
    Permission PLACE_ENTS      = create("place-ents", "ent-place");

    /** Place boats, minecarts, etc. */
    Permission PLACE_VEHICLES  = create("place-vehicles", "vehicle-place");

    /** Create fire */
    Permission IGNITE          = create("ignite", "fire-use");

    /** Cause explosions */
    Permission EXPLODE         = create("explode");

    /** Damage cows, sheep, squid, etc. */
    Permission DAMAGE_FRIENDLY = create("damage-friendly", "entity-damage-friendly");

    /** Damage zombies, skeletons, etc. */
    Permission DAMAGE_HOSTILE  = create("damage-hostile", "entity-damage-hostile");

    /** Damage boats, minecarts, etc. */
    Permission DAMAGE_VEHICLES = create("damage-vehicles", "entity-damage-vehicle");

    /** Damage all other entities */
    Permission DAMAGE_OTHER    = create("damage-other", "entity-damage-nl", "entity-damage-misc");

    /** Drop items */
    Permission DROP_ITEMS      = create("drop-items", "item-drop");

    /** Modify the claim settings */
    Permission MANAGE          = create("manage");

    /** Delete the claim */
    Permission DELETE          = create("delete");

    /** Deprecated alias for {@link #USE_STORAGE} */
    @Deprecated
    Permission CHEST_OPEN = USE_STORAGE;

    /** Deprecated alias for {@link #USE_OTHER} */
    @Deprecated
    Permission INTERACT = USE_OTHER;

    /** Deprecated alias for {@link #PLACE_ENTS} */
    @Deprecated
    Permission ENT_PLACE = PLACE_ENTS;

    /** Deprecated alias for {@link #PLACE_VEHICLES} */
    @Deprecated
    Permission VEHICLE_PLACE = PLACE_VEHICLES;

    /** Deprecated alias for {@link #IGNITE} */
    @Deprecated
    Permission FIRE_USE = IGNITE;

    /** Deprecated alias for {@link #DAMAGE_FRIENDLY} */
    @Deprecated
    Permission ENTITY_DAMAGE_FRIENDLY = DAMAGE_FRIENDLY;

    /** Deprecated alias for {@link #DAMAGE_HOSTILE} */
    @Deprecated
    Permission ENTITY_DAMAGE_HOSTILE = DAMAGE_HOSTILE;

    /** Deprecated alias for {@link #DAMAGE_VEHICLES} */
    @Deprecated
    Permission ENTITY_DAMAGE_VEHICLE = DAMAGE_VEHICLES;

    /** Deprecated alias for {@link #DAMAGE_OTHER} */
    @Deprecated
    Permission ENTITY_DAMAGE_NL = DAMAGE_OTHER;

    /** Deprecated alias for {@link #DAMAGE_OTHER} */
    @Deprecated
    Permission ENTITY_DAMAGE_MISC = DAMAGE_OTHER;

    /** Deprecated alias for {@link #DROP_ITEMS} */
    @Deprecated
    Permission ITEM_DROP = DROP_ITEMS;

    /** Server permission for admin override */
    String ADMIN_OVERRIDE = "xclaim.admin";

    //

    /**
     * Reports all Permission values sorted by ordinal
     */
    @Contract("-> new")
    static @NotNull Permission @NotNull [] values() {
        return PermissionImpl.values();
    }

    /**
     * Looks up a Permission constant by its ordinal
     */
    @ApiStatus.Internal
    static @NotNull Permission valueOf(int ordinal) throws IllegalArgumentException {
        return PermissionImpl.valueOf(ordinal);
    }

    /**
     * Looks up a Permission constant by its name.
     * The casing of the name is ignored, and underscores
     * are treated as hyphens.
     */
    static @NotNull Permission valueOf(@NotNull String name) throws IllegalArgumentException {
        return PermissionImpl.valueOf(name, false);
    }

    /**
     * Similar to {@link #valueOf(String)}, additionally
     * accounting for legacy permission names.
     * Prefer {@link #valueOf(String)} for new code.
     */
    @ApiStatus.Obsolete
    static @NotNull Permission match(@NotNull String name) throws IllegalArgumentException {
        return PermissionImpl.valueOf(name, true);
    }

    /**
     * Looks up a Permission constant by its {@link Permission#sqlName() SQL name}
     */
    @ApiStatus.Internal
    static @NotNull Permission fromSQLName(@NotNull String sqlName) {
        if ("del".equalsIgnoreCase(sqlName)) return DELETE;
        return PermissionImpl.valueOf(sqlName, false);
    }

    //

    @ApiStatus.Internal
    int ordinal();

    @NotNull String name();

    @Deprecated
    @NotNull @Unmodifiable List<String> legacyNames();

    @NotNull Translatable printName();

    @NotNull Translatable description();

    @NotNull TrustLevel defaultTrust();

    /**
     * Identical to {@link #name()} with the exclusion of {@code delete},
     * remapped to {@code del} for use as a column name in relational databases.
     */
    @ApiStatus.Internal
    default @NotNull String sqlName() {
        String ret = this.name();
        if ("delete".equals(ret)) return "del";
        return ret;
    }

}