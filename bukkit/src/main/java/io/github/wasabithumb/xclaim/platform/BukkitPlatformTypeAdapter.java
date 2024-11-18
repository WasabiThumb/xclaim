package io.github.wasabithumb.xclaim.platform;

import io.github.wasabithumb.xclaim.platform.data.PlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.data.sound.BukkitPlatformSound;
import io.github.wasabithumb.xclaim.platform.data.sound.NamedPlatformSound;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntityType;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.data.material.BukkitPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.BukkitPlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.data.sound.PlatformSound;
import io.github.wasabithumb.xclaim.platform.user.PlatformConsoleUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformOfflineUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitPlatformTypeAdapter implements PlatformTypeAdapter {

    protected final Plugin plugin;
    BukkitPlatformTypeAdapter(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    //

    @Override
    public BukkitPlatformPersistentDataContainer pdc(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        if (!(handle instanceof PersistentDataContainer pdc))
            throw new IllegalArgumentException("Object (" + handle + ") is not a PersistentDataContainer");
        return new BukkitPlatformPersistentDataContainer(this.plugin, pdc);
    }

    @Override
    public PlatformEntity entity(Object handle) throws IllegalArgumentException {
        return null;
    }

    @Override
    public PlatformEntityType entityType(Object handle) throws IllegalArgumentException {
        return null;
    }

    @Override
    public PlatformPlayer player(Object handle) throws IllegalArgumentException {
        return null;
    }

    @Override
    public PlatformInventory<?> inventory(Object handle) throws IllegalArgumentException {
        return null;
    }

    @Override
    public PlatformItem item(Object handle) throws IllegalArgumentException {
        return null;
    }

    @Override
    public PlatformMaterial material(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        if (!(handle instanceof Material m))
            throw new IllegalArgumentException("Object (" + handle + ") is not a Material");
        return BukkitPlatformMaterial.of(m);
    }

    @Override
    public PlatformSound sound(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        if (!(handle instanceof Sound s))
            throw new IllegalArgumentException("Object (" + handle + ") is not a Sound");
        return BukkitPlatformSound.of(s);
    }

    @Override
    public PlatformUser user(Object handle) throws IllegalArgumentException {
        return null;
    }

    @Override
    public PlatformConsoleUser consoleUser(Object handle) throws IllegalArgumentException {
        return null;
    }

    @Override
    public PlatformOfflineUser offlineUser(Object handle) throws IllegalArgumentException {
        return null;
    }

    @Override
    public PlatformWorld world(Object handle) throws IllegalArgumentException {
        return null;
    }

    @Override
    public PlatformChunk chunk(Object handle) throws IllegalArgumentException {
        return null;
    }

    @Override
    public PlatformLocation location(Object handle) throws IllegalArgumentException {
        return null;
    }

    @Override
    public PersistentDataContainer pdc(PlatformPersistentDataContainer object) {
        if (object == null) return null;
        return ((BukkitPlatformPersistentDataContainer) object).handle();
    }

    @Override
    public Object entity(PlatformEntity object) {
        return null;
    }

    @Override
    public Object entityType(PlatformEntityType object) {
        return null;
    }

    @Override
    public Object player(PlatformPlayer object) {
        return null;
    }

    @Override
    public Object inventory(PlatformInventory<?> object) {
        return null;
    }

    @Override
    public Object item(PlatformItem object) {
        return null;
    }

    @Override
    public Material material(PlatformMaterial object) {
        if (object == null) return null;
        if (object instanceof NamedPlatformMaterial named) {
            return BukkitPlatformMaterial.parseNamed(named);
        }
        return ((BukkitPlatformMaterial) object).handle();
    }

    @Override
    public Sound sound(PlatformSound object) {
        if (object == null) return null;
        if (object instanceof NamedPlatformSound named) {
            return BukkitPlatformSound.parseNamed(named);
        }
        return ((BukkitPlatformSound) object).handle();
    }

    @Override
    public Object user(PlatformUser object) {
        return null;
    }

    @Override
    public Object consoleUser(PlatformConsoleUser object) {
        return null;
    }

    @Override
    public Object offlineUser(PlatformOfflineUser object) {
        return null;
    }

    @Override
    public Object world(PlatformWorld object) {
        return null;
    }

    @Override
    public Object chunk(PlatformChunk object) {
        return null;
    }

    @Override
    public Object location(PlatformLocation object) {
        return null;
    }

}
