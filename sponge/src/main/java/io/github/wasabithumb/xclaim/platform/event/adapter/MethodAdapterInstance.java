package io.github.wasabithumb.xclaim.platform.event.adapter;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.event.Event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Queue;
import java.util.function.Consumer;

@ApiStatus.Internal
sealed abstract class MethodAdapterInstance<E extends Event> implements AdapterInstance<E> {

    public static void find(
            @NotNull Class<?> clazz,
            @NotNull Consumer<AdapterInstance<?>> consumer
    ) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) find0(m, consumer);
    }

    private static void find0(@NotNull Method method, @NotNull Consumer<AdapterInstance<?>> consumer) {
        if (!Modifier.isStatic(method.getModifiers())) return;
        if (method.getAnnotation(Adapter.class) == null) return;

        Type[] paramTypes = method.getGenericParameterTypes();
        int paramTypeCount = paramTypes.length;
        boolean simple = switch (paramTypeCount) {
            case 2 -> true;
            case 3 -> false;
            default -> throw new AssertionError("Method marked with @Adapter in " +
                    method.getDeclaringClass().getName() + " has " + paramTypeCount + " parameters, expected 2-3");
        };

        if (!(paramTypes[0] instanceof Class<?> cls) || !cls.isAssignableFrom(SpongePlatform.class)) {
            throw new AssertionError("Method marked with @Adapter in " +
                    method.getDeclaringClass().getName() +
                    " has first parameter of type " + paramTypes[0].getTypeName() +
                    ", which is not assignable from SpongePlatform"
            );
        }

        Class<? extends Event> spongeClass;
        if (paramTypes[1] instanceof Class<?> cls1 && Event.class.isAssignableFrom(cls1)) {
            spongeClass = cls1.asSubclass(Event.class);
        } else {
            throw new AssertionError("Method marked with @Adapter in " +
                    method.getDeclaringClass().getName() +
                    " has second parameter of type " + paramTypes[1].getTypeName() +
                    ", which is not a subclass of Event"
            );
        }

        if (simple) {
            consumer.accept(new Simple<>(spongeClass, method));
            return;
        }

        Type queueType = paramTypes[2];
        if (!(queueType instanceof ParameterizedType pt) ||
            !pt.getRawType().equals(Queue.class) ||
            !pt.getActualTypeArguments()[0].equals(PlatformEvent.class)
        ) {
            throw new AssertionError("Method marked with @Adapter in " +
                    method.getDeclaringClass().getName() +
                    " has third parameter of type " + queueType.getTypeName() +
                    ", which is not a Queue<PlatformEvent>"
            );
        }

        consumer.accept(new WithQueue<>(spongeClass, method));
    }

    //

    protected final Class<E> spongeClass;
    protected final Method method;

    protected MethodAdapterInstance(
            @NotNull Class<E> spongeClass,
            @NotNull Method method
    ) {
        this.spongeClass = spongeClass;
        this.method = method;
    }

    @Override
    public @NotNull Class<E> spongeClass() {
        return this.spongeClass;
    }

    @Override
    public void adapt(
            @NotNull SpongePlatform platform,
            @NotNull E spongeEvent,
            @NotNull Queue<PlatformEvent> out
    ) {
        try {
            this.adaptInternal(platform, spongeEvent, out);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new AssertionError(e);
        }
    }

    protected abstract void adaptInternal(
            @NotNull SpongePlatform platform,
            @NotNull E spongeEvent,
            @NotNull Queue<PlatformEvent> out
    ) throws ReflectiveOperationException;

    //

    private static final class Simple<E extends Event> extends MethodAdapterInstance<E> {

        private Simple(@NotNull Class<E> spongeClass, @NotNull Method method) {
            super(spongeClass, method);
        }

        @Override
        protected void adaptInternal(
                @NotNull SpongePlatform platform,
                @NotNull E spongeEvent,
                @NotNull Queue<PlatformEvent> out
        ) throws ReflectiveOperationException {
            SpongePlatformEvent<?> event = (SpongePlatformEvent<?>) this.method.invoke(
                    null,
                    platform,
                    spongeEvent
            );
            out.add(event);
        }

    }

    //

    private static final class WithQueue<E extends Event> extends MethodAdapterInstance<E> {

        private WithQueue(@NotNull Class<E> spongeClass, @NotNull Method method) {
            super(spongeClass, method);
        }

        @Override
        protected void adaptInternal(
                @NotNull SpongePlatform platform,
                @NotNull E spongeEvent,
                @NotNull Queue<PlatformEvent> out
        ) throws ReflectiveOperationException {
            this.method.invoke(
                    null,
                    platform,
                    spongeEvent,
                    out
            );
        }

    }

}
