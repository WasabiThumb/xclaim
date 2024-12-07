package io.github.wasabithumb.xclaim.platform.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public record FoliaPlatformSchedulerReflection(
        Class<?> cScheduledTask,
        Method mScheduledTaskIsCancelled,
        Method mScheduledTaskCancel,
        Object oGlobalScheduler,
        Method mGlobalSchedulerRun,
        Method mGlobalSchedulerRunAtFixedRate,
        Object oAsyncScheduler,
        Method mAsyncSchedulerRunNow,
        Method mAsyncSchedulerRunAtFixedRate
) {

    public static @Nullable FoliaPlatformSchedulerReflection tryInit() {
        try {
            return init();
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new AssertionError("Failed to attempt access to Folia schedulers", e);
        }
        return null;
    }

    private static @Nullable FoliaPlatformSchedulerReflection init() throws ReflectiveOperationException {
        final Class<?> cScheduledTask = Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
        final Method mScheduledTaskIsCancelled = cScheduledTask.getMethod("isCancelled");
        final Method mScheduledTaskCancel = cScheduledTask.getMethod("cancel");

        final Class<?> cGlobalScheduler = Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
        final Method mBukkitGetGlobalScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler");
        if (!Modifier.isStatic(mBukkitGetGlobalScheduler.getModifiers())) return null;
        final Object oGlobalScheduler = mBukkitGetGlobalScheduler.invoke(null);
        if (!cGlobalScheduler.isInstance(oGlobalScheduler)) return null;

        final Method mGlobalSchedulerRun = cGlobalScheduler.getMethod("run", Plugin.class, Consumer.class);
        if (Modifier.isStatic(mGlobalSchedulerRun.getModifiers())) return null;
        final Method mGlobalSchedulerRunAtFixedRate = cGlobalScheduler.getMethod("runAtFixedRate", Plugin.class, Consumer.class, Long.TYPE, Long.TYPE);
        if (Modifier.isStatic(mGlobalSchedulerRunAtFixedRate.getModifiers())) return null;

        final Class<?> cAsyncScheduler = Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
        final Method mBukkitGetAsyncScheduler = Bukkit.class.getMethod("getAsyncScheduler");
        if (!Modifier.isStatic(mBukkitGetAsyncScheduler.getModifiers())) return null;
        final Object oAsyncScheduler = mBukkitGetAsyncScheduler.invoke(null);
        if (!cAsyncScheduler.isInstance(oAsyncScheduler)) return null;

        final Method mAsyncSchedulerRunNow = cAsyncScheduler.getMethod("runNow", Plugin.class, Consumer.class);
        if (Modifier.isStatic(mAsyncSchedulerRunNow.getModifiers())) return null;
        final Method mAsyncSchedulerRunAtFixedRate = cAsyncScheduler.getMethod("runAtFixedRate", Plugin.class, Consumer.class, Long.TYPE, Long.TYPE, TimeUnit.class);
        if (Modifier.isStatic(mAsyncSchedulerRunAtFixedRate.getModifiers())) return null;

        return new FoliaPlatformSchedulerReflection(
                cScheduledTask,
                mScheduledTaskIsCancelled,
                mScheduledTaskCancel,
                oGlobalScheduler,
                mGlobalSchedulerRun,
                mGlobalSchedulerRunAtFixedRate,
                oAsyncScheduler,
                mAsyncSchedulerRunNow,
                mAsyncSchedulerRunAtFixedRate
        );
    }

    //

    <R> @UnknownNullability R use(@NotNull UseFn<R> useFn) {
        try {
            return useFn.use(this);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new AssertionError("Unexpected reflect error when using Folia scheduler", e);
        }
    }

    //

    @FunctionalInterface
    public interface UseFn<R> {

        R use(@NotNull FoliaPlatformSchedulerReflection reflection) throws ReflectiveOperationException;

    }

}
