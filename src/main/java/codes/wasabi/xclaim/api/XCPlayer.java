package codes.wasabi.xclaim.api;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.ProxyList;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

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
        return this.getTrustedPlayersSet().add(player);
    }

    public boolean untrustPlayer(@NotNull OfflinePlayer player) {
        return this.getTrustedPlayersSet().remove(player);
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

    @Deprecated
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

    private @NotNull LinkedHashSet<UUID> getCurrentTrustedPlayers() {
        List<?> entries;
        trustConfigLock.readLock().lock();
        try {
            entries = XClaim.trustConfig.getList(this.getUUIDString(), new ArrayList<String>());
        } finally {
            trustConfigLock.readLock().unlock();
        }
        LinkedHashSet<UUID> set = new LinkedHashSet<>();
        for (Object ob : entries) {
            if (ob instanceof String) {
                try {
                    UUID uuid = UUID.fromString((String) ob);
                    set.add(uuid);
                } catch (Exception ignored) {}
            }
        }
        return set;
    }

    public void setTrustedPlayers(@NotNull List<OfflinePlayer> players) {
        List<UUID> uuidList = new ProxyList<>(players, OfflinePlayer::getUniqueId);
        this.setTrustedPlayers0(uuidList);
    }

    private void setTrustedPlayers0(@NotNull List<UUID> players) {
        List<String> strList = new ProxyList<>(players, UUID::toString);
        trustConfigLock.writeLock().lock();
        try {
            XClaim.trustConfig.set(this.getUUIDString(), strList);
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

    /**
     * Returns a snapshot of the current trusted players as a list. Updates to this list will update the internal
     * database, however updates to the database will not necessarily update the list.
     *
     * The returned list is backed by a linked set in 1.14.1+.
     * Most sensible operations will work as expected, however compromises are made.
     * @deprecated Use {@link #getTrustedPlayersSet()}
     */
    public @NotNull List<OfflinePlayer> getTrustedPlayers() {
        final LinkedHashSet<UUID> current = this.getCurrentTrustedPlayers();

        // Returns a list for old API compatibility. Not actually a list because duplicate values cannot be added.

        return new AbstractList<OfflinePlayer>() {
            private UUID get0(int i) {
                final int size = current.size();
                if (i < 0 || i >= size)
                    throw new IllegalArgumentException("Index " + i + " out of bounds for length " + size);

                final Iterator<UUID> iter = current.iterator();
                UUID next;
                while (iter.hasNext()) {
                    next = iter.next();
                    if ((i--) == 0) return next;
                }

                // Iterator ended before item was found, despite precondition
                throw new ConcurrentModificationException();
            }

            @Override
            public OfflinePlayer get(int i) {
                return Bukkit.getOfflinePlayer(this.get0(i));
            }

            @Override
            public int size() {
                return current.size();
            }

            @Override
            public boolean contains(Object o) {
                if (!(o instanceof OfflinePlayer)) return false;
                UUID id = ((OfflinePlayer) o).getUniqueId();
                return current.contains(id);
            }

            @Override
            public boolean add(OfflinePlayer offlinePlayer) {
                current.add(offlinePlayer.getUniqueId());
                XCPlayer.this.setTrustedPlayers0(new ArrayList<>(current));
                return true;
            }

            @Override
            public void add(int index, OfflinePlayer element) {
                // This won't get used, but we should implement it anyway. Prepare for utter garbage.
                final int size = current.size();
                if (index < 0 || index > size)
                    throw new IllegalArgumentException("Index " + index + " out of bounds for length " + size);
                if (index == size) {
                    this.add(element);
                    return;
                }

                UUID id = element.getUniqueId();
                if (current.contains(id)) return;

                List<UUID> newList = new ArrayList<>(size + 1);
                for (int i=0; i < index; i++) {
                    newList.add(this.get0(i));
                }
                newList.add(id);
                for (int i=index; i < size; i++) {
                    newList.add(this.get0(i));
                }

                XCPlayer.this.setTrustedPlayers0(newList);
                current.clear();
                current.addAll(newList);
            }

            @Override
            public OfflinePlayer set(int index, OfflinePlayer element) {
                // Prepare for even more utter garbage.
                final int size = current.size();
                if (index < 0 || index >= size)
                    throw new IllegalArgumentException("Index " + index + " out of bounds for length " + size);

                UUID oldValue = this.get0(index);

                UUID id = element.getUniqueId();
                if (current.contains(id)) {
                    // Shady.
                    if (oldValue.equals(id)) {
                        return element;
                    } else {
                        return this.remove(index);
                    }
                }

                List<UUID> newList = new ArrayList<>(size);
                for (int i=0; i < index; i++) {
                    newList.add(this.get0(i));
                }
                newList.add(id);
                for (int i=(index + 1); i < size; i++) {
                    newList.add(this.get0(i));
                }

                XCPlayer.this.setTrustedPlayers0(newList);
                current.clear();
                current.addAll(newList);

                return Bukkit.getOfflinePlayer(oldValue);
            }

            @Override
            public OfflinePlayer remove(int index) {
                UUID id = this.get0(index);
                current.remove(id);
                return Bukkit.getOfflinePlayer(id);
            }

            @Override
            public @NotNull Iterator<OfflinePlayer> iterator() {
                return current.stream().map(Bukkit::getOfflinePlayer).iterator();
            }
        };
    }

    /**
     * Returns a snapshot of the current trusted players as a set. Updates to this list will update the internal
     * database, however updates to the database will not necessarily update the set.
     */
    public @NotNull Set<OfflinePlayer> getTrustedPlayersSet() {
        final LinkedHashSet<UUID> current = this.getCurrentTrustedPlayers();

        return new AbstractSet<OfflinePlayer>() {
            @Override
            public @NotNull Iterator<OfflinePlayer> iterator() {
                return current.stream().map(Bukkit::getOfflinePlayer).iterator();
            }

            @Override
            public int size() {
                return current.size();
            }

            @Override
            public boolean isEmpty() {
                return current.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (!(o instanceof OfflinePlayer)) return false;
                UUID id = ((OfflinePlayer) o).getUniqueId();
                return current.contains(id);
            }

            @Override
            public boolean add(OfflinePlayer offlinePlayer) {
                UUID id = offlinePlayer.getUniqueId();
                if (current.add(id)) {
                    XCPlayer.this.setTrustedPlayers0(new ArrayList<>(current));
                    return true;
                }
                return false;
            }

            @Override
            public boolean remove(Object o) {
                if (!(o instanceof OfflinePlayer)) return false;
                UUID id = ((OfflinePlayer) o).getUniqueId();
                if (current.remove(id)) {
                    XCPlayer.this.setTrustedPlayers0(new ArrayList<>(current));
                    return true;
                }
                return false;
            }

            @Override
            public void clear() {
                current.clear();
                XCPlayer.this.setTrustedPlayers(Collections.emptyList());
            }

            @Override
            public boolean addAll(@NotNull Collection<? extends OfflinePlayer> c) {
                boolean any = false;
                UUID id;
                for (OfflinePlayer op : c) {
                    id = op.getUniqueId();
                    if (current.add(id)) any = true;
                }
                if (any) XCPlayer.this.setTrustedPlayers0(new ArrayList<>(current));
                return any;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                boolean any = false;
                UUID id;
                for (Object ob : c) {
                    if (!(ob instanceof OfflinePlayer)) continue;
                    id = ((OfflinePlayer) ob).getUniqueId();
                    if (current.add(id)) any = true;
                }
                if (any) XCPlayer.this.setTrustedPlayers0(new ArrayList<>(current));
                return any;
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
