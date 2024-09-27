package codes.wasabi.xclaim.api.enums;

import codes.wasabi.xclaim.platform.Platform;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.Predicate;

@ApiStatus.Internal
abstract class EntityGroupCheck implements Predicate<EntityType> {

    static @NotNull EntityGroupCheck alive(final boolean alive) {
        return new Simple(alive, EntityType::isAlive);
    }

    static @NotNull EntityGroupCheck armorStand(final boolean armorStand) {
        return new Simple(armorStand, (EntityType et) -> et.equals(EntityType.ARMOR_STAND));
    }

    static @NotNull EntityGroupCheck hostile(final boolean hostile) {
        return new Hostile(hostile);
    }

    static @NotNull EntityGroupCheck vehicle(final boolean vehicle) {
        return new Vehicle(vehicle);
    }

    static @NotNull EntityGroupCheck misc(final boolean misc) {
        final EnumSet<EntityType> miscTypes = Platform.get().getMiscTypes();
        return new Simple(misc, miscTypes::contains);
    }

    //

    protected final boolean value;
    EntityGroupCheck(final boolean value) {
        this.value = value;
    }

    //

    private static class Simple extends EntityGroupCheck {

        private final Predicate<EntityType> positive;
        Simple(boolean value, @NotNull Predicate<EntityType> positive) {
            super(value);
            this.positive = positive;
        }

        @Override
        public boolean test(EntityType entityType) {
            return this.positive.test(entityType) == this.value;
        }

    }

    //

    private static abstract class WithClass extends EntityGroupCheck {

        WithClass(boolean value) {
            super(value);
        }

        @Override
        public boolean test(@NotNull EntityType entityType) {
            Class<? extends Entity> clazz = entityType.getEntityClass();
            if (clazz == null) return false;
            return this.testClass(clazz) == this.value;
        }

        protected abstract boolean testClass(@NotNull Class<? extends Entity> clazz);

    }

    //

    private static class Hostile extends WithClass {

        private static final Class<? extends Entity> ENEMY_CLASS;
        private static final boolean ENEMY_CLASS_EXISTS;
        private static final String[] LEGACY_EXCEPTIONS;
        static {
            Class<? extends Entity> clazz = null;
            boolean classExists = false;
            String[] exceptions = null;

            try {
                clazz = Class.forName("org.bukkit.entity.Enemy")
                        .asSubclass(Entity.class);
                classExists = true;
            } catch (ClassNotFoundException | ClassCastException ignored) { }

            if (!classExists) {
                exceptions = new String[]{
                        "org.bukkit.entity.EnderDragon",
                        "org.bukkit.entity.Ghast",
                        "org.bukkit.entity.MagmaCube",
                        "org.bukkit.entity.Phantom",
                        "org.bukkit.entity.Shulker",
                        "org.bukkit.entity.Slime"
                };
            }

            ENEMY_CLASS = clazz;
            ENEMY_CLASS_EXISTS = classExists;
            LEGACY_EXCEPTIONS = exceptions;
        }

        Hostile(boolean value) {
            super(value);
        }

        @Override
        protected boolean testClass(@NotNull Class<? extends Entity> clazz) {
            if (ENEMY_CLASS_EXISTS) return ENEMY_CLASS.isAssignableFrom(clazz);
            boolean ret = Monster.class.isAssignableFrom(clazz);
            if (!ret) {
                String className = clazz.getName();
                for (String monster : LEGACY_EXCEPTIONS) {
                    if (monster.equals(className)) {
                        ret = true;
                        break;
                    }
                }
            }
            return ret;
        }

    }

    //

    private static class Vehicle extends WithClass {

        Vehicle(boolean value) {
            super(value);
        }

        @Override
        protected boolean testClass(@NotNull Class<? extends Entity> clazz) {
            return org.bukkit.entity.Vehicle.class.isAssignableFrom(clazz);
        }

    }

}
