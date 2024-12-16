package codes.wasabi.xclaim.api.enums;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import codes.wasabi.xclaim.api.enums.permission.handler.*;
import codes.wasabi.xclaim.config.struct.sub.DefaultPermissionsConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.function.Function;

public enum Permission {
    BUILD("perm-build-name", "perm-build-description", DefaultPermissionsConfig::build, BuildBreakHandler.Build.class),
    BREAK("perm-break-name", "perm-break-description", DefaultPermissionsConfig::breakBlocks, BuildBreakHandler.Break.class),
    ENTER("perm-enter-name", "perm-enter-description", DefaultPermissionsConfig::enter, EnterHandler.class),
    INTERACT("perm-interact-name", "perm-interact-description", DefaultPermissionsConfig::interact, InteractHandler.class),
    CHEST_OPEN("perm-chest-open-name", "perm-chest-open-description", DefaultPermissionsConfig::chestOpen, InteractHandler.Chests.class, (byte) 1),
    ENT_PLACE("perm-ent-place-name", "perm-ent-place-description", DefaultPermissionsConfig::entPlace, InteractHandler.Entities.class, (byte) 1),
    VEHICLE_PLACE("perm-vehicle-place-name", "perm-vehicle-place-description", DefaultPermissionsConfig::vehiclePlace, InteractHandler.Vehicles.class, (byte) 2),
    FIRE_USE("perm-fire-use-name", "perm-fire-use-description", DefaultPermissionsConfig::fireUse, InteractHandler.Flammable.class, (byte) 1),
    ENTITY_DAMAGE_FRIENDLY("perm-entity-damage-friendly-name", "perm-entity-damage-friendly-description", DefaultPermissionsConfig::entDamageFriendly, DamageHandler.Friendly.class),
    ENTITY_DAMAGE_HOSTILE("perm-entity-damage-hostile-name", "perm-entity-damage-hostile-description", DefaultPermissionsConfig::entDamageHostile, DamageHandler.Hostile.class),
    ENTITY_DAMAGE_VEHICLE("perm-entity-damage-vehicle-name", "perm-entity-damage-vehicle-description", DefaultPermissionsConfig::entDamageVehicle, DamageHandler.Vehicle.class),
    ENTITY_DAMAGE_NL("perm-entity-damage-nl-name", "perm-entity-damage-nl-description", DefaultPermissionsConfig::entDamageNL, DamageHandler.NonLiving.class),
    ENTITY_DAMAGE_MISC("perm-entity-damage-misc-name", "perm-entity-damage-misc-description", DefaultPermissionsConfig::entDamageMisc, DamageHandler.Misc.class),
    EXPLODE("perm-explode-name", "perm-explode-description", DefaultPermissionsConfig::explode, ExplosionHandler.class),
    ITEM_DROP("perm-item-drop-name", "perm-item-drop-description", DefaultPermissionsConfig::itemDrop, DropHandler.class),
    MANAGE("perm-manage-name", "perm-manage-description", DefaultPermissionsConfig::manage),
    DELETE("perm-delete-name", "perm-delete-description", DefaultPermissionsConfig::delete);

    /**
     * Wraps #valueOf with legacy support
     * @param name Name of the permission
     * @return The permission, or null if that permission is no longer supported.
     * @throws IllegalArgumentException No permission has existed with that name
     */
    public static @Nullable Permission fromName(@NotNull String name) throws IllegalArgumentException {
        Permission p;
        try {
            p = valueOf(name);
        } catch (IllegalArgumentException e) {
            // check for legacy names
            switch (name) {
                case "ENTITY_DAMAGE":
                    p = Permission.ENTITY_DAMAGE_NL;
                    break;
                case "MOB_DAMAGE":
                    p = Permission.ENTITY_DAMAGE_HOSTILE;
                    break;
                case "FRIENDLY_MOB_DAMAGE":
                    p = Permission.ENTITY_DAMAGE_FRIENDLY;
                    break;
                case "LIVING_ENTITY_DAMAGE":
                    p = null;
                    break;
                default:
                    throw e;
            }
        }
        return p;
    }

    private final String printName;
    private final String description;
    private final Function<DefaultPermissionsConfig, String> computeDefaultTrust;
    private final Class<? extends PermissionHandler> handlerClass;
    private final byte priority;
    Permission(String printName, String description, Function<DefaultPermissionsConfig, String> computeDefaultTrust, Class<? extends PermissionHandler> handlerClass, byte priority) {
        this.printName = printName;
        this.description = description;
        this.computeDefaultTrust = computeDefaultTrust;
        this.handlerClass = handlerClass;
        this.priority = priority;
    }

    Permission(String printName, String description, Function<DefaultPermissionsConfig, String> computeDefaultTrust, Class<? extends PermissionHandler> handlerClass) {
        this(printName, description, computeDefaultTrust, handlerClass, (byte) 0);
    }

    Permission(String printName, String description, Function<DefaultPermissionsConfig, String> computeDefaultTrust, byte priority) {
        this(printName, description, computeDefaultTrust, null, priority);
    }

    Permission(String printName, String description, Function<DefaultPermissionsConfig, String> computeDefaultTrust) {
        this(printName, description, computeDefaultTrust, null, (byte) 0);
    }

    public String getDescription() {
        return XClaim.lang.get(description);
    }

    public String getPrintName() {
        return XClaim.lang.get(printName);
    }

    public byte getPriority() {
        return priority;
    }

    public boolean hasHandler() {
        return handlerClass != null;
    }

    public @NotNull PermissionHandler createHandler(@NotNull Claim claim) throws IllegalStateException {
        if (handlerClass == null) throw new IllegalStateException("No handler class is defined for permission " + name());
        PermissionHandler ph;
        try {
            Constructor<? extends PermissionHandler> construct = handlerClass.getConstructor(Claim.class);
            ph = construct.newInstance(claim);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return ph;
    }

    public @NotNull TrustLevel getDefaultTrust() {
        DefaultPermissionsConfig config = XClaim.mainConfig.defaultPermissions(); // Get the current PermissionsConfig
        String rawTrust = computeDefaultTrust.apply(config);// Fetch raw trust level string using the function
        return TrustLevel.fromString(rawTrust); // Convert string to TrustLevel
    }

}