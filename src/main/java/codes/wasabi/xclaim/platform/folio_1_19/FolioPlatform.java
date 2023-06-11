package codes.wasabi.xclaim.platform.folio_1_19;

import codes.wasabi.xclaim.platform.paper_1_17.PaperPlatform;
import org.bukkit.Bukkit;

public class FolioPlatform extends PaperPlatform {

    private FolioPlatformScheduler scheduler;
    private boolean schedulerInit = false;
    @Override
    public FolioPlatformScheduler getScheduler() {
        if (!schedulerInit) {
            scheduler = new FolioPlatformScheduler(Bukkit.getAsyncScheduler(), Bukkit.getGlobalRegionScheduler());
            schedulerInit = true;
        }
        return scheduler;
    }

}
