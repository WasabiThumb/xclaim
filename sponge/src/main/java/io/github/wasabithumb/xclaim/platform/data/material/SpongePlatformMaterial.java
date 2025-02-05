package io.github.wasabithumb.xclaim.platform.data.material;

import io.github.wasabithumb.xclaim.util.RegistryUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.DefaultedRegistryReference;
import org.spongepowered.api.registry.RegistryReference;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class SpongePlatformMaterial implements PlatformMaterial {

    private static final int F_ITEM = 1;
    private static final int F_BLOCK = 2;

    @ApiStatus.Internal
    @Contract("-> new")
    private static @NotNull Builder builder() {
        return new Builder();
    }

    @ApiStatus.Internal
    @Contract("_, _ -> new")
    private static @NotNull SpongePlatformMaterial of(
            @NotNull DefaultedRegistryReference<ItemType> item,
            @NotNull DefaultedRegistryReference<BlockType> block
    ) {
        return builder().item(item).block(block).build();
    }

    @ApiStatus.Internal
    @Contract("_ -> new")
    private static @NotNull SpongePlatformMaterial item(
            @NotNull DefaultedRegistryReference<ItemType> item
    ) {
        return builder().item(item).build();
    }

    @ApiStatus.Internal
    private static @NotNull SpongePlatformMaterial ofNamed(@NotNull NamedPlatformMaterial named) {
        return switch (named) {
            case TNT -> of(ItemTypes.TNT, BlockTypes.TNT);
            case BOOK -> item(ItemTypes.BOOK);
            case ARROW -> item(ItemTypes.ARROW);
            case BUCKET -> item(ItemTypes.BUCKET);
            case SHIELD -> item(ItemTypes.SHIELD);
            case BARRIER -> of(ItemTypes.BARRIER, BlockTypes.BARRIER);
            case EMERALD -> item(ItemTypes.EMERALD);
            case RED_DYE -> item(ItemTypes.RED_DYE);
            case LIME_DYE -> item(ItemTypes.LIME_DYE);
            case NAME_TAG -> item(ItemTypes.NAME_TAG);
            case SPYGLASS -> item(ItemTypes.SPYGLASS);
            case GREEN_DYE -> item(ItemTypes.GREEN_DYE);
            case ORANGE_DYE -> item(ItemTypes.ORANGE_DYE);
            case YELLOW_DYE -> item(ItemTypes.YELLOW_DYE);
            case NETHER_STAR -> item(ItemTypes.NETHER_STAR);
            case PLAYER_HEAD -> of(ItemTypes.PLAYER_HEAD, BlockTypes.PLAYER_HEAD);
            case RED_CONCRETE -> of(ItemTypes.RED_CONCRETE, BlockTypes.RED_CONCRETE);
            case WRITTEN_BOOK -> item(ItemTypes.WRITTEN_BOOK);
            case CHEST_MINECART -> item(ItemTypes.CHEST_MINECART);
            case CRAFTING_TABLE -> of(ItemTypes.CRAFTING_TABLE, BlockTypes.CRAFTING_TABLE);
            case GREEN_CONCRETE -> of(ItemTypes.GREEN_CONCRETE, BlockTypes.GREEN_CONCRETE);
            case SKELETON_SKULL -> of(ItemTypes.SKELETON_SKULL, BlockTypes.SKELETON_SKULL);
            case FIREWORK_ROCKET -> item(ItemTypes.FIREWORK_ROCKET);
            case ENCHANTING_TABLE -> of(ItemTypes.ENCHANTING_TABLE, BlockTypes.ENCHANTING_TABLE);
        };
    }

    private static final Map<ItemType, NamedPlatformMaterial> ITEM_TO_NAMED = new HashMap<>();
    private static final Map<BlockType, NamedPlatformMaterial> BLOCK_TO_NAMED = new HashMap<>();
    static {
        for (NamedPlatformMaterial named : NamedPlatformMaterial.values()) {
            SpongePlatformMaterial material = ofNamed(named);
            if ((material.flags & F_ITEM) != 0) ITEM_TO_NAMED.put(material.item, named);
            if ((material.flags & F_BLOCK) != 0) BLOCK_TO_NAMED.put(material.block, named);
        }
    }

    public static @NotNull ItemType adaptItem(@NotNull PlatformMaterial material) {
        if (material instanceof NamedPlatformMaterial named) {
            return ofNamed(named).item();
        } else {
            return ((SpongePlatformMaterial) material).item();
        }
    }

    public static @NotNull PlatformMaterial adaptItem(@NotNull ItemType type) {
        NamedPlatformMaterial named = ITEM_TO_NAMED.get(type);
        if (named != null) return named;
        return builder().item(type).build();
    }

    public static @NotNull BlockType adaptBlock(@NotNull PlatformMaterial material) {
        if (material instanceof NamedPlatformMaterial named) {
            return ofNamed(named).block();
        } else {
            return ((SpongePlatformMaterial) material).block();
        }
    }

    public static @NotNull PlatformMaterial adaptBlock(@NotNull BlockType type) {
        NamedPlatformMaterial named = BLOCK_TO_NAMED.get(type);
        if (named != null) return named;
        return builder().block(type).build();
    }

    //

    private final String name;
    private final int flags;
    private final ItemType item;
    private final BlockType block;

    private SpongePlatformMaterial(
            @NotNull String name,
            int flags,
            @UnknownNullability ItemType item,
            @UnknownNullability BlockType block
    ) {
        this.name = name;
        this.flags = flags;
        this.item = item;
        this.block = block;
    }

    //

    public @NotNull ItemType item() throws UnsupportedOperationException {
        if ((this.flags & F_ITEM) == 0)
            throw new UnsupportedOperationException("Material \"" + this.name + "\" has no item type");
        return this.item;
    }

    public @NotNull BlockType block() throws UnsupportedOperationException {
        if ((this.flags & F_BLOCK) == 0)
            throw new UnsupportedOperationException("Material \"" + this.name + "\" has no block type");
        return this.block;
    }

    public boolean isItemType(@NotNull DefaultedRegistryReference<ItemType> itemRef) {
        if ((this.flags & F_ITEM) == 0) return false;
        return RegistryUtil.referenceEquals(RegistryTypes.ITEM_TYPE, this.item, itemRef);
    }

    public boolean isBlockType(@NotNull RegistryReference<BlockType> blockRef) {
        if ((this.flags & F_BLOCK) == 0) return false;
        return RegistryUtil.referenceEquals(RegistryTypes.BLOCK_TYPE, this.block, blockRef);
    }

    //

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public boolean isSoil() {
        return this.isBlockType(BlockTypes.FARMLAND);
    }

    @Override
    public boolean ignites() {
        return this.isItemType(ItemTypes.FLINT_AND_STEEL) ||
                this.isItemType(ItemTypes.FIRE_CHARGE);
    }

    @Override
    public boolean isBucket() {
        if ((this.flags & F_ITEM) == 0) return false;
        return this.name.toLowerCase(Locale.ROOT).contains("bucket");
    }

    //

    private static final class Builder {

        private String name = null;
        private int flags = 0;
        private ItemType item = null;
        private BlockType block = null;

        //

        @Contract("_ -> this")
        public @NotNull Builder item(@NotNull ItemType item) {
            if (this.flags == 0) {
                this.name = item.key(RegistryTypes.ITEM_TYPE).asString();
            }
            this.item = item;
            this.flags |= F_ITEM;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder item(@NotNull DefaultedRegistryReference<ItemType> item) {
            return this.item(item.get());
        }

        @Contract("_ -> this")
        public @NotNull Builder block(@NotNull BlockType block) {
            if (this.flags == 0) {
                this.name = block.key(RegistryTypes.BLOCK_TYPE).asString();
            }
            this.block = block;
            this.flags |= F_BLOCK;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder block(@NotNull DefaultedRegistryReference<BlockType> block) {
            return this.block(block.get());
        }

        @Contract("-> new")
        public @NotNull SpongePlatformMaterial build() throws IllegalStateException {
            if (this.flags == 0)
                throw new IllegalStateException("Cannot create material with no ItemType or BlockType");
            return new SpongePlatformMaterial(this.name, this.flags, this.item, this.block);
        }

    }

}
