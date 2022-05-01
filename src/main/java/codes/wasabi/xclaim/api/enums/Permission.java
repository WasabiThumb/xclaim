package codes.wasabi.xclaim.api.enums;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import codes.wasabi.xclaim.api.enums.permission.handler.*;
import org.jetbrains.annotations.NotNull;

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
    ENTITY_DAMAGE("Damage Entities", "Cause damage to entities like item frames, minecarts, and mobs", TrustLevel.VETERANS, DamageHandler.All.class),
    LIVING_ENTITY_DAMAGE("Damage Living Entities", "Cause damage to entities that are deemed as \"living\" like mobs", TrustLevel.VETERANS, DamageHandler.Living.class, (byte) 1),
    MOB_DAMAGE("Damage Mobs", "Cause damage to mobs", TrustLevel.VETERANS, DamageHandler.Mob.class, (byte) 2),
    FRIENDLY_MOB_DAMAGE("Damage Friendly Mobs", "Cause damage to friendly mobs", TrustLevel.VETERANS, DamageHandler.Friendly.class, (byte) 3),
    ITEM_DROP("Drop Items", "Drop items", TrustLevel.ALL, DropHandler.class),
    MANAGE("Manage Claim", "Modify the claim settings", TrustLevel.NONE),
    DELETE("Remove Claim", "Remove the claim", TrustLevel.NONE);

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