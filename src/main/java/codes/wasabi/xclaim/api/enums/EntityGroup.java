package codes.wasabi.xclaim.api.enums;

import static codes.wasabi.xclaim.api.enums.EntityGroupCheck.*;

import codes.wasabi.xclaim.debug.Debuggable;
import codes.wasabi.xclaim.debug.goal.DebugGoal;
import codes.wasabi.xclaim.debug.writer.DebugWriter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

@Debuggable
public enum EntityGroup {
    FRIENDLY(alive(true), armorStand(false), hostile(false)),
    HOSTILE(alive(true), armorStand(false), hostile(true)),
    VEHICLE(alive(false), vehicle(true)),
    NOT_ALIVE(true, armorStand(true), alive(false), misc(false), vehicle(false)),
    MISC(misc(true));

    private final Predicate<EntityType> predicate;
    private EnumSet<EntityType> set = null;
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

    private @NotNull EnumSet<EntityType> getSet0() {
        synchronized (this) {
            if (this.set != null) return this.set;
            EnumSet<EntityType> set = EnumSet.noneOf(EntityType.class);
            for (EntityType type : EntityType.values()) {
                if (this.predicate.test(type)) set.add(type);
            }
            return this.set = set;
        }
    }

    public @NotNull @UnmodifiableView Set<EntityType> getSet() {
        return Collections.unmodifiableSet(this.getSet0());
    }

    /**
     * @deprecated Use {@link #getSet()}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    @Contract(" -> new")
    public @NotNull EnumSet<EntityType> getList() {
        return this.getSet0();
    }

    public boolean contains(@NotNull EntityType type) {
        return this.predicate.test(type);
    }

    public boolean contains(@NotNull Entity entity) {
        return this.predicate.test(entity.getType());
    }

    // START Debug

    @ApiStatus.Internal
    @DebugGoal(async = true)
    public static void debug(@NotNull DebugWriter out) {
        for (EntityGroup eg : values()) {
            out.color(NamedTextColor.GOLD);
            out.println("= " + eg.name() + " =");

            out.color(NamedTextColor.WHITE);
            for (EntityType et : eg.getSet()) {
                out.println("- " + et.name());
            }
            out.println();
        }
    }

    // END Debug

}
