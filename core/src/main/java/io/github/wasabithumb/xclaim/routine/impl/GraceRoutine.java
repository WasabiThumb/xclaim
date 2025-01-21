package io.github.wasabithumb.xclaim.routine.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.config.sub.WorldsConfig;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.platform.data.PlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.data.PlatformPersistentDataType;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventCategory;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.PlatformListener;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerJoinEvent;
import io.github.wasabithumb.xclaim.platform.scheduler.PlatformSchedulerDuration;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorldManager;
import io.github.wasabithumb.xclaim.routine.Routine;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public final class GraceRoutine extends Routine implements PlatformListener {

    private static final String START_KEY = "grace_start";

    public GraceRoutine(@NotNull XClaim runtime) {
        super(runtime);
    }

    //

    @Override
    protected void onTick() {
        PlatformWorldManager worlds = this.runtime.platform().worlds();
        WorldsConfig cfg = this.runtime.rootConfig().worlds();
        long time = cfg.graceTime() * 1000L;

        for (PlatformWorld world : worlds) {
            if (cfg.checkLists(world)) continue;
            if (this.worldIsSafe(world, time)) continue;

            for (Claim claim : this.runtime.claims().getByWorld(world))
                this.deleteClaim(claim);
        }
    }

    private void deleteClaim(@NotNull Claim claim) {
        boolean success = claim.modifyChunks(null)
                .silent(true)
                .clear()
                .commit()
                .isSuccess();

        if (!success) {
            this.runtime.logger().log(
                    Level.WARNING,
                    "Attempt to auto-delete claim \"" + claim.name() + "\" as CONSOLE was unsuccessful"
            );
            return;
        }

        final PlatformUser owner = claim.owner();
        final String message = this.runtime.lang(I18N.GRACE_REMOVE, claim.name());
        owner.sendMessage(message);
    }

    private boolean worldIsSafe(@NotNull PlatformWorld world, long time) {
        if (!world.isValid()) return false;

        PlatformPersistentDataContainer pdc = world.pdc();
        long now = System.currentTimeMillis();
        long start;

        if (pdc.has(START_KEY, PlatformPersistentDataType.LONG)) {
            start = pdc.get(START_KEY, PlatformPersistentDataType.LONG);
        } else if (time > 0L) {
            pdc.set(START_KEY, PlatformPersistentDataType.LONG, now);
            return true;
        } else {
            return false;
        }

        long elapsed = now - start;
        if (elapsed >= time) {
            pdc.remove(START_KEY);
            return false;
        }

        return true;
    }

    //

    @PlatformEventHandler(category = PlatformEventCategory.MONITOR)
    public void onJoin(@NotNull PlatformPlayerJoinEvent event) {
        PlatformPlayer ply = event.player();
        WorldsConfig cfg = this.runtime.rootConfig().worlds();
        int count = 0;

        for (Claim claim : this.runtime.claims().getByOwner(ply)) {
            PlatformWorld world = claim.world();
            if (world == null) continue;
            if (!cfg.checkLists(world)) {
                count++;
            }
        }

        if (count != 0)
            ply.sendMessage(this.runtime.lang(I18N.GRACE_ALERT, count));
    }

    //

    @Override
    protected boolean shouldTick() {
        WorldsConfig cfg = this.runtime.rootConfig().worlds();
        return cfg.useWhitelist() || (cfg.useBlacklist() && !cfg.blacklist().isEmpty());
    }

    @Override
    protected @NotNull PlatformSchedulerDuration period() {
        WorldsConfig cfg = this.runtime.rootConfig().worlds();
        return PlatformSchedulerDuration.seconds(Math.max(cfg.graceTime() / 8d, 10));
    }

}
