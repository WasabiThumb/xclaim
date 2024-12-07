package io.github.wasabithumb.xclaim.platform.event;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;

interface BukkitPlatformEventAdapter {

    @NotNull PlatformEventType type();

    @NotNull Class<? extends Event> bukkitClass();

    @NotNull Class<? extends PlatformEvent> platformClass();

    @NotNull PlatformEvent adapt(@NotNull BukkitPlatform platform, @NotNull Event event);

    //

    record Reflect(
            @NotNull PlatformEventType type,
            @NotNull Class<? extends Event> bukkitClass,
            @NotNull Class<? extends PlatformEvent> platformClass,
            @NotNull Constructor<?> constructor
    ) implements BukkitPlatformEventAdapter {

        @Override
        public @NotNull PlatformEvent adapt(@NotNull BukkitPlatform platform, @NotNull Event event) {
            Object instance;
            try {
                instance = this.constructor.newInstance(platform, this.bukkitClass.cast(event));
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
            return this.platformClass.cast(instance);
        }

    }

}
