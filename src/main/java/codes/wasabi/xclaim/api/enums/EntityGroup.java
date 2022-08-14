package codes.wasabi.xclaim.api.enums;

import codes.wasabi.xclaim.platform.Platform;
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
            String className = clazz.getSimpleName();
            // None of these classes implement Monster for some reason. Fun!
            String[] monsters = new String[]{ "EnderDragon", "Ghast", "MagmaCube", "Phantom", "Shulker", "Slime" };
            for (String monster : monsters) {
                if (monster.equals(className)) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

    private static boolean isMiscellaneous(@NotNull EntityType et) {
        return Platform.get().getMiscTypes().contains(et);
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
