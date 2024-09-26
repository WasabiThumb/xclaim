package codes.wasabi.xclaim.api.enums;

import static codes.wasabi.xclaim.api.enums.EntityGroupCheck.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

public enum EntityGroup {
    FRIENDLY(alive(true), armorStand(false), hostile(false)),
    HOSTILE(alive(true), armorStand(false), hostile(true)),
    VEHICLE(alive(false), vehicle(true)),
    NOT_ALIVE(true, armorStand(true), alive(false), misc(false), vehicle(false)),
    MISC(misc(true));

    private final Predicate<EntityType> predicate;
    private EnumSet<EntityType> list = null;
    EntityGroup(boolean or, EntityGroupCheck a, EntityGroupCheck... b) {
        Predicate<EntityType> predicate = a;
        if (b.length != 0) {
            Predicate<EntityType> extra = b[0];
            for (int i = 1; i < b.length; i++) {
                extra = extra.and(b[i]);
            }
            predicate = or ? predicate.or(extra) : predicate.and(extra);
        }
        this.predicate = predicate;
    }

    EntityGroup(EntityGroupCheck a, EntityGroupCheck... b) {
        this(false, a, b);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @Contract(" -> new")
    public @NotNull EnumSet<EntityType> getList() {
        synchronized (this) {
            if (this.list != null) return this.list;
            EnumSet<EntityType> set = EnumSet.noneOf(EntityType.class);
            for (EntityType type : EntityType.values()) {
                if (this.predicate.test(type)) set.add(type);
            }
            return this.list = set;
        }
    }

    @Deprecated
    public @NotNull @UnmodifiableView Set<EntityType> getSet() {
        return Collections.unmodifiableSet(this.getList());
    }

    public boolean contains(@NotNull EntityType type) {
        return this.predicate.test(type);
    }

    public boolean contains(@NotNull Entity entity) {
        return this.predicate.test(entity.getType());
    }

}
