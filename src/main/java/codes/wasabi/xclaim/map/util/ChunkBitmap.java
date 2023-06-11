package codes.wasabi.xclaim.map.util;

import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.ChunkReference;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static codes.wasabi.xclaim.util.IntLongConverter.intToLong;

public class ChunkBitmap implements Bitmap {

    private int originX;
    private int originZ;
    private int width;
    private int height;
    private World world;
    private final Set<Long> indices = new HashSet<>();
    public ChunkBitmap() {
        originX = 0;
        originZ = 0;
        width = 0;
        height = 0;
        world = null;
    }

    public ChunkBitmap(@NotNull Collection<ChunkReference> chunks) {
        setChunks(chunks);
    }

    public ChunkBitmap(@NotNull ChunkReference @NotNull ... chunks) {
        setChunks(Arrays.asList(chunks));
    }

    public void setChunks(@NotNull Collection<ChunkReference> chunks) {
        indices.clear();
        if (chunks.size() == 0) {
            originX = 0;
            originZ = 0;
            width = 0;
            height = 0;
            world = null;
        } else {
            int xMin = Integer.MAX_VALUE;
            int zMin = Integer.MAX_VALUE;
            int xMax = Integer.MIN_VALUE;
            int zMax = Integer.MIN_VALUE;
            for (ChunkReference c : chunks) {
                world = c.world;
                xMin = Math.min(xMin, c.x);
                xMax = Math.max(xMax, c.x);
                zMin = Math.min(zMin, c.z);
                zMax = Math.max(zMax, c.z);
            }
            ChunkReference originChunk = new ChunkReference(world, xMin, zMin);
            Location originBlock = originChunk.getLocation(0, Platform.get().getWorldMinHeight(world), 0);
            originX = originBlock.getBlockX();
            originZ = originBlock.getBlockZ();
            width = xMax - xMin + 1;
            height = zMax - zMin + 1;
            for (ChunkReference c : chunks) {
                indices.add(intToLong(c.x - xMin, c.z - zMin));
            }
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean getPixel(int x, int y) {
        if (x < 0 || y < 0) return false;
        if (x >= width) return false;
        if (y >= height) return false;
        long idx = intToLong(x, y);
        return indices.contains(idx);
    }

    private List<Point> transformPoints(List<Point> points) {
        return points.stream().map((Point p) -> p.product(16).sum(originX, originZ)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Point> traceBlocks() {
        return transformPoints(Bitmap.super.trace());
    }

    public List<List<Point>> traceBlocks(boolean includeAll) {
        return Bitmap.super.trace(includeAll)
                .stream().map(this::transformPoints).collect(Collectors.toList());
    }

}
