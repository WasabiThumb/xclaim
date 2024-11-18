package io.github.wasabithumb.xclaim.platform.scheduler;

public interface PlatformSchedulerTask {

    void cancel();

    boolean isCancelled();

}
