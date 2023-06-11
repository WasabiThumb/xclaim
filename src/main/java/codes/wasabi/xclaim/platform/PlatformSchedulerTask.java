package codes.wasabi.xclaim.platform;

public interface PlatformSchedulerTask {

    void cancel();

    boolean isCancelled();

}
