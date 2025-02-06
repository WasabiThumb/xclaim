package io.github.wasabithumb.xclaim.platform;

import io.github.wasabithumb.xclaim.platform.data.PlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.data.SpongePlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.SpongePlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.sound.PlatformSound;
import io.github.wasabithumb.xclaim.platform.data.sound.SpongePlatformSound;
import io.github.wasabithumb.xclaim.platform.entity.*;
import io.github.wasabithumb.xclaim.platform.inventory.*;
import io.github.wasabithumb.xclaim.platform.user.PlatformConsoleUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformOfflineUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.platform.user.SpongePlatformUser;
import io.github.wasabithumb.xclaim.platform.world.*;
import io.github.wasabithumb.xclaim.util.RegistryUtil;
import io.github.wasabithumb.xclaim.util.annotations.ParamCasts;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Server;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.registry.DefaultedRegistryReference;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.chunk.WorldChunk;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;

public record SpongePlatformTypeAdapter(
        SpongePlatform platform
) implements PlatformTypeAdapter {

    @Override
    public SpongePlatformPersistentDataContainer pdc(
            @ParamCasts(DataHolder.Mutable.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return new SpongePlatformPersistentDataContainer(
                this.platform,
                this.handleCast(handle, DataHolder.Mutable.class)
        );
    }

    @Override
    public SpongePlatformEntity entity(
            @ParamCasts(Entity.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        Entity ent = this.handleCast(handle, Entity.class);
        if (ent instanceof ServerPlayer sp) {
            return new SpongePlatformPlayer(this.platform, sp);
        }
        return new SpongePlatformEntity(this.platform, ent);
    }

    @Override
    public PlatformEntityType entityType(
             @ParamCasts(EntityType.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return SpongePlatformEntityType.of(this.handleCast(handle, EntityType.class));
    }

    @Override
    public SpongePlatformPlayer player(
            @ParamCasts(ServerPlayer.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return new SpongePlatformPlayer(this.platform, this.handleCast(handle, ServerPlayer.class));
    }

    @Override
    public SpongePlatformInventory inventory(
            @ParamCasts(Inventory.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;

        Inventory i = this.handleCast(handle, Inventory.class);
        SpongePlatformInventory ret;

        ret = SpongePlatformCustomInventory.of(this.platform, i);
        if (ret != null) return ret;

        ret = new SpongePlatformInventory(this.platform, i);
        return ret;
    }

    @Override
    public PlatformEquipmentSlot equipmentSlot(
            @ParamCasts(EquipmentType.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;

        EquipmentType et = this.handleCast(handle, EquipmentType.class);
        DefaultedRegistryReference<EquipmentType> ref = et.asDefaultedReference(RegistryTypes.EQUIPMENT_TYPE);

        if (RegistryUtil.cataloguedReferenceEquals(RegistryTypes.EQUIPMENT_TYPE, ref, EquipmentTypes.class, "MAINHAND", "MAIN_HAND"))
            return PlatformEquipmentSlot.HAND;

        if (RegistryUtil.cataloguedReferenceEquals(RegistryTypes.EQUIPMENT_TYPE, ref, EquipmentTypes.class, "OFFHAND", "OFF_HAND"))
            return PlatformEquipmentSlot.OFF_HAND;

        if (RegistryUtil.referenceEquals(ref, EquipmentTypes.HEAD))
            return PlatformEquipmentSlot.HEAD;

        if (RegistryUtil.referenceEquals(ref, EquipmentTypes.CHEST))
            return PlatformEquipmentSlot.CHEST;

        if (RegistryUtil.referenceEquals(ref, EquipmentTypes.LEGS))
            return PlatformEquipmentSlot.LEGS;

        if (RegistryUtil.referenceEquals(ref, EquipmentTypes.FEET))
            return PlatformEquipmentSlot.FEET;

        throw new IllegalArgumentException("Cannot adapt non-standard equipment type: " + ref.location().formatted());
    }

    @Override
    public SpongePlatformItem item(
            @ParamCasts(ItemStack.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return new SpongePlatformItem(this.platform, this.handleCast(handle, ItemStack.class));
    }

    @Override
    @ApiStatus.Obsolete
    public PlatformMaterial material(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        if (handle instanceof ItemType it) {
            return SpongePlatformMaterial.adaptItem(it);
        } else {
            return SpongePlatformMaterial.adaptBlock(this.handleCast(handle, BlockType.class));
        }
    }

    @Override
    public PlatformSound sound(
            @ParamCasts(SoundType.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return SpongePlatformSound.adapt(this.handleCast(handle, SoundType.class));
    }

    @Override
    public PlatformUser user(
            @ParamCasts(User.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        User u = this.handleCast(handle, User.class);

        Optional<ServerPlayer> sp = u.player();
        if (sp.isPresent()) return new SpongePlatformPlayer(this.platform, sp.get());

        return new SpongePlatformUser(this.platform, u);
    }

    @Override
    public PlatformConsoleUser consoleUser(Object handle) throws IllegalArgumentException {
        if (handle == null) return null;
        return this.platform.users().console();
    }

    @Override
    @Contract("_ -> fail")
    public PlatformOfflineUser offlineUser(Object handle) {
        // Reason: User covers OfflineUser cases
        throw new UnsupportedOperationException("Sponge has no OfflineUser struct");
    }

    @Override
    public PlatformWorld world(
            @ParamCasts(ServerWorld.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return new SpongePlatformWorld(this.platform, this.handleCast(handle, ServerWorld.class));
    }

    @Override
    public PlatformChunk chunk(
            @ParamCasts(WorldChunk.class) Object handle
    ) throws IllegalArgumentException {
        if (handle == null) return null;
        return new SpongePlatformChunk(this.platform, this.handleCast(handle, WorldChunk.class));
    }

    @Override
    @Contract("_ -> fail")
    public PlatformBlock block(Object handle) {
        // Reason: BlockState is missing position
        throw new UnsupportedOperationException("Sponge has no Block struct");
    }

    @Override
    @Contract("_ -> fail")
    public PlatformLocation location(Object handle) {
        // Reason: ServerLocation is missing pitch, yaw
        throw new UnsupportedOperationException("Sponge has no Location struct");
    }

    @Contract("_, _ -> new")
    public @NotNull PlatformLocation location(
            @NotNull ServerLocation sl,
            @NotNull Vector3d rot
    ) {
        return new PlatformLocation(
                this.world(sl.world()),
                sl.x(), sl.y(), sl.z(),
                (float) rot.y(), (float) rot.x()
        );
    }

    //

    @Override
    public DataHolder.Mutable pdc(PlatformPersistentDataContainer object) {
        if (object == null) return null;
        return ((SpongePlatformPersistentDataContainer) object).handle();
    }

    @Override
    public Entity entity(PlatformEntity object) {
        if (object == null) return null;
        return ((SpongePlatformEntity) object).handle();
    }

    @Override
    public EntityType<?> entityType(PlatformEntityType object) {
        if (object == null) return null;
        if (object instanceof NamedPlatformEntityType named) {
            return SpongePlatformEntityType.parseNamed(named);
        } else {
            return ((SpongePlatformEntityType) object).handle();
        }
    }

    @Override
    public ServerPlayer player(PlatformPlayer object) {
        if (object == null) return null;
        return ((SpongePlatformPlayer) object).handle();
    }

    @Override
    public Inventory inventory(PlatformInventory object) {
        if (object == null) return null;
        return ((SpongePlatformInventory) object).handle();
    }

    @Override
    public EquipmentType equipmentSlot(PlatformEquipmentSlot object) {
        if (object == null) return null;

        return switch (object) {
            case HAND -> RegistryUtil.cataloguedReference(
                    RegistryTypes.EQUIPMENT_TYPE,
                    EquipmentTypes.class,
                    "MAINHAND", "MAIN_HAND"
            ).get();
            case OFF_HAND -> RegistryUtil.cataloguedReference(
                    RegistryTypes.EQUIPMENT_TYPE,
                    EquipmentTypes.class,
                    "OFFHAND", "OFF_HAND"
            ).get();
            case HEAD -> EquipmentTypes.HEAD.get();
            case CHEST, BODY -> EquipmentTypes.CHEST.get();
            case LEGS -> EquipmentTypes.LEGS.get();
            case FEET -> EquipmentTypes.FEET.get();
        };
    }

    @Override
    public ItemStack item(PlatformItem object) {
        if (object == null) return null;
        return ((SpongePlatformItem) object).handle();
    }

    /**
     * @see SpongePlatformMaterial#adaptItem(PlatformMaterial)
     * @see SpongePlatformMaterial#adaptBlock(PlatformMaterial)
     */
    @Override
    @Contract("_ -> fail")
    public Object material(PlatformMaterial object) {
        throw new UnsupportedOperationException("Sponge cannot adapt Material as there are multiple target types");
    }

    @Override
    public SoundType sound(PlatformSound object) {
        if (object == null) return null;
        return SpongePlatformSound.adapt(object);
    }

    @Override
    public User user(PlatformUser object) {
        return switch (object) {
            case null -> null;
            case SpongePlatformPlayer ply -> ply.handle().user();
            case SpongePlatformUser usr -> usr.handle();
            default -> throw new IllegalArgumentException("Sponge cannot adapt console user");
        };
    }

    @Override
    public Server consoleUser(PlatformConsoleUser object) {
        if (object == null) return null;
        return this.platform.server();
    }

    @Override
    @Contract("_ -> fail")
    public Object offlineUser(PlatformOfflineUser object) {
        throw new UnsupportedOperationException("Sponge has no OfflineUser struct");
    }

    @Override
    public ServerWorld world(PlatformWorld object) {
        if (object == null) return null;
        return ((SpongePlatformWorld) object).handle();
    }

    @Override
    public WorldChunk chunk(PlatformChunk object) {
        if (object == null) return null;
        return ((SpongePlatformChunk) object).handle();
    }

    @Override
    public ServerLocation block(PlatformBlock object) {
        if (object == null) return null;
        return ServerLocation.of(
                this.world(object.world()),
                object.x(), object.y(), object.z()
        );
    }

    @Override
    public ServerLocation location(PlatformLocation object) {
        if (object == null) return null;
        return ServerLocation.of(
                this.world(object.world()),
                object.x(), object.y(), object.z()
        );
    }

}
