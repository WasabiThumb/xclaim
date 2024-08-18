package codes.wasabi.xclaim.api;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XCPlayer {

    private static final ReentrantReadWriteLock trustConfigLock = new ReentrantReadWriteLock();

    public static @NotNull XCPlayer of(@NotNull OfflinePlayer ply) {
        return new XCPlayer(ply);
    }

    public static @NotNull XCPlayer of(@NotNull UUID uuid) {
        return new XCPlayer(uuid);
    }

    private final UUID uuid;
    private final StampedLock valuesLock = new StampedLock();
    private int valuesFlag = 0;
    private OfflinePlayer op;
    private String uuidString;

    protected XCPlayer(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    protected XCPlayer(@NotNull OfflinePlayer ply) {
        this(ply.getUniqueId());
        this.valuesFlag = 1;
        this.op = ply;
    }

    public @NotNull OfflinePlayer getOfflinePlayer() {
        long stamp = this.valuesLock.readLock();
        try {
            if ((this.valuesFlag & 1) == 1) return this.op;
            stamp = this.valuesLock.tryConvertToWriteLock(stamp);
            this.op = Bukkit.getOfflinePlayer(this.uuid);
            this.valuesFlag |= 1;
            return this.op;
        } finally {
            this.valuesLock.unlock(stamp);
        }
    }

    public @Nullable Player getPlayer() {
        return this.getOfflinePlayer().getPlayer();
    }

    public boolean trustPlayer(@NotNull OfflinePlayer player) {
        List<OfflinePlayer> list = getTrustedPlayers();
        if (list.contains(player)) return false;
        list.add(player);
        return true;
    }

    public boolean untrustPlayer(@NotNull OfflinePlayer player) {
        List<OfflinePlayer> list = getTrustedPlayers();
        boolean removed = false;
        while (list.contains(player)) {
            list.remove(player);
            removed = true;
        }
        return removed;
    }

    public boolean playerTrusted(@NotNull OfflinePlayer player) {
        String query = player.getUniqueId().toString();
        List<?> entries;
        trustConfigLock.readLock().lock();
        try {
            entries = XClaim.trustConfig.getList(this.getUUIDString(), new ArrayList<String>());
        } finally {
            trustConfigLock.readLock().unlock();
        }
        for (Object ob : entries) {
            if (ob instanceof String) {
                if (Objects.equals(ob, query)) return true;
            }
        }
        return false;
    }

    public int getNumTrustedPlayers() {
        List<?> entries;
        trustConfigLock.readLock().lock();
        try {
            entries = XClaim.trustConfig.getList(this.getUUIDString(), new ArrayList<String>());
        } finally {
            trustConfigLock.readLock().unlock();
        }
        return entries.size();
    }

    private @NotNull List<OfflinePlayer> getCurrentTrustedPlayers() {
        List<?> entries;
        trustConfigLock.readLock().lock();
        try {
            entries = XClaim.trustConfig.getList(this.getUUIDString(), new ArrayList<String>());
        } finally {
            trustConfigLock.readLock().unlock();
        }
        List<OfflinePlayer> ret = new ArrayList<>();
        for (Object ob : entries) {
            if (ob instanceof String) {
                try {
                    UUID uuid = UUID.fromString((String) ob);
                    ret.add(Bukkit.getOfflinePlayer(uuid));
                } catch (Exception ignored) {}
            }
        }
        return ret;
    }

    public void setTrustedPlayers(@NotNull List<OfflinePlayer> players) {
        List<String> list = players.stream().flatMap((OfflinePlayer op) -> Stream.of(op.getUniqueId().toString())).collect(Collectors.toList());
        trustConfigLock.writeLock().lock();
        try {
            XClaim.trustConfig.set(this.getUUIDString(), list);
        } finally {
            trustConfigLock.writeLock().unlock();
        }
    }

    private double[] getPermGroup() {
        ConfigurationSection section = XClaim.mainConfig.getConfigurationSection("limits");
        if (section == null) return new double[]{ 0, 0, 0, 0, 0, -1 };
        int maxChunks = 0;
        int maxClaims = 0;
        double claimPrice = -1;
        double unclaimReward = 0;
        int freeChunks = 0;
        int maxInWorld = -1;
        for (String groupName : section.getKeys(false)) {
            boolean inGroup = true;
            if (!groupName.equalsIgnoreCase("default")) {
                inGroup = false;
                final OfflinePlayer op = this.getOfflinePlayer();
                Player ply = op.getPlayer();
                if (ply != null) {
                    inGroup = ply.hasPermission("xclaim.group." + groupName);
                    if (!inGroup) {
                        int giveAfter = section.getInt(groupName + ".give-after", -1);
                        if (giveAfter == 0) {
                            ply.addAttachment(XClaim.instance, "xclaim.group." + groupName, true);
                            inGroup = true;
                        } else if (giveAfter > 0) {
                            long elapsed = Platform.get().getLastSeen(op) - op.getFirstPlayed();
                            int seconds = (int) Math.round(Math.min((elapsed / 1000d), Integer.MAX_VALUE));
                            if (seconds >= giveAfter) {
                                ply.addAttachment(XClaim.instance, "xclaim.group." + groupName, true);
                                inGroup = true;
                            }
                        }
                    }
                }
            }
            if (inGroup) {
                maxChunks = Math.max(maxChunks, section.getInt(groupName + ".max-chunks", 0));
                maxClaims = Math.max(maxClaims, section.getInt(groupName + ".max-claims", 0));
                double proposed = section.getDouble(groupName + ".claim-price", -1);
                if (claimPrice < 0) {
                    claimPrice = proposed;
                } else if (proposed > 0) {
                    claimPrice = Math.min(claimPrice, proposed);
                }
                unclaimReward = Math.max(unclaimReward, section.getDouble(groupName + ".unclaim-reward", 0));
                freeChunks = Math.max(freeChunks, section.getInt(groupName + ".free-chunks", 0));
                int candidate = section.getInt(groupName + ".max-claims-in-world", -1);
                if (candidate > 0) maxInWorld = Math.max(maxInWorld, candidate);
            }
        }
        if (claimPrice < 0) claimPrice = 0;
        return new double[]{ maxChunks, maxClaims, claimPrice, unclaimReward, freeChunks, maxInWorld };
    }

    public int getMaxChunks() {
        return (int) getPermGroup()[0];
    }

    public int getMaxClaims() {
        return (int) getPermGroup()[1];
    }

    public double getClaimPrice() {
        return getPermGroup()[2];
    }

    public double getUnclaimReward() {
        return getPermGroup()[3];
    }

    public int getFreeChunks() {
        return (int) getPermGroup()[4];
    }

    public int getMaxClaimsInWorld() {
        int ret = (int) getPermGroup()[5];
        if (ret < 1) return Integer.MAX_VALUE;
        return ret;
    }

    public @NotNull List<OfflinePlayer> getTrustedPlayers() {
        return new AbstractList<OfflinePlayer>() {
            @Override
            public OfflinePlayer get(int i) {
                return XCPlayer.this.getCurrentTrustedPlayers().get(i);
            }

            @Override
            public int size() {
                return XCPlayer.this.getNumTrustedPlayers();
            }

            @Override
            public boolean contains(Object o) {
                return XCPlayer.this.getCurrentTrustedPlayers().contains(o);
            }

            @Override
            public boolean add(OfflinePlayer offlinePlayer) {
                List<OfflinePlayer> cur = XCPlayer.this.getCurrentTrustedPlayers();
                cur.add(offlinePlayer);
                XCPlayer.this.setTrustedPlayers(cur);
                return true;
            }

            @Override
            public void add(int index, OfflinePlayer element) {
                List<OfflinePlayer> cur = XCPlayer.this.getCurrentTrustedPlayers();
                cur.add(index, element);
                XCPlayer.this.setTrustedPlayers(cur);
            }

            @Override
            public OfflinePlayer set(int index, OfflinePlayer element) {
                List<OfflinePlayer> cur = XCPlayer.this.getCurrentTrustedPlayers();
                OfflinePlayer ret = cur.set(index, element);
                XCPlayer.this.setTrustedPlayers(cur);
                return ret;
            }

            @Override
            public OfflinePlayer remove(int index) {
                List<OfflinePlayer> cur = XCPlayer.this.getCurrentTrustedPlayers();
                OfflinePlayer ret = cur.remove(index);
                XCPlayer.this.setTrustedPlayers(cur);
                return ret;
            }

            @Override
            public @NotNull Iterator<OfflinePlayer> iterator() {
                return XCPlayer.this.getCurrentTrustedPlayers().iterator();
            }
        };
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.uuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof XCPlayer) {
            return ((XCPlayer) obj).uuid.equals(this.uuid);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "XCPlayer[uuid=" + this.uuid + "]";
    }

    public @NotNull UUID getUniqueId() {
        return this.uuid;
    }

    public @NotNull String getUUIDString() {
        long stamp = this.valuesLock.readLock();
        try {
            if ((this.valuesFlag & 2) == 2) return this.uuidString;
            stamp = this.valuesLock.tryConvertToWriteLock(stamp);
            this.uuidString = this.uuid.toString();
            this.valuesFlag |= 2;
            return this.uuidString;
        } finally {
            this.valuesLock.unlock(stamp);
        }
    }

    public @Nullable String getName() {
        return this.getOfflinePlayer().getName();
    }

}
