package codes.wasabi.xclaim.platform.folio_1_19;

import codes.wasabi.xclaim.platform.PlatformSchedulerTask;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class FolioPlatformSchedulerTask implements PlatformSchedulerTask {

    private final ScheduledTask task;
    public FolioPlatformSchedulerTask(ScheduledTask task) {
        this.task = task;
    }

    public ScheduledTask getHandle() {
        return this.task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return this.task.isCancelled();
    }

}
