package codes.wasabi.xclaim.api.dynmap.outline;

import codes.wasabi.xclaim.platform.Platform;
import org.bukkit.Chunk;
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

    public ChunkBitmap(@NotNull Collection<Chunk> chunks) {
        setChunks(chunks);
    }

    public ChunkBitmap(@NotNull Chunk @NotNull ... chunks) {
        setChunks(Arrays.asList(chunks));
    }

    public void setChunks(@NotNull Collection<Chunk> chunks) {
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
            for (Chunk c : chunks) {
                world = c.getWorld();
                xMin = Math.min(xMin, c.getX());
                xMax = Math.max(xMax, c.getX());
                zMin = Math.min(zMin, c.getZ());
                zMax = Math.max(zMax, c.getZ());
            }
            Chunk originChunk = world.getChunkAt(xMin, zMin);
            Block originBlock = originChunk.getBlock(0, Platform.get().getWorldMinHeight(world), 0);
            originX = originBlock.getX();
            originZ = originBlock.getZ();
            width = xMax - xMin + 1;
            height = zMax - zMin + 1;
            for (Chunk c : chunks) {
                indices.add(intToLong(c.getX() - xMin, c.getZ() - zMin));
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

    public List<Point> traceBlocks() {
        List<Point> points = Bitmap.super.trace();
        return points.stream().map((Point p) -> p.product(16).sum(originX, originZ)).collect(Collectors.toCollection(ArrayList::new));
    }

}
