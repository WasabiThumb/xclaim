package codes.wasabi.xclaim.util.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ServiceFactory<T> {

    private final Class<? extends T>[] serviceClasses;

    @SafeVarargs
    public ServiceFactory(Class<? extends T>... serviceClasses) {
        for (Class<? extends T> clazz : serviceClasses) {
            int mod = clazz.getModifiers();
            if (Modifier.isInterface(mod) || Modifier.isAbstract(mod)) throw new IllegalArgumentException(clazz.getName() + " is abstract");
        }
        this.serviceClasses = Arrays.copyOf(serviceClasses, serviceClasses.length);
    }

    public @NotNull T create() throws ServiceInitException {
        ServiceInitException e = null;
        for (Class<? extends T> clazz : serviceClasses) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (InvocationTargetException e1) {
                Throwable cause = e1.getCause();
                if (cause instanceof ServiceInitException) {
                    e = (ServiceInitException) cause;
                } else {
                    throw new ServiceInitException("Unexpected error in initializer for class \"" + clazz.getName() + "\"", cause);
                }
            } catch (ReflectiveOperationException | SecurityException | LinkageError e1) {
                throw new ServiceInitException("Unexpected error in initializer for class \"" + clazz.getName() + "\"", e1);
            }
        }
        if (e != null) throw e;
        throw new ServiceInitException("No services set to be loaded");
    }

    public @Nullable T createElseNull(boolean log) {
        try {
            return create();
        } catch (ServiceInitException e) {
            if (log) e.printStackTrace();
        }
        return null;
    }

    public @Nullable T createElseNull() {
        return this.createElseNull(false);
    }

}
