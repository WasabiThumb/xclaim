package io.github.wasabithumb.xclaim.platform.event;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;

public enum PlatformEventType {
    CHAT,
    ENTITY_PLACE,
    EXPLOSION,
    ENTITY_DAMAGED,
    HANGING_BREAK,
    ITEM_FRAME_CHANGE,
    PLAYER_INTERACT,
    PLAYER_INTERACT_ENTITY,
    PLAYER_DROP_ITEM,
    ENTITY_PICKUP_ITEM,
    PLAYER_MOVE,
    PLAYER_TELEPORT,
    PLAYER_JOIN,
    PLAYER_QUIT,
    ENTITY_DEATH,
    INVENTORY_CLICK,
    INVENTORY_DRAG,
    INVENTORY_CLOSE,
    BLOCK_PLACE,
    BLOCK_MULTI_PLACE,
    BLOCK_FLOW,
    BLOCK_BREAK;

    @ApiStatus.Internal
    public static @NotNull PlatformEventType of(@NotNull Class<? extends PlatformEvent> clazz) throws IllegalArgumentException {
        Field typeField;
        try {
            typeField = clazz.getField("TYPE");
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Event class (" + clazz.getName() + ") has no TYPE field", e);
        }

        if (!Modifier.isStatic(typeField.getModifiers()))
            throw new IllegalArgumentException("TYPE field on event class (" + clazz.getName() + ") is not static");

        try {
            typeField.setAccessible(true);
        } catch (InaccessibleObjectException | SecurityException ignored) { }

        Object typeUnqualified;
        try {
            typeUnqualified = typeField.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("TYPE field on event class (" + clazz.getName() + ") is inaccessible");
        }
        if (!(typeUnqualified instanceof PlatformEventType type)) {
            throw new IllegalArgumentException("TYPE field on event class (" + clazz.getName() +
                    ") is of wrong type (got " + typeUnqualified.getClass().getName() + ")");
        }
        return type;
    }
}
