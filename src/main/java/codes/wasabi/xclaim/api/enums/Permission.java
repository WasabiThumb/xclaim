package codes.wasabi.xclaim.api.enums;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import codes.wasabi.xclaim.api.enums.permission.handler.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

public enum Permission {
    BUILD("perm-build-name", "perm-build-description", TrustLevel.TRUSTED, BuildBreakHandler.Build.class),
    BREAK("perm-break-name", "perm-break-description", TrustLevel.TRUSTED, BuildBreakHandler.Break.class),
    ENTER("perm-enter-name", "perm-enter-description", TrustLevel.ALL, EnterHandler.class),
    INTERACT("perm-interact-name", "perm-interact-description", TrustLevel.VETERANS, InteractHandler.class),
    CHEST_OPEN("perm-chest-open-name", "perm-chest-open-description", TrustLevel.TRUSTED, InteractHandler.Chests.class, (byte) 1),
    ENT_PLACE("perm-ent-place-name", "perm-ent-place-description", TrustLevel.VETERANS, InteractHandler.Entities.class, (byte) 1),
    VEHICLE_PLACE("perm-vehicle-place-name", "perm-vehicle-place-description", TrustLevel.VETERANS, InteractHandler.Vehicles.class, (byte) 2),
    FIRE_USE("perm-fire-use-name", "perm-fire-use-description", TrustLevel.TRUSTED, InteractHandler.Flammable.class, (byte) 1),
    ENTITY_DAMAGE_FRIENDLY("perm-entity-damage-friendly-name", "perm-entity-damage-friendly-description", TrustLevel.VETERANS, DamageHandler.Friendly.class),
    ENTITY_DAMAGE_HOSTILE("perm-entity-damage-hostile-name", "perm-entity-damage-hostile-description", TrustLevel.VETERANS, DamageHandler.Hostile.class),
    ENTITY_DAMAGE_VEHICLE("perm-entity-damage-vehicle-name", "perm-entity-damage-vehicle-description", TrustLevel.VETERANS, DamageHandler.Vehicle.class),
    ENTITY_DAMAGE_NL("perm-entity-damage-nl-name", "perm-entity-damage-nl-description", TrustLevel.VETERANS, DamageHandler.NonLiving.class),
    ENTITY_DAMAGE_MISC("perm-entity-damage-misc-name", "perm-entity-damage-misc-description", TrustLevel.ALL, DamageHandler.Misc.class),
    ITEM_DROP("perm-item-drop-name", "perm-item-drop-description", TrustLevel.ALL, DropHandler.class),
    MANAGE("perm-manage-name", "perm-manage-description", TrustLevel.NONE),
    DELETE("perm-delete-name", "perm-delete-description", TrustLevel.NONE);

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
            p = switch (name) {
                case "ENTITY_DAMAGE" -> Permission.ENTITY_DAMAGE_NL;
                case "MOB_DAMAGE" -> Permission.ENTITY_DAMAGE_HOSTILE;
                case "FRIENDLY_MOB_DAMAGE" -> Permission.ENTITY_DAMAGE_FRIENDLY;
                case "LIVING_ENTITY_DAMAGE" -> null;
                default -> throw e;
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