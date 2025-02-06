package io.github.wasabithumb.xclaim.platform.event.adapter;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Event;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Queue;
import java.util.function.Consumer;

@ApiStatus.Internal
final class ConstructorAdapterInstance<E extends Event> implements AdapterInstance<E> {

    public static void find(
            @NotNull Class<? extends SpongePlatformEvent<?>> clazz,
            @NotNull Consumer<AdapterInstance<?>> consumer
    ) {
        if (Modifier.isAbstract(clazz.getModifiers()))
            throw new IllegalArgumentException("Cannot inspect constructors of abstract class " + clazz);

        for (Constructor<?> con : clazz.getDeclaredConstructors())
            find0(con, consumer);
    }

    private static void find0(@NotNull Constructor<?> constructor, @NotNull Consumer<AdapterInstance<?>> consumer) {
        Adapter annotation = constructor.getAnnotation(Adapter.class);
        if (annotation == null) return;

        Class<?>[] paramTypes = constructor.getParameterTypes();
        int paramTypeCount = paramTypes.length;

        if (paramTypeCount != 2) {
            throw new AssertionError("Constructor marked with @Adapter in " +
                    constructor.getDeclaringClass().getName() +
                    " should have 2 parameters, got " + paramTypeCount
            );
        }

        if (!paramTypes[0].isAssignableFrom(SpongePlatform.class)) {
            throw new AssertionError("Constructor marked with @Adapter in " +
                    constructor.getDeclaringClass().getName() +
                    " has first parameter of type " + paramTypes[0].getName() +
                    ", which is not assignable from SpongePlatform"
            );
        }

        Class<?> unqualifiedSpongeClass = paramTypes[1];
        if (!Event.class.isAssignableFrom(unqualifiedSpongeClass)) {
            throw new AssertionError("Constructor marked with @Adapter in " +
                    constructor.getDeclaringClass().getName() +
                    " has second parameter of type " + unqualifiedSpongeClass.getName() +
                    ", which is not a subclass of Event"
            );
        }
        Class<? extends Event> spongeClass = unqualifiedSpongeClass.asSubclass(Event.class);

        consumer.accept(new ConstructorAdapterInstance<>(spongeClass, constructor));
    }

    //

    private final Class<E> spongeClass;
    private final Constructor<?> constructor;

    private ConstructorAdapterInstance(@NotNull Class<E> spongeClass, @NotNull Constructor<?> constructor) {
        this.spongeClass = spongeClass;
        this.constructor = constructor;
    }

    //

    @Override
    public @NotNull Class<E> spongeClass() {
        return this.spongeClass;
    }

    @Override
    public void adapt(@NotNull SpongePlatform platform, @NotNull E spongeEvent, @NotNull Queue<PlatformEvent> out) {
        SpongePlatformEvent<?> event;
        try {
            event = (SpongePlatformEvent<?>) this.constructor.newInstance(platform, spongeEvent);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new AssertionError(e);
        }
        out.add(event);
    }

}
