package io.github.wasabithumb.xclaim.platform.entity;

import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vehicle;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Predicate;

@ApiStatus.Internal
sealed abstract class BukkitPlatformEntityGroupCheck implements Predicate<Entity> {

    static boolean test(@NotNull PlatformEntityGroup group, @NotNull Entity entity) {
        return switch (group) {
            case FRIENDLY -> test(entity, alive(true), armorStand(false), hostile(false));
            case HOSTILE -> test(entity, alive(true), armorStand(false), hostile(true));
            case VEHICLE -> test(entity, alive(false), vehicle(true));
            case NOT_ALIVE -> test(entity, armorStand(true)) || test(entity, alive(false), misc(false), vehicle(false));
            case MISC -> test(entity, misc(true));
        };
    }

    @SafeVarargs
    private static boolean test(@NotNull Entity entity, @NotNull Predicate<Entity> @NotNull ... predicates) {
        for (Predicate<Entity> p : predicates) {
            if (!p.test(entity)) return false;
        }
        return true;
    }

    //

    private static @NotNull BukkitPlatformEntityGroupCheck alive(final boolean alive) {
        return new ByType(EntityType::isAlive, alive);
    }

    private static @NotNull BukkitPlatformEntityGroupCheck armorStand(final boolean armorStand) {
        return new ByType(EntityType.ARMOR_STAND::equals, armorStand);
    }

    private static @NotNull BukkitPlatformEntityGroupCheck hostile(final boolean hostile) {
        return new ByInheritance(Enemy.class, hostile);
    }

    private static @NotNull BukkitPlatformEntityGroupCheck vehicle(final boolean vehicle) {
        return new ByInheritance(Vehicle.class, vehicle);
    }

    private static @NotNull BukkitPlatformEntityGroupCheck misc(final boolean misc) {
        return new ByType((EntityType et) -> MISC_TYPES.contains(et.name()), misc);
    }

    private static final Set<String> MISC_TYPES = Set.of(
            "AREA_EFFECT_CLOUD",
            "ARROW",
            "DRAGON_FIREBALL",
            "DROPPED_ITEM",
            "EGG",
            "ENDER_CRYSTAL",
            "ENDER_PEARL",
            "ENDER_SIGNAL",
            "EVOKER_FANGS",
            "EXPERIENCE_ORB",
            "FALLING_BLOCK",
            "FIREBALL",
            "FIREWORK",
            "FISHING_HOOK",
            "LIGHTNING",
            "LLAMA_SPIT",
            "MARKER",
            "SMALL_FIREBALL",
            "SNOWBALL",
            "SPECTRAL_ARROW",
            "SPLASH_POTION",
            "THROWN_EXP_BOTTLE",
            "TRIDENT",
            "UNKNOWN"
    );

    //

    private static final class ByType extends BukkitPlatformEntityGroupCheck {

        private final Predicate<EntityType> predicate;
        private final boolean value;
        ByType(@NotNull Predicate<EntityType> predicate, boolean value) {
            this.predicate = predicate;
            this.value = value;
        }

        @Override
        public boolean test(Entity entity) {
            if (entity == null) return false;
            return this.predicate.test(entity.getType()) == this.value;
        }

    }

    private static final class ByInheritance extends BukkitPlatformEntityGroupCheck {

        private final Class<? extends Entity> superClass;
        private final boolean value;
        ByInheritance(@NotNull Class<? extends Entity> superClass, boolean value) {
            this.superClass = superClass;
            this.value = value;
        }

        @Override
        public boolean test(Entity entity) {
            if (entity == null) return false;
            return superClass.isAssignableFrom(entity.getClass()) == this.value;
        }

    }

}
