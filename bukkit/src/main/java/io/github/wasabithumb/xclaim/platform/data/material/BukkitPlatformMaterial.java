package io.github.wasabithumb.xclaim.platform.data.material;

import io.github.wasabithumb.xclaim.platform.PlatformObject;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public record BukkitPlatformMaterial(
        @NotNull Material handle
) implements PlatformMaterial, PlatformObject {

    public static @NotNull Material adapt(@NotNull PlatformMaterial material) {
        if (material instanceof NamedPlatformMaterial named) return parseNamed(named);
        return ((BukkitPlatformMaterial) material).handle;
    }

    public static @NotNull Material parseNamed(@NotNull NamedPlatformMaterial named) {
        return switch (named) {
            case PLAYER_HEAD      -> Material.PLAYER_HEAD;
            case GREEN_CONCRETE   -> Material.GREEN_CONCRETE;
            case RED_CONCRETE     -> Material.RED_CONCRETE;
            case SKELETON_SKULL   -> Material.SKELETON_SKULL;
            case ENCHANTING_TABLE -> Material.ENCHANTING_TABLE;
            case CHEST_MINECART   -> Material.CHEST_MINECART;
            case CRAFTING_TABLE   -> Material.CRAFTING_TABLE;
            case FIREWORK_ROCKET  -> Material.FIREWORK_ROCKET;
            case GREEN_DYE        -> Material.GREEN_DYE;
            case RED_DYE          -> Material.RED_DYE;
            case YELLOW_DYE       -> Material.YELLOW_DYE;
            case ORANGE_DYE       -> Material.ORANGE_DYE;
            case LIME_DYE         -> Material.LIME_DYE;
            case SHIELD           -> Material.SHIELD;
            case SPYGLASS         -> Material.SPYGLASS;
            case ARROW            -> Material.ARROW;
            case BARRIER          -> Material.BARRIER;
            case NETHER_STAR      -> Material.NETHER_STAR;
            case NAME_TAG         -> Material.NAME_TAG;
            case TNT              -> Material.TNT;
            case BUCKET           -> Material.BUCKET;
            case EMERALD          -> Material.EMERALD;
            case BOOK             -> Material.BOOK;
        };
    }

    public static @NotNull PlatformMaterial of(@NotNull Material material) {
        return switch (material) {
            case PLAYER_HEAD      -> NamedPlatformMaterial.PLAYER_HEAD;
            case GREEN_CONCRETE   -> NamedPlatformMaterial.GREEN_CONCRETE;
            case RED_CONCRETE     -> NamedPlatformMaterial.RED_CONCRETE;
            case SKELETON_SKULL   -> NamedPlatformMaterial.SKELETON_SKULL;
            case ENCHANTING_TABLE -> NamedPlatformMaterial.ENCHANTING_TABLE;
            case CHEST_MINECART   -> NamedPlatformMaterial.CHEST_MINECART;
            case CRAFTING_TABLE   -> NamedPlatformMaterial.CRAFTING_TABLE;
            case FIREWORK_ROCKET  -> NamedPlatformMaterial.FIREWORK_ROCKET;
            case GREEN_DYE        -> NamedPlatformMaterial.GREEN_DYE;
            case RED_DYE          -> NamedPlatformMaterial.RED_DYE;
            case YELLOW_DYE       -> NamedPlatformMaterial.YELLOW_DYE;
            case ORANGE_DYE       -> NamedPlatformMaterial.ORANGE_DYE;
            case LIME_DYE         -> NamedPlatformMaterial.LIME_DYE;
            case SHIELD           -> NamedPlatformMaterial.SHIELD;
            case SPYGLASS         -> NamedPlatformMaterial.SPYGLASS;
            case ARROW            -> NamedPlatformMaterial.ARROW;
            case BARRIER          -> NamedPlatformMaterial.BARRIER;
            case NETHER_STAR      -> NamedPlatformMaterial.NETHER_STAR;
            case NAME_TAG         -> NamedPlatformMaterial.NAME_TAG;
            case TNT              -> NamedPlatformMaterial.TNT;
            case BUCKET           -> NamedPlatformMaterial.BUCKET;
            case EMERALD          -> NamedPlatformMaterial.EMERALD;
            case BOOK             -> NamedPlatformMaterial.BOOK;
            default -> new BukkitPlatformMaterial(material);
        };
    }

    //

    @Override
    public @NotNull String name() {
        return this.handle.name();
    }

    @Override
    public boolean isSoil() {
        return this.handle.equals(Material.FARMLAND);
    }

    @Override
    public boolean ignites() {
        return this.handle.equals(Material.FLINT_AND_STEEL) || this.handle.equals(Material.FIRE_CHARGE);
    }

}
