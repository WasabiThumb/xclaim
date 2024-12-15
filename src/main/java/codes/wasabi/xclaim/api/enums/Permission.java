package codes.wasabi.xclaim.api.enums;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import codes.wasabi.xclaim.api.enums.permission.handler.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Locale;

public enum Permission {
    BUILD("perm-build-name", "perm-build-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultBuild()), BuildBreakHandler.Build.class),
    BREAK("perm-break-name", "perm-break-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultBreak()), BuildBreakHandler.Break.class),
    ENTER("perm-enter-name", "perm-enter-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultEnter()), EnterHandler.class),
    INTERACT("perm-interact-name", "perm-interact-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultInteract()), InteractHandler.class),
    CHEST_OPEN("perm-chest-open-name", "perm-chest-open-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultChestOpen()), InteractHandler.Chests.class, (byte) 1),
    ENT_PLACE("perm-ent-place-name", "perm-ent-place-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultEntPlace()), InteractHandler.Entities.class, (byte) 1),
    VEHICLE_PLACE("perm-vehicle-place-name", "perm-vehicle-place-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultVehiclePlace()), InteractHandler.Vehicles.class, (byte) 2),
    FIRE_USE("perm-fire-use-name", "perm-fire-use-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultFireUse()), InteractHandler.Flammable.class, (byte) 1),
    ENTITY_DAMAGE_FRIENDLY("perm-entity-damage-friendly-name", "perm-entity-damage-friendly-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultEntFriendly()), DamageHandler.Friendly.class),
    ENTITY_DAMAGE_HOSTILE("perm-entity-damage-hostile-name", "perm-entity-damage-hostile-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultEntDamageHost()), DamageHandler.Hostile.class),
    ENTITY_DAMAGE_VEHICLE("perm-entity-damage-vehicle-name", "perm-entity-damage-vehicle-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultEntDamageVehicle()), DamageHandler.Vehicle.class),
    ENTITY_DAMAGE_NL("perm-entity-damage-nl-name", "perm-entity-damage-nl-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultEntDamageNL()), DamageHandler.NonLiving.class),
    ENTITY_DAMAGE_MISC("perm-entity-damage-misc-name", "perm-entity-damage-misc-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultEntDamageMisc()), DamageHandler.Misc.class),
    EXPLODE("perm-explode-name", "perm-explode-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultExplode()), ExplosionHandler.class),
    ITEM_DROP("perm-item-drop-name", "perm-item-drop-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultItemDrop()), DropHandler.class),
    MANAGE("perm-manage-name", "perm-manage-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultManage())),
    DELETE("perm-delete-name", "perm-delete-description", TrustLevel.fromString(XClaim.mainConfig.permissions().defaultDelete()));

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
            };
        }
        return p;
    }

    private final String printName;
    private final String description;
    private final TrustLevel defaultTrust;
    private final Class<? extends PermissionHandler> handlerClass;
    private final byte priority;
    Permission(String printName, String description, TrustLevel defaultTrust, Class<? extends PermissionHandler> handlerClass, byte priority) {
        this.printName = printName;
        this.description = description;
        this.defaultTrust = defaultTrust;
        this.handlerClass = handlerClass;
        this.priority = priority;
    }

    Permission(String printName, String description, TrustLevel defaultTrust, Class<? extends PermissionHandler> handlerClass) {
        this(printName, description, defaultTrust, handlerClass, (byte) 0);
    }

    Permission(String printName, String description, TrustLevel defaultTrust, byte priority) {
        this(printName, description, defaultTrust, null, priority);
    }

    Permission(String printName, String description, TrustLevel defaultTrust) {
        this(printName, description, defaultTrust, null, (byte) 0);
    }

    public String getDescription() {
        return XClaim.lang.get(description);
    }

    public String getPrintName() {
        return XClaim.lang.get(printName);
    }

    public TrustLevel getDefaultTrust() {
        return defaultTrust;
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

}