package codes.wasabi.xclaim.api;

import codes.wasabi.xclaim.XClaim;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class XCPlayer implements OfflinePlayer {

    public static @NotNull XCPlayer of(@NotNull OfflinePlayer ply) {
        if (ply instanceof XCPlayer xcp) return xcp;
        return new XCPlayer(ply);
    }

    private final String uuidString;
    private final OfflinePlayer op;

    XCPlayer(@NotNull UUID uuid) {
        uuidString = uuid.toString();
        op = Bukkit.getOfflinePlayer(uuid);
    }

    XCPlayer(@NotNull OfflinePlayer ply) {
        uuidString = ply.getUniqueId().toString();
        op = ply;
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
        List<?> entries = XClaim.trustConfig.getList(uuidString, new ArrayList<String>());
        for (Object ob : entries) {
            if (ob instanceof String str) {
                if (str.equals(query)) return true;
            }
        }
        return false;
    }

    public @NotNull OfflinePlayer getOfflinePlayer() {
        return op;
    }

    private @NotNull List<OfflinePlayer> getCurrentTrustedPlayers() {
        List<?> entries = XClaim.trustConfig.getList(uuidString, new ArrayList<String>());
        List<OfflinePlayer> ret = new ArrayList<>();
        for (Object ob : entries) {
            if (ob instanceof String str) {
                try {
                    UUID uuid = UUID.fromString(str);
                    ret.add(Bukkit.getOfflinePlayer(uuid));
                } catch (Exception ignored) {}
            }
        }
        return ret;
    }

    public void setTrustedPlayers(@NotNull List<OfflinePlayer> players) {
        List<String> list = players.stream().flatMap((OfflinePlayer op) -> Stream.of(op.getUniqueId().toString())).toList();
        XClaim.trustConfig.set(uuidString, list);
    }

    private int[] getPermGroup() {
        ConfigurationSection section = XClaim.mainConfig.getConfigurationSection("limits");
        if (section == null) return new int[]{ 0, 0 };
        int maxChunks = 0;
        int maxClaims = 0;
        for (String groupName : section.getKeys(false)) {
            boolean inGroup = true;
            if (!groupName.equalsIgnoreCase("default")) {
                inGroup = false;
                Player ply = op.getPlayer();
                if (ply != null) {
                    inGroup = ply.hasPermission("xclaim.group." + groupName);
                    if (!inGroup) {
                        int giveAfter = section.getInt(groupName + ".give-after", -1);
                        if (giveAfter == 0) {
                            ply.addAttachment(XClaim.instance, "xclaim.group." + groupName, true);
                            inGroup = true;
                        } else if (giveAfter > 0) {
                            long elapsed = op.getLastSeen() - op.getFirstPlayed();
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
            }
        }
        return new int[]{ maxChunks, maxClaims };
    }

    public int getMaxChunks() {
        return getPermGroup()[0];
    }

    public int getMaxClaims() {
        return getPermGroup()[1];
    }

    public @NotNull List<OfflinePlayer> getTrustedPlayers() {
        return new List<>() {
            @Override
            public int size() {
                return getCurrentTrustedPlayers().size();
            }

            @Override
            public boolean isEmpty() {
                return size() > 0;
            }

            @Override
            public boolean contains(Object o) {
                return getCurrentTrustedPlayers().contains(o);
            }

            @NotNull
            @Override
            public Iterator<OfflinePlayer> iterator() {
                return getCurrentTrustedPlayers().iterator();
            }

            @NotNull
            @Override
            public Object[] toArray() {
                return getCurrentTrustedPlayers().toArray();
            }

            @NotNull
            @Override
            public <T> T[] toArray(@NotNull T[] a) {
                return getCurrentTrustedPlayers().toArray(a);
            }

            @Override
            public boolean add(OfflinePlayer offlinePlayer) {
                List<OfflinePlayer> cur = getCurrentTrustedPlayers();
                cur.add(offlinePlayer);
                setTrustedPlayers(cur);
                return true;
            }

            @Override
            public boolean remove(Object o) {
                List<OfflinePlayer> cur = getCurrentTrustedPlayers();
                if (cur.remove(o)) {
                    setTrustedPlayers(cur);
                    return true;
                }
                return false;
            }

            @Override
            public boolean containsAll(@NotNull Collection<?> c) {
                return getCurrentTrustedPlayers().containsAll(c);
            }

            @Override
            public boolean addAll(@NotNull Collection<? extends OfflinePlayer> c) {
                List<OfflinePlayer> cur = getCurrentTrustedPlayers();
                if (cur.addAll(c)) {
                    setTrustedPlayers(cur);
                    return true;
                }
                return false;
            }

            @Override
            public boolean addAll(int index, @NotNull Collection<? extends OfflinePlayer> c) {
                List<OfflinePlayer> cur = getCurrentTrustedPlayers();
                if (cur.addAll(index, c)) {
                    setTrustedPlayers(cur);
                    return true;
                }
                return false;
            }

            @Override
            public boolean removeAll(@NotNull Collection<?> c) {
                List<OfflinePlayer> cur = getCurrentTrustedPlayers();
                if (cur.removeAll(c)) {
                    setTrustedPlayers(cur);
                    return true;
                }
                return false;
            }

            @Override
            public boolean retainAll(@NotNull Collection<?> c) {
                List<OfflinePlayer> cur = getCurrentTrustedPlayers();
                if (cur.retainAll(c)) {
                    setTrustedPlayers(cur);
                    return true;
                }
                return false;
            }

            @Override
            public void clear() {
                setTrustedPlayers(Collections.emptyList());
            }

            @Override
            public OfflinePlayer get(int index) {
                return getCurrentTrustedPlayers().get(index);
            }

            @Override
            public OfflinePlayer set(int index, OfflinePlayer element) {
                List<OfflinePlayer> cur = getCurrentTrustedPlayers();
                OfflinePlayer ret = cur.set(index, element);
                setTrustedPlayers(cur);
                return ret;
            }

            @Override
            public void add(int index, OfflinePlayer element) {
                List<OfflinePlayer> cur = getCurrentTrustedPlayers();
                cur.add(index, element);
                setTrustedPlayers(cur);
            }

            @Override
            public OfflinePlayer remove(int index) {
                List<OfflinePlayer> cur = getCurrentTrustedPlayers();
                OfflinePlayer ret = cur.remove(index);
                setTrustedPlayers(cur);
                return ret;
            }

            @Override
            public int indexOf(Object o) {
                return getCurrentTrustedPlayers().indexOf(o);
            }

            @Override
            public int lastIndexOf(Object o) {
                return getCurrentTrustedPlayers().lastIndexOf(o);
            }

            @NotNull
            @Override
            public ListIterator<OfflinePlayer> listIterator() {
                return getCurrentTrustedPlayers().listIterator();
            }

            @NotNull
            @Override
            public ListIterator<OfflinePlayer> listIterator(int index) {
                return getCurrentTrustedPlayers().listIterator(index);
            }

            @NotNull
            @Override
            public List<OfflinePlayer> subList(int fromIndex, int toIndex) {
                return getCurrentTrustedPlayers().subList(fromIndex, toIndex);
            }
        };
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(op);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof XCPlayer xcp) {
            return xcp.op.equals(op);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "XCPlayer[uuid=" + op.getUniqueId() + "]";
    }

    @Override
    public boolean isOnline() {
        return op.isOnline();
    }

    @Override
    public @Nullable String getName() {
        return op.getName();
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return op.getUniqueId();
    }

    @Override
    public @NotNull PlayerProfile getPlayerProfile() {
        return op.getPlayerProfile();
    }

    @Override
    public boolean isBanned() {
        return op.isBanned();
    }

    @Override
    public boolean isWhitelisted() {
        return op.isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean value) {
        op.setWhitelisted(value);
    }

    @Override
    public @Nullable Player getPlayer() {
        return op.getPlayer();
    }

    @Override
    public long getFirstPlayed() {
        return op.getFirstPlayed();
    }

    @Override
    public long getLastPlayed() {
        return op.getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return op.hasPlayedBefore();
    }

    @Override
    public @Nullable Location getBedSpawnLocation() {
        return op.getBedSpawnLocation();
    }

    @Override
    public long getLastLogin() {
        return op.getLastLogin();
    }

    @Override
    public long getLastSeen() {
        return op.getLastSeen();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        op.incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        op.decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, int amount) throws IllegalArgumentException {
        op.incrementStatistic(statistic, amount);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, int amount) throws IllegalArgumentException {
        op.decrementStatistic(statistic, amount);
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, int newValue) throws IllegalArgumentException {
        op.setStatistic(statistic, newValue);
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        return op.getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        op.incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        op.decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        return op.getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int amount) throws IllegalArgumentException {
        op.incrementStatistic(statistic, material, amount);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int amount) throws IllegalArgumentException {
        op.decrementStatistic(statistic, material, amount);
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull Material material, int newValue) throws IllegalArgumentException {
        op.setStatistic(statistic, material, newValue);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        op.incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        op.decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        return op.getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int amount) throws IllegalArgumentException {
        op.incrementStatistic(statistic, entityType, amount);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int amount) {
        op.decrementStatistic(statistic, entityType, amount);
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int newValue) {
        op.setStatistic(statistic, entityType, newValue);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return op.serialize();
    }

    @Override
    public boolean isOp() {
        return op.isOp();
    }

    @Override
    public void setOp(boolean value) {
        op.setOp(value);
    }

    @Override
    public @NotNull BanEntry banPlayer(@Nullable String reason) {
        return op.banPlayer(reason);
    }

    @Override
    public @NotNull BanEntry banPlayer(@Nullable String reason, @Nullable String source) {
        return op.banPlayer(reason, source);
    }

    @Override
    public @NotNull BanEntry banPlayer(@Nullable String reason, @Nullable Date expires) {
        return op.banPlayer(reason, expires);
    }

    @Override
    public @NotNull BanEntry banPlayer(@Nullable String reason, @Nullable Date expires, @Nullable String source) {
        return op.banPlayer(reason, expires, source);
    }

    @Override
    public @NotNull BanEntry banPlayer(@Nullable String reason, @Nullable Date expires, @Nullable String source, boolean kickIfOnline) {
        return op.banPlayer(reason, expires, source, kickIfOnline);
    }

}
