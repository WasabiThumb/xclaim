package codes.wasabi.xclaim.platform.folia_1_19;

import codes.wasabi.xclaim.platform.paper_1_17.PaperPlatform;
import org.bukkit.Bukkit;

public class FoliaPlatform extends PaperPlatform {

    private FoliaPlatformScheduler scheduler;
    private boolean schedulerInit = false;
    @Override
    public FoliaPlatformScheduler getScheduler() {
        if (!schedulerInit) {
            scheduler = new FoliaPlatformScheduler(Bukkit.getAsyncScheduler(), Bukkit.getGlobalRegionScheduler());
            schedulerInit = true;
        }
        return scheduler;
    }

    @Override
    public boolean hasFoliaScheduler() {
        return true;
    }

}
