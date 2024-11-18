package io.github.wasabithumb.xclaim.platform.data.material;

import io.github.wasabithumb.xclaim.platform.PlatformObject;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public record BukkitPlatformMaterial(
        @NotNull Material handle
) implements PlatformMaterial, PlatformObject {

    public static @NotNull Material parseNamed(@NotNull NamedPlatformMaterial named) {
        // NOTE: The names in the NamedPlatformMaterial happen to coincide with the Bukkit material names;
        // take care to ensure this remains the case.
        return Material.valueOf(named.name());
    }

    public static @NotNull BukkitPlatformMaterial of(@NotNull Material material) {
        return new BukkitPlatformMaterial(material);
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
