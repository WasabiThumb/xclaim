package io.github.wasabithumb.xclaim.util.tracer;

import io.github.wasabithumb.xclaim.platform.world.PlatformLocation;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import io.github.wasabithumb.xclaim.util.ChunkReference;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.wasabithumb.xclaim.util.BitManipulation.i32i64;

public class ChunkBitmap implements Bitmap {

    private int originX;
    private int originZ;
    private int width;
    private int height;
    private PlatformWorld world;
    private Set<Long> indices;

    public ChunkBitmap() {
        clear();
    }

    public ChunkBitmap(@NotNull Collection<ChunkReference> chunks) {
        setChunks(chunks);
    }

    public ChunkBitmap(@NotNull ChunkReference @NotNull ... chunks) {
        setChunks(Arrays.asList(chunks));
    }

    //

    public void clear() {
        this.originX = 0;
        this.originZ = 0;
        this.width = 0;
        this.height = 0;
        this.world = null;
        this.indices = new HashSet<>();
    }

    public void setChunks(@NotNull Collection<ChunkReference> chunks) {
        final int size = chunks.size();
        if (size == 0) {
            clear();
        } else {
            this.indices = new HashSet<>(size);

            int xMin = Integer.MAX_VALUE;
            int zMin = Integer.MAX_VALUE;
            int xMax = Integer.MIN_VALUE;
            int zMax = Integer.MIN_VALUE;
            for (ChunkReference c : chunks) {
                this.world = c.world;
                xMin = Math.min(xMin, c.x);
                xMax = Math.max(xMax, c.x);
                zMin = Math.min(zMin, c.z);
                zMax = Math.max(zMax, c.z);
            }

            ChunkReference originChunk = new ChunkReference(this.world, xMin, zMin);
            PlatformLocation originBlock = originChunk.getLocation(0, this.world.getMinHeight(), 0);
            this.originX = originBlock.blockX();
            this.originZ = originBlock.blockZ();
            this.width = xMax - xMin + 1;
            this.height = zMax - zMin + 1;

            for (ChunkReference c : chunks) {
                this.indices.add(i32i64(c.x - xMin, c.z - zMin));
            }
        }
    }

    //

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean getPixel(int x, int y) {
        if (x < 0 || y < 0) return false;
        if (x >= this.width) return false;
        if (y >= this.height) return false;
        long idx = i32i64(x, y);
        return this.indices.contains(idx);
    }

    private @NotNull List<Point> transformPoints(@NotNull List<Point> points) {
        final int size = points.size();
        Point[] transformed = new Point[size];
        for (int i=0; i < size; i++) {
            transformed[i] = points.get(i)
                    .product(16)
                    .sum(this.originX, this.originZ);
        }
        return Arrays.asList(transformed);
    }

    public @NotNull List<Point> traceBlocks() {
        return transformPoints(Bitmap.super.trace());
    }

    public @NotNull List<List<Point>> traceBlocks(boolean includeAll) {
        return Bitmap.super.trace(includeAll)
                .stream().map(this::transformPoints).collect(Collectors.toList());
    }

}
