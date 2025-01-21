package io.github.wasabithumb.xclaim.routine.impl;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.struct.Permission;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventCategory;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.PlatformListener;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerJoinEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerMoveEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.PlatformPlayerQuitEvent;
import io.github.wasabithumb.xclaim.platform.scheduler.task.PlatformSchedulerTask;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.routine.Routine;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import io.github.wasabithumb.xclaim.util.ColorTag;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class MoveRoutine extends Routine implements PlatformListener {

    private final Map<UUID, PlatformSchedulerTask> barrierTask = Collections.synchronizedMap(new HashMap<>());

    public MoveRoutine(@NotNull XClaim runtime) {
        super(runtime);
    }

    //

    @PlatformEventHandler(category = PlatformEventCategory.MONITOR)
    public void onJoin(@NotNull PlatformPlayerJoinEvent event) {
        PlatformPlayer ply = event.player();
        this.updateBarriers(ply, ChunkReference.of(ply.location().chunk()));
    }

    @PlatformEventHandler(category = PlatformEventCategory.MONITOR)
    public void onQuit(@NotNull PlatformPlayerQuitEvent event) {
        PlatformSchedulerTask task = this.barrierTask.remove(event.player().uuid());
        if (task == null) return;
        if (task.isCancelled()) return;
        task.cancel();
    }

    @PlatformEventHandler(category = PlatformEventCategory.MONITOR)
    public void onMove(@NotNull PlatformPlayerMoveEvent event) {
        if (event.isCancelled()) return;
        PlatformPlayer player = event.player();
        PlatformLocation from = event.getFrom();
        PlatformLocation to = event.getTo();
        if (from == null || to == null) return;

        PlatformChunk fromChunk = from.chunk();
        PlatformChunk toChunk = to.chunk();
        ChunkReference toChunkRef = ChunkReference.of(toChunk);
        if (toChunkRef.matches(fromChunk)) return;
        this.updateBarriers(player, toChunkRef);

        Claim fromClaim = this.runtime.claims().getByChunk(fromChunk);
        Claim toClaim = this.runtime.claims().getByChunk(toChunk);

        if (toClaim == null) {
            if (fromClaim != null) {
                // Left claim
                player.sendActionBar(this.runtime.lang("move-exit", fromClaim.name()));
            }
        } else if (fromClaim == null || !fromClaim.equals(toClaim)) {
            // Entered claim
            player.sendActionBar(this.runtime.lang(
                    "move-enter",
                    toClaim.owner().displayName(),
                    toClaim.name()
            ));
        }
    }

    //

    private void updateBarriers(@NotNull PlatformPlayer player, @NotNull ChunkReference center) {
        int flags = 0;
        int flag = 1;

        for (int dz=-1; dz <= 1; dz++) {
            for (int dx=-1; dx <= 1; dx++) {
                if (dx == 0 && dz == 0) continue;
                int f = flag;
                flag <<= 1;

                Claim claim = this.runtime.claims().getByChunk(center.getRelative(dx, dz));
                if (claim == null) continue;
                if (claim.checkPermission(player, Permission.ENTER)) continue;
                flags |= f;
            }
        }

        PlatformSchedulerTask existing = this.barrierTask.remove(player.uuid());
        if (existing != null && !existing.isCancelled()) existing.cancel();
        if (flags == 0) return;

        BarrierRenderer renderer = new BarrierRenderer(player, (byte) flags);
        PlatformSchedulerTask task = this.platform().scheduler().newTask()
                .executor(renderer)
                .targetEntity(player)
                .periodTicks(2)
                .build();

        this.barrierTask.put(player.uuid(), task);
    }

    //

    private record BarrierRenderer(
            @NotNull PlatformPlayer player,
            byte flags
    ) implements Runnable {

        // Generated with Python
        private static final long[] LINES = new long[] {
                0x0000000000000000L, 0x0000000000004515L, 0x0000000000894559L, 0x0000000000008919L, 0x0000000000009d89L,
                0x000000009d894515L, 0x000000000000455dL, 0x000000000000001dL, 0x0000000000152656L, 0x0000000000002646L,
                0x0000000026894619L, 0x0000000026568959L, 0x0000001526569d89L, 0x00000000269d8946L, 0x000000000026461dL,
                0x000000000026565dL, 0x00000000009dae9aL, 0x0000009dae9a4515L, 0x00000000ae8a455dL, 0x0000000000ae8a1dL,
                0x000000000000ae8aL, 0x00000000ae8a4515L, 0x00000000ae9a4559L, 0x0000000000ae9a19L, 0x00009dae9a152656L,
                0x0000009dae9a2646L, 0x000000ae268a461dL, 0x000000ae26568a5dL, 0x000000ae1526568aL, 0x00000000ae268a46L,
                0x000000ae9a264619L, 0x000000ae9a265659L, 0x0000000000006726L, 0x0000000067264515L, 0x0000006726894559L,
                0x0000000067268919L, 0x0000000067269d89L, 0x000067269d894515L, 0x000000006726455dL, 0x000000000067261dL,
                0x0000000000001557L, 0x0000000000006746L, 0x0000000067894619L, 0x0000000000578959L, 0x0000000015579d89L,
                0x00000000679d8946L, 0x000000000067461dL, 0x000000000000575dL, 0x0000009dae9a6726L, 0x009dae9a67264515L,
                0x0000ae67268a455dL, 0x000000ae67268a1dL, 0x00000000ae67268aL, 0x0000ae67268a4515L, 0x0000ae9a67264559L,
                0x000000ae9a672619L, 0x0000009dae9a1557L, 0x0000009dae9a6746L, 0x000000ae678a461dL, 0x00000000ae578a5dL,
                0x00000000ae15578aL, 0x00000000ae678a46L, 0x000000ae9a674619L, 0x00000000ae9a5759L, 0x0000000000ab676aL,
                0x000000ab676a4515L, 0x0000ab676a894559L, 0x000000ab676a8919L, 0x000000ab676a9d89L, 0x00ab676a9d894515L,
                0x000000ab676a455dL, 0x00000000ab676a1dL, 0x0000000015ab572aL, 0x00000000ab672a46L, 0x0000ab672a894619L,
                0x000000ab572a8959L, 0x000015ab572a9d89L, 0x0000ab672a9d8946L, 0x000000ab672a461dL, 0x00000000ab572a5dL,
                0x000000009d9b676eL, 0x00009d9b676e4515L, 0x0000ab676e8a455dL, 0x000000ab676e8a1dL, 0x00000000ab676e8aL,
                0x0000ab676e8a4515L, 0x0000009b676e4559L, 0x000000009b676e19L, 0x0000009d159b572eL, 0x0000009d9b672e46L,
                0x0000ab672e8a461dL, 0x000000ab572e8a5dL, 0x00000015ab572e8aL, 0x000000ab672e8a46L, 0x0000009b672e4619L,
                0x000000009b572e59L, 0x000000000000ab2aL, 0x00000000ab2a4515L, 0x000000ab2a894559L, 0x00000000ab2a8919L,
                0x00000000ab2a9d89L, 0x0000ab2a9d894515L, 0x00000000ab2a455dL, 0x0000000000ab2a1dL, 0x000000001556ab6aL,
                0x0000000000ab6a46L, 0x000000ab6a894619L, 0x00000056ab6a8959L, 0x00001556ab6a9d89L, 0x000000ab6a9d8946L,
                0x00000000ab6a461dL, 0x0000000056ab6a5dL, 0x00000000009d9b2eL, 0x0000009d9b2e4515L, 0x000000ab2e8a455dL,
                0x00000000ab2e8a1dL, 0x0000000000ab2e8aL, 0x000000ab2e8a4515L, 0x000000009b2e4559L, 0x00000000009b2e19L,
                0x0000009d15569b6eL, 0x000000009d9b6e46L, 0x000000ab6e8a461dL, 0x00000056ab6e8a5dL, 0x0000001556ab6e8aL,
                0x00000000ab6e8a46L, 0x000000009b6e4619L, 0x00000000569b6e59L, 0x000000000000aeabL, 0x00000000aeab4515L,
                0x000000aeab894559L, 0x00000000aeab8919L, 0x00000000aeab9d89L, 0x0000aeab9d894515L, 0x00000000aeab455dL,
                0x0000000000aeab1dL, 0x000000152656aeabL, 0x0000000026aeab46L, 0x000026aeab894619L, 0x00002656aeab8959L,
                0x00152656aeab9d89L, 0x000026aeab9d8946L, 0x00000026aeab461dL, 0x0000002656aeab5dL, 0x0000000000009d9bL,
                0x000000009d9b4515L, 0x00000000ab8a455dL, 0x0000000000ab8a1dL, 0x000000000000ab8aL, 0x00000000ab8a4515L,
                0x00000000009b4559L, 0x0000000000009b19L, 0x0000009d1526569bL, 0x000000009d269b46L, 0x00000026ab8a461dL,
                0x0000002656ab8a5dL, 0x000000152656ab8aL, 0x0000000026ab8a46L, 0x00000000269b4619L, 0x0000000026569b59L,
                0x00000000aeab6726L, 0x0000aeab67264515L, 0x00aeab6726894559L, 0x0000aeab67268919L, 0x0000aeab67269d89L,
                0xaeab67269d894515L, 0x0000aeab6726455dL, 0x000000aeab67261dL, 0x0000000015aeab57L, 0x00000000aeab6746L,
                0x0000aeab67894619L, 0x000000aeab578959L, 0x000015aeab579d89L, 0x0000aeab679d8946L, 0x000000aeab67461dL,
                0x00000000aeab575dL, 0x000000009d9b6726L, 0x00009d9b67264515L, 0x0000ab67268a455dL, 0x000000ab67268a1dL,
                0x00000000ab67268aL, 0x0000ab67268a4515L, 0x0000009b67264559L, 0x000000009b672619L, 0x000000009d159b57L,
                0x000000009d9b6746L, 0x000000ab678a461dL, 0x00000000ab578a5dL, 0x0000000015ab578aL, 0x00000000ab678a46L,
                0x000000009b674619L, 0x00000000009b5759L, 0x000000000000676eL, 0x00000000676e4515L, 0x000000676e894559L,
                0x00000000676e8919L, 0x00000000676e9d89L, 0x0000676e9d894515L, 0x00000000676e455dL, 0x0000000000676e1dL,
                0x000000000015572eL, 0x0000000000672e46L, 0x000000672e894619L, 0x00000000572e8959L, 0x00000015572e9d89L,
                0x000000672e9d8946L, 0x00000000672e461dL, 0x0000000000572e5dL, 0x000000009d9a676aL, 0x00009d9a676a4515L,
                0x000000676a8a455dL, 0x00000000676a8a1dL, 0x0000000000676a8aL, 0x000000676a8a4515L, 0x0000009a676a4559L,
                0x000000009a676a19L, 0x0000009d9a15572aL, 0x0000009d9a672a46L, 0x000000672a8a461dL, 0x00000000572a8a5dL,
                0x0000000015572a8aL, 0x00000000672a8a46L, 0x0000009a672a4619L, 0x000000009a572a59L, 0x000000000000002eL,
                0x00000000002e4515L, 0x000000002e894559L, 0x00000000002e8919L, 0x00000000002e9d89L, 0x0000002e9d894515L,
                0x00000000002e455dL, 0x0000000000002e1dL, 0x000000000015566eL, 0x0000000000006e46L, 0x000000006e894619L,
                0x00000000566e8959L, 0x00000015566e9d89L, 0x000000006e9d8946L, 0x00000000006e461dL, 0x0000000000566e5dL,
                0x00000000009d9a2aL, 0x0000009d9a2a4515L, 0x000000002a8a455dL, 0x00000000002a8a1dL, 0x0000000000002a8aL,
                0x000000002a8a4515L, 0x000000009a2a4559L, 0x00000000009a2a19L, 0x0000009d9a15566aL, 0x000000009d9a6a46L,
                0x000000006a8a461dL, 0x00000000566a8a5dL, 0x0000000015566a8aL, 0x00000000006a8a46L, 0x000000009a6a4619L,
                0x000000009a566a59L
        };

        private static final int[] OFFSETS = new int[] { -16, 0, 16, 32 };

        private static final double MPD = 0.6d;

        @Override
        public void run() {
            PlatformLocation loc = this.player.location();
            int cx = loc.blockX() & -16;
            int cz = loc.blockZ() & -16;

            long lineData = LINES[Byte.toUnsignedInt(this.flags)];
            long tmp;
            for (int i=0; i < 8; i++) {
                tmp = lineData & 0xFF;
                if (tmp == 0L) break;
                this.renderLine(cx, cz, (int) tmp);
                lineData >>>= 8;
            }
        }

        private void renderLine(int cx, int cz, int data) {
            int x1 = cx + OFFSETS[data >> 6];
            int z1 = cz + OFFSETS[(data >> 4) & 3];
            int x2 = cx + OFFSETS[(data >> 2) & 3];
            int z2 = cz + OFFSETS[data & 3];
            this.renderLine(x1, z1, x2, z2);
        }

        private void renderLine(int x1, int z1, int x2, int z2) {
            PlatformLocation loc = this.player.location();
            double ux1, uz1, ux2, uz2;

            if (x1 == x2) {
                double rad = this.cRad(loc.x() - x1);
                if (rad < 0.01) return;
                ux1 = ux2 = x1;
                uz1 = loc.z() - rad;
                uz2 = loc.z() + rad;
                if (uz2 < z1) return;
                if (uz1 < z1) uz1 = z1;
                if (uz1 > z2) return;
                if (uz2 > z2) uz2 = z2;
            } else {
                double rad = this.cRad(loc.z() - z1);
                if (rad < 0.01) return;
                ux1 = loc.x() - rad;
                ux2 = loc.x() + rad;
                uz1 = uz2 = z1;
                if (ux2 < x1) return;
                if (ux1 < x1) ux1 = x1;
                if (ux1 > x2) return;
                if (ux2 > x2) ux2 = x2;
            }

            Random random = ThreadLocalRandom.current();
            double dx = ux2 - ux1;
            double dz = uz2 - uz1;
            double mag = Math.sqrt((dx * dx) + (dz * dz));
            dx /= mag;
            dz /= mag;

            double n = 0d;
            do {
                double r = random.nextDouble();
                double qx = ux1 + (dx * (n + MPD * r));
                double qz = uz1 + (dz * (n + MPD * r));

                for (int v=0; v <= 7; v++) {
                    double vd = ((double) v) / 7d;
                    double y = loc.y() + (5d * vd) - 1d + (random.nextDouble() * 0.6 - 0.3);
                    this.player.sendRedstoneParticle(ColorTag.RED.rgb(), qx, y, qz);
                }

                n += MPD;
            } while (n <= mag);
        }

        private double cRad(double dist) {
            if (dist > 8d || dist < -8d) return 0d;
            return Math.sqrt(64d - (dist * dist));
        }

    }

}
