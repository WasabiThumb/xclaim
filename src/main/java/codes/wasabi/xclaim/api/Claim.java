package codes.wasabi.xclaim.api;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.TrustLevel;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import codes.wasabi.xclaim.gui.ChunkEditor;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Stream;

public class Claim {

    private static final Set<Claim> registry = new HashSet<>();

    public static @NotNull @UnmodifiableView Set<Claim> getAll() {
        return Collections.unmodifiableSet(registry);
    }

    public static @Nullable Claim getByName(@NotNull String name, boolean ignoreCase) {
        for (Claim c : registry) {
            if (ignoreCase ? c.getName().equalsIgnoreCase(name) : c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public static @Nullable Claim getByName(@NotNull String name) {
        return getByName(name, true);
    }

    public static @NotNull @UnmodifiableView Set<Claim> getByOwner(@NotNull XCPlayer owner) {
        Set<Claim> set = new HashSet<>();
        for (Claim c : registry) {
            if (c.owner.equals(owner)) set.add(c);
        }
        return Collections.unmodifiableSet(set);
    }

    public static @NotNull @UnmodifiableView Set<Claim> getByOwner(@NotNull OfflinePlayer owner) {
        return getByOwner(XCPlayer.of(owner));
    }

    public static @Nullable Claim getByChunk(@NotNull Chunk chunk) {
        for (Claim c : registry) {
            for (Chunk chk : c.chunks) {
                if (chk.getX() != chunk.getX()) continue;
                if (chk.getZ() != chunk.getZ()) continue;
                if (chk.getWorld().getName().equals(chunk.getWorld().getName())) return c;
            }
        }
        return null;
    }

    public static @NotNull Claim deserialize(@NotNull ConfigurationSection section) throws IllegalArgumentException {
        String name = section.getString("name");
        if (name == null) throw new IllegalArgumentException("Missing property \"name\"");
        String ownerUUIDString = section.getString("owner");
        if (ownerUUIDString == null) throw new IllegalArgumentException("Missing property \"owner\"");
        UUID ownerUUID;
        try {
            ownerUUID = UUID.fromString(ownerUUIDString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Property \"owner\" is not a valid UUID");
        }
        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUUID);
        String worldName = section.getString("world");
        if (worldName == null) throw new IllegalArgumentException("Missing property \"world\"");
        World world = Bukkit.getWorld(worldName);
        if (world == null) throw new IllegalArgumentException("No world with the name \"" + worldName + "\" could be found");
        Set<Chunk> chunks = new HashSet<>();
        ConfigurationSection sec;
        sec = section.getConfigurationSection("chunks");
        if (sec == null) throw new IllegalArgumentException("Missing property \"chunks\"");
        for (String key : sec.getKeys(false)) {
            ConfigurationSection sc = sec.getConfigurationSection(key);
            if (sc == null) throw new IllegalArgumentException("Chunk with ID " + key + " is malformed!");
            int x = sc.getInt("x");
            int z = sc.getInt("z");
            Chunk chunk = world.getChunkAt(x, z);
            chunks.add(chunk);
        }
        Map<Permission, TrustLevel> global = new HashMap<>();
        sec = section.getConfigurationSection("permissions");
        if (sec == null) throw new IllegalArgumentException("Missing property \"permissions\"");
        for (String key : sec.getKeys(false)) {
            Permission perm;
            try {
                perm = Permission.valueOf(key);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown permission \"" + key + "\"");
            }
            String value = sec.getString(key);
            if (value == null) throw new IllegalArgumentException("Illegal value for permissions." + key);
            TrustLevel trust;
            try {
                trust = TrustLevel.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown trust level \"" + key + "\"");
            }
            global.put(perm, trust);
        }
        Map<UUID, EnumSet<Permission>> players = new HashMap<>();
        sec = section.getConfigurationSection("users");
        if (sec == null) throw new IllegalArgumentException("Missing property \"users\"");
        for (String key : sec.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("\"" + key + "\" is not a valid UUID");
            }
            EnumSet<Permission> set = EnumSet.noneOf(Permission.class);
            List<?> list = sec.getList(key);
            if (list == null) throw new IllegalArgumentException("users." + key + " is not a list");
            for (int i=0; i < list.size(); i++) {
                Object ob = list.get(i);
                if (ob instanceof String str) {
                    Permission perm;
                    try {
                        perm = Permission.valueOf(str);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Unknown permission \"" + str + "\"");
                    }
                    set.add(perm);
                } else {
                    throw new IllegalArgumentException("Element " + i + " of users." + key + " is not a string");
                }
            }
            players.put(uuid, set);
        }
        return new Claim(name, chunks, new XCPlayer(owner), global, players);
    }

    private String name;
    private final Set<Chunk> chunks;
    private final XCPlayer owner;
    private final Map<Permission, TrustLevel> globalPerms;
    private final Map<UUID, EnumSet<Permission>> playerPerms;
    private BoundingBox outerBounds;
    private boolean manageHandlers = false;

    private BoundingBox getChunkBounds(Chunk c) {
        World w = c.getWorld();
        return BoundingBox.of(c.getBlock(0, w.getMinHeight(), 0), c.getBlock(15, w.getMaxHeight() - 1, 15));
    }

    private void generateBounds() {
        BoundingBox bb = null;
        boolean set = false;
        for (Chunk c : chunks) {
            BoundingBox bounds = getChunkBounds(c);
            if (!set) {
                bb = bounds;
                set = true;
            } else {
                bb.union(bounds);
            }
        }
        outerBounds = (set ? bb : new BoundingBox());
    }

    public @NotNull BoundingBox getOuterBounds() {
        return outerBounds;
    }

    Claim(@NotNull String name, @NotNull Set<Chunk> chunks, @NotNull XCPlayer owner, @NotNull Map<Permission, TrustLevel> globalPerms, @NotNull Map<UUID, EnumSet<Permission>> playerPerms) {
        this.name = name;
        this.chunks = new HashSet<>(chunks);
        this.owner = owner;
        this.globalPerms = new HashMap<>(globalPerms);
        this.playerPerms = new HashMap<>(playerPerms);
        generateBounds();
    }

    public Claim(@NotNull String name, @NotNull Set<Chunk> chunks, @NotNull XCPlayer owner) {
        this(name, new HashSet<>(chunks), owner, Collections.emptyMap(), Collections.emptyMap());
    }

    public Claim(@NotNull String name, @NotNull Set<Chunk> chunks, @NotNull OfflinePlayer owner) {
        this(name, new HashSet<>(chunks), XCPlayer.of(owner), Collections.emptyMap(), Collections.emptyMap());
    }

    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        for (Player ply : Bukkit.getOnlinePlayers()) {
            if (Objects.equals(ChunkEditor.getEditing(ply), this)) {
                PersistentDataContainer pdc = ply.getPersistentDataContainer();
                pdc.set(ChunkEditor.getNameKey(), PersistentDataType.STRING, name);
            }
        }
        this.name = name;
    }

    public XCPlayer getOwner() {
        return owner;
    }

    public @NotNull @UnmodifiableView Set<Chunk> getChunks() {
        return Collections.unmodifiableSet(chunks);
    }

    public @Nullable World getWorld() {
        if (chunks.size() > 0) {
            return chunks.iterator().next().getWorld();
        }
        return null;
    }

    public boolean addChunk(@NotNull Chunk chunk) throws IllegalArgumentException {
        boolean claim = false;
        if (chunks.size() > 0) {
            World w = chunks.iterator().next().getWorld();
            if (w != chunk.getWorld()) throw new IllegalArgumentException("New chunks must be in the same world as previous chunks!");
        } else {
            claim = true;
        }
        boolean ret = true;
        for (Chunk other : chunks) {
            if (other.getX() != chunk.getX()) continue;
            if (other.getZ() != chunk.getZ()) continue;
            if (!other.getWorld().getName().equalsIgnoreCase(chunk.getWorld().getName())) continue;
            ret = false;
        }
        if (ret) ret = chunks.add(chunk);
        if (ret) {
            generateBounds();
            if (manageHandlers) {
                for (Claim c : registry) {
                    if (c == this) continue;
                    if (c.chunks.contains(chunk)) c.removeChunk(chunk);
                }
            } else if (claim) {
                claim();
            }
        }
        return ret;
    }

    public boolean removeChunk(@NotNull Chunk chunk) {
        boolean ret = chunks.remove(chunk);
        if (!ret) {
            Set<Chunk> toRemove = new HashSet<>();
            for (Chunk chk : chunks) {
                if (chk.getX() != chunk.getX()) continue;
                if (chk.getZ() != chunk.getZ()) continue;
                if (chk.getWorld().getName().equals(chunk.getWorld().getName()))  toRemove.add(chk);
            }
            ret = chunks.removeAll(toRemove);
        }
        if (ret) {
            generateBounds();
            if (manageHandlers && chunks.size() < 1) unclaim();
        }
        return ret;
    }

    public boolean contains(@NotNull Location location) {
        org.bukkit.util.Vector vector = location.toVector();
        if (!outerBounds.contains(vector)) return false;
        boolean first = true;
        for (Chunk c : getChunks()) {
            if (first) {
                if (c.getWorld() != location.getWorld()) return false;
            }
            first = false;
            if (getChunkBounds(c).contains(vector)) return true;
        }
        return false;
    }

    public void setPermission(@NotNull Permission permission, @NotNull TrustLevel trustLevel) {
        globalPerms.put(permission, trustLevel);
        updateHandlers();
    }

    public @NotNull TrustLevel getPermission(@NotNull Permission permission) {
        return globalPerms.getOrDefault(permission, permission.getDefaultTrust());
    }

    public @NotNull @UnmodifiableView Map<Permission, TrustLevel> getPermissions() {
        Map<Permission, TrustLevel> ret = new HashMap<>();
        for (Permission p : Permission.values()) {
            ret.put(p, globalPerms.getOrDefault(p, p.getDefaultTrust()));
        }
        return Collections.unmodifiableMap(ret);
    }

    public void setUserPermission(@NotNull OfflinePlayer player, @NotNull Permission permission, boolean value) {
        UUID uuid = player.getUniqueId();
        EnumSet<Permission> set = Objects.requireNonNullElseGet(playerPerms.get(uuid), () -> EnumSet.noneOf(Permission.class));
        if (value) {
            set.add(permission);
            playerPerms.put(uuid, set);
        } else {
            set.remove(permission);
            if (set.size() > 0) {
                playerPerms.put(uuid, set);
            } else {
                playerPerms.remove(uuid);
            }
        }
        updateHandlers();
    }

    public boolean getUserPermission(@NotNull OfflinePlayer player, @NotNull Permission permission) {
        UUID uuid = player.getUniqueId();
        EnumSet<Permission> set = playerPerms.get(uuid);
        if (set == null) return false;
        return set.contains(permission);
    }

    public @NotNull @UnmodifiableView Map<XCPlayer, EnumSet<Permission>> getUserPermissions() {
        Map<XCPlayer, EnumSet<Permission>> ret = new HashMap<>();
        for (UUID uuid : playerPerms.keySet()) {
            OfflinePlayer ply = Bukkit.getOfflinePlayer(uuid);
            ret.put(XCPlayer.of(ply), playerPerms.get(uuid));
        }
        return Collections.unmodifiableMap(ret);
    }

    public void clearUserPermissions(@NotNull OfflinePlayer player) {
        playerPerms.remove(player.getUniqueId());
    }

    public boolean hasPermission(@NotNull OfflinePlayer player, @NotNull Permission permission) {
        if (XClaim.mainConfig.getBoolean("exempt-claim-owner-from-permission-rules", true)) {
            if (player.equals(owner)) return true;
        }
        Player online = player.getPlayer();
        if (online != null) {
            if (online.hasPermission("xclaim.admin")) return true;
        }
        EnumSet<Permission> set = playerPerms.get(player.getUniqueId());
        if (set != null) {
            if (set.contains(permission)) return true;
        }
        TrustLevel tl = globalPerms.getOrDefault(permission, permission.getDefaultTrust());
        switch (tl) {
            case VETERANS -> {
                long firstLogin = player.getFirstPlayed();
                if (firstLogin == 0L) return false;
                long duration = (long) Math.floor((System.currentTimeMillis() - firstLogin) / 1000d);
                long required = XClaim.mainConfig.getLong("veteran-time", 604800L);
                return (duration >= required);
            }
            case TRUSTED -> {
                return owner.playerTrusted(player);
            }
            case ALL -> {
                return true;
            }
        }
        return false;
    }

    public void serialize(@NotNull ConfigurationSection section) {
        section.set("name", name);
        section.set("owner", owner.getOfflinePlayer().getUniqueId().toString());
        section.set("world", "");
        ConfigurationSection sec;
        sec = section.getConfigurationSection("chunks");
        if (sec == null) sec = section.createSection("chunks");
        Iterator<Chunk> iterator = chunks.iterator();
        for (int i=0; i < chunks.size(); i++) {
            Chunk chunk = iterator.next();
            String key = Integer.toString(i);
            ConfigurationSection sc = sec.getConfigurationSection(key);
            if (sc == null) sc = sec.createSection(key);
            sc.set("x", chunk.getX());
            sc.set("z", chunk.getZ());
            if (i == 0) section.set("world", chunk.getWorld().getName());
        }
        sec = section.getConfigurationSection("permissions");
        if (sec == null) sec = section.createSection("permissions");
        for (Map.Entry<Permission, TrustLevel> entry : globalPerms.entrySet()) {
            sec.set(entry.getKey().name(), entry.getValue().name());
        }
        sec = section.getConfigurationSection("users");
        if (sec == null) sec = section.createSection("users");
        for (Map.Entry<UUID, EnumSet<Permission>> entry : playerPerms.entrySet()) {
            List<String> list = entry.getValue().stream().flatMap((Permission p) -> Stream.of(p.name())).toList();
            sec.set(entry.getKey().toString(), list);
        }
    }

    private final Map<Permission, PermissionHandler> handlers = new HashMap<>();

    /**
     * Called when permissions on this claim change and handlers should be adjusted.
     */
    private void updateHandlers() {
        if (!manageHandlers) return;
        EnumSet<Permission> inUse = EnumSet.noneOf(Permission.class);
        for (Permission p : Permission.values()) {
            if (Objects.equals(globalPerms.get(p), TrustLevel.ALL)) continue;
            inUse.add(p);
        }
        for (EnumSet<Permission> set : playerPerms.values()) inUse.addAll(set);
        //
        for (Permission key : handlers.keySet().toArray(Permission[]::new)) {
            if (inUse.contains(key)) {
                inUse.remove(key);
            } else {
                handlers.remove(key).unregister();
            }
        }
        for (Permission p : inUse) {
            if (!p.hasHandler()) continue;
            PermissionHandler handler = p.createHandler(this);
            handler.register();
            handlers.put(p, handler);
        }
    }

    private void removeHandlers() {
        for (PermissionHandler handler : handlers.values()) handler.unregister();
        handlers.clear();
    }

    /**
     * Makes this claim canonical and starts enforcing the claim's permissions.
     * @return If false, this claim was already canonical.
     */
    public boolean claim() {
        for (Claim c : registry) {
            if (c == this) continue;
            if (c.name.equals(name)) {
                c.unclaim();
                continue;
            }
            for (Chunk chunk : c.chunks.toArray(Chunk[]::new)) {
                if (chunks.contains(chunk)) c.removeChunk(chunk);
            }
        }
        manageHandlers = true;
        updateHandlers();
        return registry.add(this);
    }

    /**
     * Makes this claim non-canonical and stops enforcing the claim's permissions.
     * @return If false, this claim was already non-canonical.
     */
    public boolean unclaim() {
        if (registry.remove(this)) {
            manageHandlers = false;
            removeHandlers();
            return true;
        }
        return false;
    }

}
