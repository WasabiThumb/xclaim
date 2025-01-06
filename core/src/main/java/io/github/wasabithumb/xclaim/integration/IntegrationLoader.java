package io.github.wasabithumb.xclaim.integration;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.XClaimBootstrap;
import io.github.wasabithumb.xclaim.asset.AssetManager;
import io.github.wasabithumb.xclaim.asset.AssetPath;
import io.github.wasabithumb.xclaim.asset.AssetSource;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.config.struct.Config;
import io.github.wasabithumb.xclaim.i18n.Lang;
import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.platform.PlatformTypeAdapter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

@ApiStatus.Internal
final class IntegrationLoader<T extends Integration> {

    private final Class<T> clazz;
    IntegrationLoader(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public @Nullable T load(@NotNull AssetManager assets) {
        final AssetSource resources = assets.resources();
        final AssetPath key = AssetPath.of("META-INF", "integrations", this.clazz.getName());
        T ret = null;

        try {
            if (!resources.exists(key)) return null;
            try (InputStream is = resources.read(key);
                 Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(r)
            ) {
                String line;
                T instance;
                while ((line = br.readLine()) != null) {
                    if (line.isEmpty()) continue;
                    instance = this.init(line);
                    if (instance == null) continue;
                    if (ret == null || instance.weight() > ret.weight()) ret = instance;
                }
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to read integrations metadata", e);
        }

        return ret;
    }

    private @Nullable T init(@NotNull String name) {
        Class<?> clazz;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Integration class \"" + name + "\" does not exist");
        } catch (ExceptionInInitializerError e) {
            Throwable t = e.getCause();
            if (t instanceof IntegrationException) return null;
            throw new AssertionError("Unexpected exception in initializer", e);
        } catch (LinkageError e) {
            return null;
        }

        if (!this.clazz.isAssignableFrom(clazz)) {
            throw new AssertionError("Integration class \"" + name + "\" does not subclass supertype \"" +
                    this.clazz.getName() + "\"");
        }

        Class<? extends T> qual = clazz.asSubclass(this.clazz);
        T ret;
        try {
            ret = qual.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Integration class \"" + name + "\" has no primary constructor");
        } catch (InvocationTargetException | ExceptionInInitializerError e) {
            Throwable t = e.getCause();
            if (t instanceof IntegrationException) return null;
            throw new AssertionError("Unexpected exception in constructor", e);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new AssertionError("Unexpected reflection error", e);
        } catch (LinkageError e) {
            return null;
        }

        return ret;
    }

    public void inject(@NotNull T instance, @NotNull XClaim runtime) {
        Class<?> cls = instance.getClass();
        for (Field f : cls.getDeclaredFields()) {
            if (f.getAnnotation(IntegrationInject.class) == null) continue;
            try {
                f.setAccessible(true);
            } catch (InaccessibleObjectException | SecurityException ignored) { }
            try {
                this.inject0(instance, f, runtime);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError("Failed to inject field \"" + f.getName() + "\"", e);
            }
        }
    }

    // Handle injection for a given field
    private void inject0(
            @NotNull T instance,
            @NotNull Field f,
            @NotNull XClaim runtime
    ) throws ReflectiveOperationException {
        Class<?> type = f.getType();
        if (type.isAssignableFrom(XClaim.class)) {
            f.set(instance, runtime);
            return;
        } else if (type.isAssignableFrom(XClaimBootstrap.class)) {
            f.set(instance, runtime.bootstrap());
            return;
        } else if (type.isAssignableFrom(Lang.class)) {
            f.set(instance, runtime.lang());
            return;
        } else if (type.isAssignableFrom(Platform.class)) {
            f.set(instance, runtime.platform());
            return;
        } else if (type.isAssignableFrom(PlatformTypeAdapter.class)) {
            f.set(instance, runtime.platform().adapter());
            return;
        } else if (type.isAssignableFrom(Logger.class)) {
            f.set(instance, runtime.logger());
            return;
        } else if (Config.class.isAssignableFrom(type) && inject00(instance, f, runtime)) {
            return;
        }
        throw new AssertionError("No rule to inject field of type: " + type.getName());
    }

    // Handle injection for a field that does not pass any other injection targets; meaning we should
    // try to fit a Config to it. If not, returns false.
    private boolean inject00(
            @NotNull T instance,
            @NotNull Field f,
            @NotNull XClaim runtime
    ) throws ReflectiveOperationException {
        Class<? extends Config> type = f.getType().asSubclass(Config.class);
        Queue<Config> queue = new LinkedList<>();
        queue.add(runtime.rootConfig());

        Config next;
        Class<? extends Config> nextType;
        while ((next = queue.poll()) != null) {
            nextType = next.getClass();
            if (type.isAssignableFrom(nextType)) {
                f.set(instance, next);
                return true;
            }

            for (Method m : nextType.getMethods()) {
                if (Modifier.isStatic(m.getModifiers())) continue;
                if (m.getParameterCount() != 0) continue;
                if (!Config.class.isAssignableFrom(m.getReturnType())) continue;
                queue.add((Config) m.invoke(instance));
            }
        }

        return false;
    }

}
