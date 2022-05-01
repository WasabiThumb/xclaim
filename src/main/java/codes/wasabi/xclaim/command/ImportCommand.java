package codes.wasabi.xclaim.command;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.command.argument.Argument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import java.nio.ByteBuffer;
import java.util.*;

public class ImportCommand implements Command {

    private static class UUIDPlane {

        private final Map<Long, UUID> backingMap = new HashMap<>();

        public long coordsToKey(int x, int z) {
            return ByteBuffer.allocate(Long.BYTES).putInt(x).putInt(z).position(0).getLong();
        }

        public int[] keyToCoords(long key) {
            ByteBuffer bb = ByteBuffer.allocate(Long.BYTES).putLong(key).position(0);
            return new int[]{ bb.getInt(), bb.getInt() };
        }

        public void set(int x, int z, UUID uuid) {
            backingMap.put(coordsToKey(x, z), uuid);
        }

        public UUID get(int x, int z) {
            return backingMap.get(coordsToKey(x, z));
        }

        public List<int[]> pullClump(long curKey) {
            UUID uuid = backingMap.get(curKey);
            if (uuid == null) return Collections.emptyList();
            List<Long> queue = new ArrayList<>();
            List<int[]> ret = new ArrayList<>();
            queue.add(curKey);
            while (queue.size() > 0) {
                long l = queue.remove(0);
                int[] coords = keyToCoords(l);
                int _x = coords[0];
                int _z = coords[1];
                if (Objects.equals(get(_x, _z), uuid)) {
                    backingMap.remove(l);
                    ret.add(new int[]{ _x, _z });
                    long leftKey = coordsToKey(_x - 1, _z);
                    long rightKey = coordsToKey(_x + 1, _z);
                    long upKey = coordsToKey(_x, _z - 1);
                    long downKey = coordsToKey(_x, _z + 1);
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
        return "importclaims";
    }

    @Override
    public @NotNull String getDescription() {
        return "Imports claims from the ClaimChunk plugin";
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
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ClaimChunk");
        if (plugin != null) {
            if (!plugin.isEnabled()) {
                sender.sendMessage(Component.text("The claimchunk plugin does not seem to be enabled.").color(NamedTextColor.RED));
                return;
            }
            com.cjburkey.claimchunk.ClaimChunk qual = (com.cjburkey.claimchunk.ClaimChunk) plugin;
            sender.sendMessage(Component.text("Getting data handler...").color(NamedTextColor.GREEN));
            com.cjburkey.claimchunk.data.newdata.IClaimChunkDataHandler dataHandler;
            try {
                Field field = qual.getClass().getDeclaredField("dataHandler");
                field.setAccessible(true);
                dataHandler = (com.cjburkey.claimchunk.data.newdata.IClaimChunkDataHandler) field.get(qual);
            } catch (Exception e) {
                sender.sendMessage(Component.text("Failed. See details in console.").color(NamedTextColor.RED));
                e.printStackTrace();
                return;
            }
            int importIndex = 1;
            for (World w : Bukkit.getWorlds()) {
                sender.sendMessage(Component.empty()
                        .append(Component.text("Processing world ").color(NamedTextColor.DARK_GREEN).decorate(TextDecoration.ITALIC))
                        .append(Component.text(w.getName()).color(NamedTextColor.GOLD))
                );
                UUIDPlane plane = new UUIDPlane();
                for (com.cjburkey.claimchunk.chunk.DataChunk chk : dataHandler.getClaimedChunks()) {
                    com.cjburkey.claimchunk.chunk.ChunkPos pos = chk.chunk;
                    if (pos.getWorld().equalsIgnoreCase(w.getName())) {
                        sender.sendMessage(
                                Component.empty()
                                        .append(Component.text("Found claimed chunk at ").color(NamedTextColor.GREEN))
                                        .append(Component.text(pos.getX() + ", " + pos.getZ()).color(NamedTextColor.GOLD))
                        );
                        UUID ownerUUID = chk.player;
                        plane.set(pos.getX(), pos.getZ(), ownerUUID);
                    }
                };
                sender.sendMessage(Component.text("Performing flood fill algorithm").color(NamedTextColor.GREEN));
                Map<UUID, List<List<int[]>>> clumps = plane.pullClumps();
                for (Map.Entry<UUID, List<List<int[]>>> entry : clumps.entrySet()) {
                    UUID uuid = entry.getKey();
                    OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                    Component name;
                    Player online = op.getPlayer();
                    if (online != null) {
                        name = online.displayName();
                    } else {
                        String n = op.getName();
                        if (n == null) n = uuid.toString();
                        name = Component.text(n);
                    }
                    sender.sendMessage(Component.empty()
                            .append(Component.text("Creating claims for player ").color(NamedTextColor.GREEN))
                            .append(name.color(NamedTextColor.GOLD))
                    );
                    for (List<int[]> cells : entry.getValue()) {
                        Set<Chunk> set = new HashSet<>();
                        for (int[] cell : cells) {
                            set.add(w.getChunkAt(cell[0], cell[1]));
                        }
                        Claim claim = new Claim("Imported Claim #" + importIndex, set, op);
                        claim.claim();
                        importIndex++;
                    }
                    sender.sendMessage(Component.text("Success").color(NamedTextColor.GREEN));
                }
            }
            sender.sendMessage(Component.text("Processed all worlds successfully. Disabling ClaimChunk plugin...").color(NamedTextColor.GREEN));
            qual.disable();
            sender.sendMessage(Component.text("Done").color(NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("* ClaimChunk does not appear to be installed and enabled").color(NamedTextColor.RED));
        }
    }

}
