package io.github.wasabithumb.xclaim.platform.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class PlatformEventManager {

    public abstract void register(@NotNull PlatformListener listener);

    public abstract void unregister(@NotNull PlatformListener listener);

    protected abstract void reportIssue(@NotNull PlatformListener listener, @NotNull Throwable issue);

    //

    protected final @NotNull List<Entry> processEntries(@NotNull PlatformListener listener) {
        final Class<?> clazz = listener.getClass();
        final Method[] methods = clazz.getDeclaredMethods();
        final Entry[] entries = new Entry[methods.length];
        int count = 0;
        Entry next;

        for (Method method : methods) {
            try {
                next = this.processEntry(listener, method);
            } catch (ReflectiveOperationException | SecurityException e) {
                this.reportIssue(listener, e);
                continue;
            }
            if (next == null) continue;
            entries[count++] = next;
        }

        if (count == 0) {
            this.reportIssue(listener, new AssertionError("Listener has no event handlers"));
            return Collections.emptyList();
        }

        return Arrays.asList(entries)
                .subList(0, count);
    }

    private @Nullable Entry processEntry(@NotNull PlatformListener listener, @NotNull Method method) throws ReflectiveOperationException, SecurityException {
        final PlatformEventHandler annotation = method.getAnnotation(PlatformEventHandler.class);
        if (annotation == null) return null;

        final Class<?>[] params = method.getParameterTypes();
        if (params.length != 1) {
            this.reportIssue(
                    listener,
                    new AssertionError(
                            "Method \"" + method.getName() +
                            "\" should have 1 parameter, got " + params.length
                    )
            );
            return null;
        }

        final Class<?> paramType = params[0];
        if (paramType == null || !PlatformEvent.class.isAssignableFrom(paramType)) {
            this.reportIssue(
                    listener,
                    new AssertionError(
                            "Method \"" + method.getName() +
                                    "\" does not accept a valid Event class"
                    )
            );
            return null;
        }

        Field typeField;
        try {
            typeField = paramType.getDeclaredField("TYPE");
        } catch (NoSuchFieldException e) {
            this.reportIssue(
                    listener,
                    new AssertionError(
                            "Event class (" + paramType.getName() + ") has no TYPE field",
                            e
                    )
            );
            return null;
        }

        if (!Modifier.isStatic(typeField.getModifiers())) {
            this.reportIssue(
                    listener,
                    new AssertionError(
                            "TYPE field on event class (" + paramType.getName() +
                                    ") is not static"
                    )
            );
            return null;
        }

        Object typeUnqualified = typeField.get(null);
        if (!(typeUnqualified instanceof PlatformEventType type)) {
            this.reportIssue(
                    listener,
                    new AssertionError(
                            "TYPE field on event class (" + paramType.getName() +
                                    ") is of wrong type (got " + typeUnqualified.getClass().getName() + ")"
                    )
            );
            return null;
        }

        return new Entry(
                listener,
                method,
                type,
                annotation.category()
        );
    }

    //

    protected record Entry(
            @NotNull PlatformListener listener,
            @NotNull Method method,
            @NotNull PlatformEventType type,
            @NotNull PlatformEventCategory category
    ) { }

}
