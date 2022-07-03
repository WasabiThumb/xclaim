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
                List<List<int[]>> cs = Objects.requireNonNullElseGet(clumps.get(uuid), ArrayList::new);
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
                for (com.cjburkey.claimchunk.chunk.DataChunk chk : dataHandler.getClaimedChunks()) {
                    com.cjburkey.claimchunk.chunk.ChunkPos pos = chk.chunk;
                    if (pos.getWorld().equalsIgnoreCase(w.getName())) {
                        audience.sendMessage(XClaim.lang.getComponent("cmd-import-status-chunk", pos.getX(), pos.getZ()));
                        UUID ownerUUID = chk.player;
                        plane.set(pos.getX(), pos.getZ(), ownerUUID);
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

}
