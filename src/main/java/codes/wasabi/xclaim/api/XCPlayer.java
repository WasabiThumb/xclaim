package codes.wasabi.xclaim.api;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XCPlayer {

    public static @NotNull XCPlayer of(@NotNull OfflinePlayer ply) {
        if (ply instanceof XCPlayer) return (XCPlayer) ply;
        return new XCPlayer(ply);
    }

    private final String uuidString;
    private final OfflinePlayer op;

    protected XCPlayer(@NotNull UUID uuid) {
        uuidString = uuid.toString();
        op = Bukkit.getOfflinePlayer(uuid);
    }

    protected XCPlayer(@NotNull OfflinePlayer ply) {
        this(ply.getUniqueId());
    }

    public @NotNull OfflinePlayer getOfflinePlayer() {
        return op;
    }

    public @Nullable Player getPlayer() {
        return op.getPlayer();
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
            if (ob instanceof String) {
                if (Objects.equals(ob, query)) return true;
            }
        }
        return false;
    }

    private @NotNull List<OfflinePlayer> getCurrentTrustedPlayers() {
        List<?> entries = XClaim.trustConfig.getList(uuidString, new ArrayList<String>());
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
        XClaim.trustConfig.set(uuidString, list);
    }

    private double[] getPermGroup() {
        ConfigurationSection section = XClaim.mainConfig.getConfigurationSection("limits");
        if (section == null) return new double[]{ 0, 0, 0, 0, 0 };
        int maxChunks = 0;
        int maxClaims = 0;
        double claimPrice = -1;
        double unclaimReward = 0;
        int freeChunks = 0;
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
            }
        }
        if (claimPrice < 0) claimPrice = 0;
        return new double[]{ maxChunks, maxClaims, claimPrice, unclaimReward, freeChunks };
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
                return new HashSet<>(getCurrentTrustedPlayers()).containsAll(c);
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
        if (obj instanceof XCPlayer) {
            return ((XCPlayer) obj).op.equals(op);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "XCPlayer[uuid=" + op.getUniqueId() + "]";
    }

    public @NotNull UUID getUniqueId() {
        return op.getUniqueId();
    }

    public @Nullable String getName() {
        return op.getName();
    }

}
