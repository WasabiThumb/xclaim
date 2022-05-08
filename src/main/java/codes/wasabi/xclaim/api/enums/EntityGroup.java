package codes.wasabi.xclaim.api.enums;

import org.bukkit.entity.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.Predicate;

public enum EntityGroup {
    FRIENDLY((EntityType et) -> {
        if (!et.isAlive()) return false;
        if (et.equals(EntityType.ARMOR_STAND)) return false;
        Class<? extends Entity> clazz = et.getEntityClass();
        if (clazz == null) return false;
        return !isMonster(clazz);
    }),
    HOSTILE((EntityType et) -> {
        if (!et.isAlive()) return false;
        if (et.equals(EntityType.ARMOR_STAND)) return false;
        Class<? extends Entity> clazz = et.getEntityClass();
        if (clazz == null) return false;
        return isMonster(clazz);
    }),
    VEHICLE((EntityType et) -> {
        if (et.isAlive()) return false;
        Class<? extends Entity> clazz = et.getEntityClass();
        if (clazz == null) return false;
        return Vehicle.class.isAssignableFrom(clazz);
    }),
    NOT_ALIVE((EntityType et) -> {
        if (et.equals(EntityType.ARMOR_STAND)) return true;
        if (et.isAlive()) return false;
        if (isMiscellaneous(et)) return false;
        Class<? extends Entity> clazz = et.getEntityClass();
        if (clazz == null) return true;
        return !Vehicle.class.isAssignableFrom(clazz);
    }),
    MISC(EntityGroup::isMiscellaneous);

    private static boolean isMonster(@NotNull Class<? extends Entity> clazz) {
        boolean ret = Monster.class.isAssignableFrom(clazz);
        if (!ret) {
            // None of these classes implement Monster for some reason. Fun!
            if (EnderDragon.class.isAssignableFrom(clazz)) {
                ret = true;
            } else if (Ghast.class.isAssignableFrom(clazz)) {
                ret = true;
            } else if (MagmaCube.class.isAssignableFrom(clazz)) {
                ret = true;
            } else if (Phantom.class.isAssignableFrom(clazz)) {
                ret = true;
            } else if (Shulker.class.isAssignableFrom(clazz)) {
                ret = true;
            } else if (Slime.class.isAssignableFrom(clazz)) {
                ret = true;
            }
        }
        return ret;
    }

    private static EnumSet<EntityType> miscTypes = null;
    private static boolean isMiscellaneous(@NotNull EntityType et) {
        // idk why i need to do this but java dies if i dont
        if (miscTypes == null) {
            miscTypes = EnumSet.of(
                    EntityType.AREA_EFFECT_CLOUD,
                    EntityType.ARROW,
                    EntityType.DRAGON_FIREBALL,
                    EntityType.DROPPED_ITEM,
                    EntityType.EGG,
                    EntityType.ENDER_CRYSTAL,
                    EntityType.ENDER_PEARL,
                    EntityType.ENDER_SIGNAL,
                    EntityType.EVOKER_FANGS,
                    EntityType.EXPERIENCE_ORB,
                    EntityType.FALLING_BLOCK,
                    EntityType.FIREBALL,
                    EntityType.FIREWORK,
                    EntityType.FISHING_HOOK,
                    EntityType.LIGHTNING,
                    EntityType.LLAMA_SPIT,
                    EntityType.MARKER,
                    EntityType.SMALL_FIREBALL,
                    EntityType.SNOWBALL,
                    EntityType.SPECTRAL_ARROW,
                    EntityType.SPLASH_POTION,
                    EntityType.THROWN_EXP_BOTTLE,
                    EntityType.TRIDENT,
                    EntityType.UNKNOWN
            );
        }
        return miscTypes.contains(et);
    }

    private final Predicate<EntityType> checker;
    private final EnumSet<EntityType> types;
    EntityGroup(@NotNull Predicate<EntityType> checker) {
        this.checker = checker;
        this.types = EnumSet.noneOf(EntityType.class);
        for (EntityType et : EntityType.values()) {
            if (checker.test(et)) types.add(et);
        }
    }

    EntityGroup(@NotNull EnumSet<EntityType> types) {
        this.checker = types::contains;
        this.types = types;
    }

    @Contract(" -> new")
    public @NotNull EnumSet<EntityType> getList() {
        return EnumSet.copyOf(types);
    }

    public boolean contains(@NotNull EntityType type) {
        return checker.test(type);
    }

    public boolean contains(@NotNull Entity entity) {
        return checker.test(entity.getType());
    }

}
