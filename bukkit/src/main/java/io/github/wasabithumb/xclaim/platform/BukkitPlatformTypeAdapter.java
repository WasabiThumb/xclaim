package io.github.wasabithumb.xclaim.platform;

import io.github.wasabithumb.xclaim.platform.data.PlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.entity.*;
import io.github.wasabithumb.xclaim.platform.data.sound.BukkitPlatformSound;
import io.github.wasabithumb.xclaim.platform.data.sound.NamedPlatformSound;
import io.github.wasabithumb.xclaim.platform.inventory.*;
import io.github.wasabithumb.xclaim.platform.user.BukkitPlatformConsoleUser;
import io.github.wasabithumb.xclaim.platform.user.BukkitPlatformOfflineUser;
import io.github.wasabithumb.xclaim.platform.user.BukkitPlatformUser;
import io.github.wasabithumb.xclaim.platform.world.*;
import io.github.wasabithumb.xclaim.platform.data.material.BukkitPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.BukkitPlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.data.sound.PlatformSound;
import io.github.wasabithumb.xclaim.platform.user.PlatformConsoleUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformOfflineUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitPlatformTypeAdapter implements PlatformTypeAdapter {

    protected final BukkitPlatform platform;
    BukkitPlatformTypeAdapter(@NotNull BukkitPlatform platform) {
        this.platform = platform;
    }

    //

    @Override
    public BukkitPlatformPersistentDataContainer pdc(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        return new BukkitPlatformPersistentDataContainer(
                this.platform,
                this.handleCast(handle, PersistentDataContainer.class)
        );
    }

    @Override
    public BukkitPlatformEntity entity(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        if (handle instanceof Player) return this.player(handle);
        return new BukkitPlatformEntity(this.platform, this.handleCast(handle, Entity.class));
    }

    @Override
    public PlatformEntityType entityType(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        return BukkitPlatformEntityType.of(this.handleCast(handle, EntityType.class));
    }

    @Override
    public abstract BukkitPlatformPlayer player(Object handle) throws IllegalArgumentException;

    @Override
    public abstract BukkitPlatformInventory inventory(Object handle) throws IllegalArgumentException;

    @Override
    public PlatformEquipmentSlot equipmentSlot(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        return PlatformEquipmentSlot.valueOf(this.handleCast(handle, EquipmentSlot.class).name());
    }

    @Override
    public abstract BukkitPlatformItem item(Object handle) throws IllegalArgumentException;

    @Override
    public PlatformMaterial material(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        return BukkitPlatformMaterial.of(this.handleCast(handle, Material.class));
    }

    @Override
    public PlatformSound sound(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        return BukkitPlatformSound.of(this.handleCast(handle, Sound.class));
    }

    @Override
    public abstract BukkitPlatformUser user(Object handle) throws IllegalArgumentException;

    @Override
    public abstract BukkitPlatformConsoleUser consoleUser(Object handle) throws IllegalArgumentException;

    @Override
    public BukkitPlatformOfflineUser offlineUser(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        return new BukkitPlatformOfflineUser(this.handleCast(handle, OfflinePlayer.class));
    }

    @Override
    public BukkitPlatformWorld world(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        return new BukkitPlatformWorld(this.handleCast(handle, World.class));
    }

    @Override
    public BukkitPlatformChunk chunk(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        return new BukkitPlatformChunk(this.handleCast(handle, Chunk.class));
    }

    @Override
    public BukkitPlatformBlock block(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        return new BukkitPlatformBlock(this.handleCast(handle, Block.class));
    }

    @Override
    public PlatformLocation location(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        final Location loc = this.handleCast(handle, Location.class);
        return new PlatformLocation(
                world(loc.getWorld()),
                loc.getX(), loc.getY(), loc.getZ(),
                loc.getYaw(), loc.getPitch()
        );
    }

    @Override
    public PersistentDataContainer pdc(PlatformPersistentDataContainer object) {
        if (object == null) return null;
        return (PersistentDataContainer) object.handle();
    }

    @Override
    public Entity entity(PlatformEntity object) {
        if (object == null) return null;
        return (Entity) object.handle();
    }

    @Override
    public EntityType entityType(PlatformEntityType object) {
        if (object == null) return null;
        if (object instanceof NamedPlatformEntityType named) {
            return BukkitPlatformEntityType.parseNamed(named);
        }
        return ((BukkitPlatformEntityType) object).handle();
    }

    @Override
    public Player player(PlatformPlayer object) {
        if (object == null) return null;
        return (Player) object.handle();
    }

    @Override
    public Inventory inventory(PlatformInventory object) {
        if (object == null) return null;
        return (Inventory) object.handle();
    }

    @Override
    public EquipmentSlot equipmentSlot(PlatformEquipmentSlot object) {
        if (object == null) return null;
        return EquipmentSlot.valueOf(object.name());
    }

    @Override
    public ItemStack item(PlatformItem object) {
        if (object == null) return null;
        return (ItemStack) object.handle();
    }

    @Override
    public Material material(PlatformMaterial object) {
        if (object == null) return null;
        return BukkitPlatformMaterial.adapt(object);
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
    public CommandSender user(PlatformUser object) {
        if (object == null) return null;
        return ((BukkitPlatformUser) object).handle();
    }

    @Override
    public ConsoleCommandSender consoleUser(PlatformConsoleUser object) {
        if (object == null) return null;
        return ((BukkitPlatformConsoleUser) object).handle();
    }

    @Override
    public OfflinePlayer offlineUser(PlatformOfflineUser object) {
        if (object == null) return null;
        return ((BukkitPlatformOfflineUser) object).handle();
    }

    @Override
    public World world(PlatformWorld object) {
        if (object == null) return null;
        return (World) object.handle();
    }

    @Override
    public Chunk chunk(PlatformChunk object) {
        if (object == null) return null;
        return (Chunk) object.handle();
    }

    @Override
    public Block block(PlatformBlock object) {
        if (object == null) return null;
        return (Block) object.handle();
    }

    @Override
    public Location location(PlatformLocation object) {
        if (object == null) return null;
        return new Location(
                world(object.world()),
                object.x(), object.y(), object.z(),
                object.yaw(), object.pitch()
        );
    }

}
