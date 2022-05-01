package codes.wasabi.xclaim.command.argument.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Collections;

public abstract class Type<T> {

    public static <T> @NotNull Type<T> auto(@NotNull Class<T> clazz) throws IllegalArgumentException {
        return auto(clazz, clazz.getSimpleName());
    }

    public static <T> @NotNull Type<T> auto(@NotNull Class<T> clazz, @NotNull String name) throws IllegalArgumentException {
        Method method = null;
        for (Method m : clazz.getDeclaredMethods()) {
            if ((m.getModifiers() & Modifier.STATIC) == 0) continue;
            if (!clazz.isAssignableFrom(m.getReturnType())) continue;
            Parameter[] params = m.getParameters();
            if (params.length != 1) continue;
            Parameter param = params[0];
            if (!param.getType().equals(String.class)) continue;
            method = m;
            break;
        }
        if (method == null) throw new IllegalArgumentException("Cannot find suitable string parser method for " + clazz.getName());
        final Method finalMethod = method;
        return new Type<>() {
            @Override
            public @NotNull Class<T> getTypeClass() {
                return clazz;
            }

            @Override
            public @NotNull String getTypeName() {
                return name;
            }

            @Override
            protected @NotNull T convert(@NotNull String string) throws Exception {
                Object ob = finalMethod.invoke(null, string);
                return clazz.cast(ob);
            }
        };
    }

    public abstract @NotNull Class<T> getTypeClass();
    public @NotNull String getTypeName() {
        return getTypeClass().getSimpleName();
    }
    public @NotNull Collection<String> getSampleValues() {
        return Collections.emptyList();
    }
    protected abstract @NotNull T convert(@NotNull String string) throws Exception;
    protected boolean validate(@NotNull T value) { return true; }
    public final @NotNull T parse(@NotNull String string) throws IllegalArgumentException {
        T ret;
        try {
            ret = convert(string);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        if (!validate(ret)) throw new IllegalArgumentException("Argument does not meet special requirements for argument type " + getClass().getName());
        return ret;
    }
    public final @Nullable T parseElseNull(@NotNull String string) {
        try {
            return parse(string);
        } catch (IllegalArgumentException ignored) { }
        return null;
    }


}
