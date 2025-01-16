package io.github.wasabithumb.xclaim.platform.scheduler.task;

public interface PlatformSchedulerTask {

    void cancel();

    boolean isCancelled();

}
