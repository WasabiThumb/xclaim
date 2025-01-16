package io.github.wasabithumb.xclaim.claim.enforcer.impl;

import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.claim.enforcer.ClaimEnforcer;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerJoinEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerMoveEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerQuitEvent;
import io.github.wasabithumb.xclaim.platform.scheduler.PlatformSchedulerTask;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ApiStatus.Internal
public final class EnterClaimEnforcer extends ClaimEnforcer {

    private final Map<UUID, PlatformSchedulerTask> occluding = Collections.synchronizedMap(new HashMap<>());

    public EnterClaimEnforcer(@NotNull ClaimManager manager) {
        super(manager);
    }

    @Override
    protected @NotNull Permission permission() {
        return Permission.ENTER;
    }

    @Override
    public void register() {
        synchronized (this.occluding) {
            this.occluding.clear();
            this.scan();
        }
    }

    @Override
    public void unregister() {
        synchronized (this.occluding) {
            for (PlatformSchedulerTask task : this.occluding.values())
                task.cancel();
            this.occluding.clear();
        }
    }

    //

    @PlatformEventHandler
    public void onMove(@NotNull PlatformPlayerMoveEvent event) {
        PlatformPlayer ply = event.player();
        PlatformLocation from = event.getFrom();
        PlatformLocation to = event.getTo();
        if (from == null || to == null) return;

        ChunkReference fromChunk = ChunkReference.of(from);
        boolean fromDenied = false;
        boolean toDenied;

        Claim fromClaim = this.claimAt(fromChunk);
        if (fromClaim != null && !fromClaim.checkPermission(ply, Permission.ENTER)) {
            fromDenied = true;
        }

        if (fromChunk.matches(to.chunk())) {
            toDenied = fromDenied;
        } else {
            Claim toClaim = this.claimAt(to);
            toDenied = toClaim != null && !toClaim.checkPermission(ply, Permission.ENTER);
        }
        if (!toDenied) return;

        if (fromDenied) {
            this.update(ply, true);
        } else {
            event.setCancelled(true);
            ply.sendMessage(this.lang().get("permHandler-stdError"));
        }
    }

    @PlatformEventHandler
    public void onJoin(@NotNull PlatformPlayerJoinEvent event) {
        this.scanSingle(event.player());
    }

    @PlatformEventHandler
    public void onLeave(@NotNull PlatformPlayerQuitEvent event) {
        this.update(event.player(), false);
    }

    //
    private void scan() {
        for (PlatformPlayer ply : this.platform().users().players()) {
            this.scanSingle(ply);
        }
    }

    private void scanSingle(@NotNull PlatformPlayer ply) {
        Claim claim = this.claimAt(ply.location());
        if (claim == null) return;
        this.update(ply, this.isNotPermitted(claim, ply));
    }

    private void update(@NotNull PlatformPlayer ply, boolean occluding) {
        UUID uuid = ply.uuid();
        if (occluding) {
            synchronized (this.occluding) {
                if (this.occluding.containsKey(uuid)) return;
                PlatformSchedulerTask task = this.platform().scheduler().runTaskTimer(
                        new OccludingTask(ply),
                        0L,
                        1L
                );
                this.occluding.put(uuid, task);
            }
        } else {
            PlatformSchedulerTask task = this.occluding.remove(uuid);
            if (task != null) task.cancel();
        }
    }

    //

    private static final class OccludingTask implements Runnable {

        private static final double PUSH_POWER = 0.2d;

        private final PlatformPlayer ply;
        private int counter = 0;

        OccludingTask(@NotNull PlatformPlayer ply) {
            this.ply = ply;
        }

        @Override
        public void run() {
            this.doDamage();
            this.doPush();
        }

        private void doDamage() {
            if ((++this.counter) == 10) {
                this.ply.damage(2d);
                this.counter = 0;
            }
        }

        private void doPush() {
            PlatformLocation loc = this.ply.location();
            double x = loc.x();
            double z = loc.z();

            int chunkCenterX = (loc.blockX() & -16) | 8;
            int chunkCenterZ = (loc.blockZ() & -16) | 8;

            double dx = x - chunkCenterX;
            double dz = z - chunkCenterZ;

            double mag = Math.sqrt((dx * dx) + (dz * dz));
            double factor = PUSH_POWER;
            if (Math.abs(mag) < 1e-6d) {
                // Player is at dead center
                dx = 1d;
                dz = 0d;
            } else {
                factor /= mag;
            }

            this.ply.addVelocity(dx * factor, 0, dz * factor);
        }

    }

}
