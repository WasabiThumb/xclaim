package codes.wasabi.xclaim.api.enums;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import codes.wasabi.xclaim.api.enums.permission.handler.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

public enum Permission {
    BUILD("Build", "Place blocks", TrustLevel.TRUSTED, BuildBreakHandler.Build.class),
    BREAK("Break", "Break blocks", TrustLevel.TRUSTED, BuildBreakHandler.Break.class),
    ENTER("Enter", "Walk into the area", TrustLevel.ALL, EnterHandler.class),
    INTERACT("Interact", "Interact with buttons, chests, etc.", TrustLevel.VETERANS, InteractHandler.class),
    CHEST_OPEN("Open Chests", "Interact with chests, for general interaction see Interact.", TrustLevel.TRUSTED, InteractHandler.Chests.class, (byte) 1),
    ENT_PLACE("Place Entities", "Put down boats, minecarts, armor stands, etc.", TrustLevel.VETERANS, InteractHandler.Entities.class, (byte) 1),
    VEHICLE_PLACE("Place Vehicles", "Put down vehicles like minecarts", TrustLevel.VETERANS, InteractHandler.Vehicles.class, (byte) 2),
    FIRE_USE("Use Flammables", "Use flint & steel and fire charges", TrustLevel.TRUSTED, InteractHandler.Flammable.class, (byte) 1),
    ENTITY_DAMAGE_FRIENDLY("Damage Friendly Entities", "Cause damage to things like cows, sheep, squid, etc", TrustLevel.VETERANS, DamageHandler.Friendly.class),
    ENTITY_DAMAGE_HOSTILE("Damage Hostile Entities", "Cause damage to things like zombies, skeletons, slimes, etc", TrustLevel.VETERANS, DamageHandler.Hostile.class),
    ENTITY_DAMAGE_VEHICLE("Damage Vehicles", "Cause damage to things like boats and minecarts", TrustLevel.VETERANS, DamageHandler.Vehicle.class),
    ENTITY_DAMAGE_NL("Damage Non-Living Entities", "Cause damage to things like armor stands and decoations", TrustLevel.VETERANS, DamageHandler.NonLiving.class),
    ENTITY_DAMAGE_MISC("Damage Miscellaneous  Entities", "Cause damage to entities that don't fall into any other group", TrustLevel.ALL, DamageHandler.Misc.class),
    ITEM_DROP("Drop Items", "Drop items", TrustLevel.ALL, DropHandler.class),
    MANAGE("Manage Claim", "Modify the claim settings", TrustLevel.NONE),
    DELETE("Remove Claim", "Remove the claim", TrustLevel.NONE);

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
        return description;
    }

    public String getPrintName() {
        return printName;
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