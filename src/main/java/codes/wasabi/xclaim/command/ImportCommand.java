package codes.wasabi.xclaim.command;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.command.argument.Argument;
import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.*;

import static codes.wasabi.xclaim.util.IntLongConverter.*;

public class ImportCommand implements Command {

    private static class UUIDPlane {

        private final Map<Long, UUID> backingMap = new HashMap<>();

        public void set(int x, int z, UUID uuid) {
            backingMap.put(intToLong(x, z), uuid);
        }

        public UUID get(int x, int z) {
            return backingMap.get(intToLong(x, z));
        }

        public List<int[]> pullClump(long curKey) {
            UUID uuid = backingMap.get(curKey);
            if (uuid == null) return Collections.emptyList();
            List<Long> queue = new ArrayList<>();
            List<int[]> ret = new ArrayList<>();
            queue.add(curKey);
            while (queue.size() > 0) {
                long l = queue.remove(0);
                int[] coords = longToInt(l);
                int _x = coords[0];
                int _z = coords[1];
                if (Objects.equals(get(_x, _z), uuid)) {
                    backingMap.remove(l);
                    ret.add(new int[]{ _x, _z });
                    long leftKey = intToLong(_x - 1, _z);
                    long rightKey = intToLong(_x + 1, _z);
                    long upKey = intToLong(_x, _z - 1);
                    long downKey = intToLong(_x, _z + 1);
                    if (!queue.contains(leftKey)) queue.add(leftKey);
                    if (!queue.contains(rightKey)) queue.add(rightKey);
                    if (!queue.contains(upKey)) queue.add(upKey);
                    if (!queue.contains(downKey)) queue.add(downKey);
                }
            }
            return ret;
        }

        public Map<UUID, List<List<int[]>>> pullClumps() {
            Map<UUID, List<List<int[]>>> clumps = new HashMap<>();
            while (backingMap.size() > 0) {
                long start = backingMap.keySet().stream().findFirst().get();
                UUID uuid = backingMap.get(start);
                List<int[]> clump = pullClump(start);
                List<List<int[]>> cs = clumps.get(uuid);
                if (cs == null) cs = new ArrayList<>();
                cs.add(clump);
                clumps.put(uuid, cs);
            }
            return clumps;
        }

    }

    public ImportCommand() { }

    @Override
    public @NotNull String getName() {
        return XClaim.lang.get("cmd-import-name");
    }

    @Override
    public @NotNull String getDescription() {
        return XClaim.lang.get("cmd-import-description");
    }

    @Override
    public @NotNull Argument @NotNull [] getArguments() {
        return new Argument[0];
    }

    @Override
    public @Range(from = 0, to = Integer.MAX_VALUE) int getNumRequiredArguments() {
        return 0;
    }

    @Override
    public boolean requiresPlayerExecutor() {
        return false;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Object @NotNull ... arguments) throws Exception {
        Audience audience = Platform.getAdventure().sender(sender);
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ClaimChunk");
        if (plugin != null) {
            if (!plugin.isEnabled()) {
                audience.sendMessage(XClaim.lang.getComponent("cmd-import-err-disabled"));
                return;
            }
            com.cjburkey.claimchunk.ClaimChunk qual = (com.cjburkey.claimchunk.ClaimChunk) plugin;
            audience.sendMessage(XClaim.lang.getComponent("cmd-import-status-handler"));
            com.cjburkey.claimchunk.data.newdata.IClaimChunkDataHandler dataHandler;
            try {
                Field field = qual.getClass().getDeclaredField("dataHandler");
                field.setAccessible(true);
                dataHandler = (com.cjburkey.claimchunk.data.newdata.IClaimChunkDataHandler) field.get(qual);
            } catch (Exception e) {
                audience.sendMessage(XClaim.lang.getComponent("cmd-import-err-reflect"));
                e.printStackTrace();
                return;
            }
            int importIndex = 1;
            for (World w : Bukkit.getWorlds()) {
                audience.sendMessage(XClaim.lang.getComponent("cmd-import-status-world", w.getName()));
                UUIDPlane plane = new UUIDPlane();
                com.cjburkey.claimchunk.chunk.ChunkPos pos;
                int x;
                int z;
                for (com.cjburkey.claimchunk.chunk.DataChunk chk : dataHandler.getClaimedChunks()) {
                    pos = chk.chunk;
                    if (chunkPosGetWorld(pos).equalsIgnoreCase(w.getName())) {
                        x = chunkPosGetX(pos);
                        z = chunkPosGetZ(pos);
                        audience.sendMessage(XClaim.lang.getComponent("cmd-import-status-chunk", x, z));
                        UUID ownerUUID = chk.player;
                        plane.set(x, z, ownerUUID);
                    }
                }
                audience.sendMessage(XClaim.lang.getComponent("cmd-import-status-fill"));
                Map<UUID, List<List<int[]>>> clumps = plane.pullClumps();
                for (Map.Entry<UUID, List<List<int[]>>> entry : clumps.entrySet()) {
                    UUID uuid = entry.getKey();
                    OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                    Component name;
                    Player online = op.getPlayer();
                    if (online != null) {
                        name = Platform.get().playerDisplayName(online);
                    } else {
                        String n = op.getName();
                        if (n == null) n = uuid.toString();
                        name = Component.text(n);
                    }
                    audience.sendMessage(XClaim.lang.getComponent("cmd-import-status-player", name));
                    for (List<int[]> cells : entry.getValue()) {
                        Set<Chunk> set = new HashSet<>();
                        for (int[] cell : cells) {
                            set.add(w.getChunkAt(cell[0], cell[1]));
                        }
                        Claim claim = new Claim("Imported Claim #" + importIndex, set, op);
                        claim.claim();
                        importIndex++;
                    }
                    audience.sendMessage(XClaim.lang.getComponent("cmd-import-status-success"));
                }
            }
            audience.sendMessage(XClaim.lang.getComponent("cmd-import-status-disabling"));
            qual.disable();
            audience.sendMessage(XClaim.lang.getComponent("cmd-import-status-done"));
        } else {
            audience.sendMessage(XClaim.lang.getComponent("cmd-import-err-installed"));
        }
    }

    // ClaimChunk broke API at some point by converting ChunkPos to a Record, now we need to check for both
    // "x" and "getX" methods

    private static Object[] CHUNK_POS_GET_WORLD;
    private static Object[] CHUNK_POS_GET_X;
    private static Object[] CHUNK_POS_GET_Z;
    static {
        Class<?> clazz = null;
        boolean found = false;
        try {
            clazz = Class.forName("com.cjburkey.claimchunk.chunk.ChunkPos");
            found = true;
        } catch (ClassNotFoundException e) {
            CHUNK_POS_GET_WORLD = CHUNK_POS_GET_X = CHUNK_POS_GET_Z = new Object[] { null, new AssertionError(e) };
        }

        if (found) {
            CHUNK_POS_GET_WORLD = new Object[] { null, null };
            findRecordMethod(clazz, "world", CHUNK_POS_GET_WORLD);

            CHUNK_POS_GET_X = new Object[] { null, null };
            findRecordMethod(clazz, "x", CHUNK_POS_GET_X);

            CHUNK_POS_GET_Z = new Object[] { null, null };
            findRecordMethod(clazz, "z", CHUNK_POS_GET_Z);
        }
    }

    private static void findRecordMethod(Class<?> clazz, String name, Object[] data) {
        Method m;
        try {
            m = clazz.getMethod(name);
        } catch (NoSuchMethodException e1) {
            final char[] chars = new char[name.length() + 3];
            CharBuffer.wrap(chars)
                    .put("get")
                    .put((char) (name.charAt(0) - 32))
                    .put(name, 1, name.length());
            try {
                m = clazz.getMethod(new String(chars));
            } catch (NoSuchMethodException e2) {
                e2.addSuppressed(e1);
                data[1] = new AssertionError(e2);
                return;
            }
        }
        data[0] = m;
    }


    private static Object unwrapRecordMethod(com.cjburkey.claimchunk.chunk.ChunkPos pos, Object[] data) {
        if (data[0] == null) throw ((AssertionError) data[1]);
        Method m = (Method) data[0];
        Object ret;
        try {
            ret = m.invoke(pos);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return ret;
    }

    private static String chunkPosGetWorld(com.cjburkey.claimchunk.chunk.ChunkPos pos) {
        return (String) unwrapRecordMethod(pos, CHUNK_POS_GET_WORLD);
    }

    private static int chunkPosGetX(com.cjburkey.claimchunk.chunk.ChunkPos pos) {
        return (Integer) unwrapRecordMethod(pos, CHUNK_POS_GET_X);
    }

    private static int chunkPosGetZ(com.cjburkey.claimchunk.chunk.ChunkPos pos) {
        return (Integer) unwrapRecordMethod(pos, CHUNK_POS_GET_Z);
    }

}
