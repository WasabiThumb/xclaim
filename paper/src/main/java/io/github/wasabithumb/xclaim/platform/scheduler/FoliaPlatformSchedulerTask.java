package io.github.wasabithumb.xclaim.platform.scheduler;

import org.jetbrains.annotations.NotNull;

public class FoliaPlatformSchedulerTask implements BukkitPlatformSchedulerTask {

    private final Object handle;
    private final FoliaPlatformSchedulerReflection reflection;

    FoliaPlatformSchedulerTask(
            @NotNull Object handle,
            @NotNull FoliaPlatformSchedulerReflection reflection
    ) {
        this.handle = handle;
        this.reflection = reflection;
        if (!reflection.cScheduledTask().isInstance(handle))
            throw new IllegalArgumentException("Handle is not of type ScheduledTask (got " + handle.getClass() + ")");
    }


    @Override
    public void cancel() {
        this.reflection.use((r) -> {
            r.mScheduledTaskCancel().invoke(this.handle);
            return null;
        });
    }

    @Override
    public boolean isCancelled() {
        return this.reflection.use((r) ->
                (Boolean) r.mScheduledTaskIsCancelled().invoke(this.handle)
        );
    }

}
