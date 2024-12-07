package io.github.wasabithumb.xclaim.platform;

import io.github.wasabithumb.xclaim.platform.data.PlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntityType;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformEquipmentSlot;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.sound.PlatformSound;
import io.github.wasabithumb.xclaim.platform.user.PlatformConsoleUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformOfflineUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.platform.world.PlatformBlock;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface PlatformTypeAdapter {

    @Contract("null -> null; !null -> !null")
    PlatformPersistentDataContainer pdc(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformEntity entity(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformEntityType entityType(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformPlayer player(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformInventory inventory(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformEquipmentSlot equipmentSlot(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformItem item(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformMaterial material(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformSound sound(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformUser user(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformConsoleUser consoleUser(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformOfflineUser offlineUser(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformWorld world(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformChunk chunk(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformBlock block(Object handle) throws IllegalArgumentException;

    @Contract("null -> null; !null -> !null")
    PlatformLocation location(Object handle) throws IllegalArgumentException;
    
    @Contract("null -> null; !null -> !null")
    Object pdc(PlatformPersistentDataContainer object);

    @Contract("null -> null; !null -> !null")
    Object entity(PlatformEntity object);

    @Contract("null -> null; !null -> !null")
    Object entityType(PlatformEntityType object);

    @Contract("null -> null; !null -> !null")
    Object player(PlatformPlayer object);

    @Contract("null -> null; !null -> !null")
    Object inventory(PlatformInventory object);

    @Contract("null -> null; !null -> !null")
    Object equipmentSlot(PlatformEquipmentSlot object);

    @Contract("null -> null; !null -> !null")
    Object item(PlatformItem object);

    @Contract("null -> null; !null -> !null")
    Object material(PlatformMaterial object);

    @Contract("null -> null; !null -> !null")
    Object sound(PlatformSound object);

    @Contract("null -> null; !null -> !null")
    Object user(PlatformUser object);

    @Contract("null -> null; !null -> !null")
    Object consoleUser(PlatformConsoleUser object);

    @Contract("null -> null; !null -> !null")
    Object offlineUser(PlatformOfflineUser object);

    @Contract("null -> null; !null -> !null")
    Object world(PlatformWorld object);

    @Contract("null -> null; !null -> !null")
    Object chunk(PlatformChunk object);

    @Contract("null -> null; !null -> !null")
    Object block(PlatformBlock object);

    @Contract("null -> null; !null -> !null")
    Object location(PlatformLocation object);

    //

    default <T> @NotNull T handleCast(@NotNull Object handle, @NotNull Class<T> type) throws IllegalArgumentException {
        if (!type.isInstance(handle))
            throw new IllegalArgumentException("Handle (" + handle + ") is not of type " + type.getName());
        return type.cast(handle);
    }

}
