package codes.wasabi.xclaim.api;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.TrustLevel;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.map.MapMarker;
import codes.wasabi.xclaim.map.MapService;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformPersistentDataContainer;
import codes.wasabi.xclaim.platform.PlatformPersistentDataType;
import codes.wasabi.xclaim.util.BoundingBox;
import codes.wasabi.xclaim.util.ChunkReference;
import codes.wasabi.xclaim.util.StringUtil;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Claim {

    private static final Set<Claim> registry = new HashSet<>();
    private static final ReentrantReadWriteLock registryLock = new ReentrantReadWriteLock();

    public static @NotNull @UnmodifiableView Set<Claim> getAll() {
        Set<Claim> ret = new HashSet<>();
        registryLock.readLock().lock();
        try {
            ret.addAll(registry);
        } finally {
            registryLock.readLock().unlock();
        }
        return Collections.unmodifiableSet(ret);
    }

    public static @Nullable Claim getByName(@NotNull String name, boolean ignoreCase) {
        registryLock.readLock().lock();
        try {
            for (Claim c : registry) {
                if (ignoreCase ? c.getName().equalsIgnoreCase(name) : c.getName().equals(name)) {
                    return c;
                }
            }
            return null;
        } finally {
            registryLock.readLock().unlock();
        }
    }

    public static @Nullable Claim getByName(@NotNull String name) {
        return getByName(name, true);
    }

    public static @NotNull @UnmodifiableView Set<Claim> getByOwner(@NotNull XCPlayer owner) {
        registryLock.readLock().lock();
        try {
            Set<Claim> set = new HashSet<>();
            for (Claim c : registry) {
                if (c.owner.getUniqueId().equals(owner.getUniqueId())) set.add(c);
            }
            return Collections.unmodifiableSet(set);
        } finally {
            registryLock.readLock().unlock();
        }
    }

    public static @NotNull @UnmodifiableView Set<Claim> getByOwner(@NotNull OfflinePlayer owner) {
        return getByOwner(XCPlayer.of(owner));
    }

    public static @Nullable Claim getByChunk(@NotNull Chunk chunk) {
        registryLock.readLock().lock();
        try {
            for (Claim c : registry) {
                for (ChunkReference chk : c.chunks) {
                    if (chk.matches(chunk)) return c;
                }
            }
            return null;
        } finally {
            registryLock.readLock().unlock();
        }
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
        Set<ChunkReference> chunks = new HashSet<>();
        ConfigurationSection sec;
        sec = section.getConfigurationSection("chunks");
        if (sec == null) throw new IllegalArgumentException("Missing property \"chunks\"");
        for (String key : sec.getKeys(false)) {
            ConfigurationSection sc = sec.getConfigurationSection(key);
            if (sc == null) throw new IllegalArgumentException("Chunk with ID " + key + " is malformed!");
            int x = sc.getInt("x");
            int z = sc.getInt("z");
            chunks.add(new ChunkReference(world, x, z));
        }
        Map<Permission, TrustLevel> global = new HashMap<>();
        sec = section.getConfigurationSection("permissions");
        if (sec == null) throw new IllegalArgumentException("Missing property \"permissions\"");
        for (String key : sec.getKeys(false)) {
            Permission perm;
            try {
                perm = Permission.fromName(key);
                if (perm == null) continue;
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
                if (ob instanceof String) {
                    String str = (String) ob;
                    Permission perm;
                    try {
                        perm = Permission.fromName(str);
                        if (perm == null) continue;
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
        Claim ret = new Claim(name, chunks, XCPlayer.of(owner), global, players, -1);
        if (section.contains("graceStart")) {
            ret.graceStart = section.getLong("graceStart", -1);
        }
        return ret;
    }

    private String name;
    private final Set<ChunkReference> chunks;
    private XCPlayer owner;
    private final Map<Permission, TrustLevel> globalPerms;
    private final Map<UUID, EnumSet<Permission>> playerPerms;
    private final List<java.util.function.Consumer<Claim>> ownerChangeCallbacks = Collections.synchronizedList(new ArrayList<>());
    private BoundingBox outerBounds;
    private boolean manageHandlers = false;
    private long graceStart = -1;

    private void validateMarkers() {
        if (MapService.isAvailable()) {
            MapService ms = MapService.getNonNull();
            ms.getMarkerAsync(this, (MapMarker marker) -> {
                if (marker != null) marker.update(this);
            });
        }
    }

    private void generateBounds() {
        BoundingBox bb = null;
        boolean set = false;
        for (ChunkReference c : chunks) {
            BoundingBox bounds = c.getBounds();
            if (!set) {
                bb = bounds;
                set = true;
            } else {
                bb.union(bounds);
            }
        }
        outerBounds = (set ? bb : new BoundingBox());
        validateMarkers();
    }

    public @NotNull BoundingBox getOuterBounds() {
        return outerBounds;
    }

    Claim(@NotNull String name, @NotNull Set<ChunkReference> chunks, @NotNull XCPlayer owner, @NotNull Map<Permission, TrustLevel> globalPerms, @NotNull Map<UUID, EnumSet<Permission>> playerPerms, int dummy) {
        this.name = name;
        this.chunks = Collections.synchronizedSet(new HashSet<>(chunks));
        this.owner = owner;
        this.globalPerms = Collections.synchronizedMap(new HashMap<>(globalPerms));
        this.playerPerms = Collections.synchronizedMap(new HashMap<>(playerPerms));
        this.nameRepeatCheck();
        generateBounds();
    }

    Claim(@NotNull String name, @NotNull Set<Chunk> chunks, @NotNull XCPlayer owner, @NotNull Map<Permission, TrustLevel> globalPerms, @NotNull Map<UUID, EnumSet<Permission>> playerPerms) {
        this(name, chunks.stream().map(ChunkReference::ofChunk).collect(Collectors.toSet()), owner, globalPerms, playerPerms, -1);
    }

    public Claim(@NotNull String name, @NotNull Set<Chunk> chunks, @NotNull XCPlayer owner) {
        this(name, chunks, owner, Collections.emptyMap(), Collections.emptyMap());
    }

    public Claim(@NotNull String name, @NotNull Set<Chunk> chunks, @NotNull OfflinePlayer owner) {
        this(name, chunks, XCPlayer.of(owner), Collections.emptyMap(), Collections.emptyMap());
    }

    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        for (Player ply : Bukkit.getOnlinePlayers()) {
            if (Objects.equals(ChunkEditor.getEditing(ply), this)) {
                PlatformPersistentDataContainer pdc = Platform.get().getPersistentDataContainer(ply);
                pdc.set(ChunkEditor.getNameKey(), PlatformPersistentDataType.STRING, name);
            }
        }
        this.name = name;
        this.nameRepeatCheck();
    }

    public XCPlayer getOwner() {
        return owner;
    }

    public void setOwner(@NotNull OfflinePlayer op) {
        this.owner = XCPlayer.of(op);
        this.nameRepeatCheck();
        ownerChangeCallbacks.forEach((java.util.function.Consumer<Claim> consumer) -> consumer.accept(this));
    }

    private void nameRepeatCheck() {
        if (this.owner == null) return;
        String desiredName = this.name;
        String targetName = this.name;
        int i = 0;
        UUID ownerId = this.owner.getUniqueId();
        registryLock.readLock().lock();
        try {
            while (true) {
                boolean ok = true;
                for (Claim c : registry) {
                    if (c == this) continue;
                    if (c.owner == null || c.owner.getUniqueId() != ownerId) continue;
                    if (c.name.equalsIgnoreCase(targetName)) {
                        i++;
                        targetName = desiredName + " (" + i + ")";
                        ok = false;
                        break;
                    }
                }
                if (ok) break;
            }
        } finally {
            registryLock.readLock().unlock();
        }
        this.name = targetName;
    }

    public @NotNull String getUniqueToken() {
        byte[] nameBytes = this.name.getBytes(StandardCharsets.UTF_8);
        UUID uuid = this.owner.getUniqueId();
        ByteBuffer digestBuffer = ByteBuffer.allocate(nameBytes.length + (Long.BYTES * 2));
        digestBuffer.put(nameBytes);
        digestBuffer.putLong(uuid.getMostSignificantBits());
        digestBuffer.putLong(uuid.getLeastSignificantBits());
        byte[] digest;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            digest = md.digest(digestBuffer.array());
        } catch (NoSuchAlgorithmException e) {
            digest = digestBuffer.array();
        }
        return StringUtil.bytesToHex(digest);
    }

    public void onOwnerChanged(@NotNull java.util.function.Consumer<Claim> callback) {
        ownerChangeCallbacks.add(callback);
    }

    public @NotNull @UnmodifiableView Set<ChunkReference> getChunks() {
        return Collections.unmodifiableSet(chunks);
    }

    public @Nullable World getWorld() {
        if (chunks.size() > 0) {
            return chunks.iterator().next().world;
        }
        return null;
    }

    public long getGraceStart() {
        return graceStart;
    }

    public void setGraceStart(long graceStart) {
        this.graceStart = graceStart;
    }

    public boolean addChunk(@NotNull Chunk chunk) throws IllegalArgumentException {
        boolean claim = false;
        if (chunks.size() > 0) {
            World w = chunks.iterator().next().world;
            if (w != chunk.getWorld()) throw new IllegalArgumentException("New chunks must be in the same world as previous chunks!");
        } else {
            claim = true;
        }
        boolean ret = true;
        for (ChunkReference other : chunks) {
            if (other.matches(chunk)) {
                ret = false;
                break;
            }
        }
        ChunkReference ref = ChunkReference.ofChunk(chunk);
        if (ret) ret = chunks.add(ref);
        if (ret) {
            generateBounds();
            if (manageHandlers) {
                registryLock.readLock().lock();
                try {
                    for (Claim c : registry) {
                        if (c == this) continue;
                        if (c.chunks.contains(ref)) c.removeChunk(chunk);
                    }
                } finally {
                    registryLock.readLock().unlock();
                }
            } else if (claim) {
                claim();
            }
        }
        return ret;
    }

    public boolean removeChunk(@NotNull Chunk chunk) {
         return this.removeChunk(ChunkReference.ofChunk(chunk));
    }

    public boolean removeChunk(@NotNull ChunkReference ref) {
        boolean ret = chunks.remove(ref);
        if (!ret) {
            Set<ChunkReference> toRemove = new HashSet<>();
            for (ChunkReference chk : chunks) {
                if (chk.equals(ref)) toRemove.add(chk);
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
        for (ChunkReference c : getChunks()) {
            if (first) {
                if (c.world != location.getWorld()) return false;
            }
            first = false;
            if (c.getBounds().contains(vector)) return true;
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
        EnumSet<Permission> set = playerPerms.get(uuid);
        if (set == null) set = EnumSet.noneOf(Permission.class);
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
            if (player.getUniqueId().equals(owner.getUniqueId())) return true;
        }
        if (player.isOp()) return true;
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
            case VETERANS:
                long firstLogin = player.getFirstPlayed();
                if (firstLogin == 0L) return false;
                long duration = (long) Math.floor((System.currentTimeMillis() - firstLogin) / 1000d);
                long required = XClaim.mainConfig.getLong("veteran-time", 604800L);
                return (duration >= required);
            case TRUSTED:
                return owner.playerTrusted(player);
            case ALL:
                return true;
        }
        return false;
    }

    public void serialize(@NotNull ConfigurationSection section) {
        section.set("name", name);
        section.set("owner", owner.getUniqueId().toString());
        section.set("world", "");
        ConfigurationSection sec;
        sec = section.getConfigurationSection("chunks");
        if (sec == null) sec = section.createSection("chunks");
        Iterator<ChunkReference> iterator = chunks.iterator();
        for (int i=0; i < chunks.size(); i++) {
            ChunkReference chunk = iterator.next();
            String key = Integer.toString(i);
            ConfigurationSection sc = sec.getConfigurationSection(key);
            if (sc == null) sc = sec.createSection(key);
            sc.set("x", chunk.x);
            sc.set("z", chunk.z);
            if (i == 0) section.set("world", chunk.world.getName());
        }
        sec = section.getConfigurationSection("permissions");
        if (sec == null) sec = section.createSection("permissions");
        for (Map.Entry<Permission, TrustLevel> entry : globalPerms.entrySet()) {
            sec.set(entry.getKey().name(), entry.getValue().name());
        }
        sec = section.getConfigurationSection("users");
        if (sec == null) sec = section.createSection("users");
        for (Map.Entry<UUID, EnumSet<Permission>> entry : playerPerms.entrySet()) {
            List<String> list = entry.getValue().stream().flatMap((Permission p) -> Stream.of(p.name())).collect(Collectors.toList());
            sec.set(entry.getKey().toString(), list);
        }
        if (graceStart > 0) {
            section.set("graceStart", graceStart);
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
        for (Object key : handlers.keySet().toArray()) {
            Permission perm = (Permission) key;
            if (inUse.contains(perm)) {
                inUse.remove(perm);
            } else {
                handlers.remove(perm).unregister();
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
        // prevent ConcurrentModificationException
        String myToken = this.getUniqueToken();
        for (Claim c : new HashSet<>(registry)) {
            if (c == this) continue;
            if (c.getUniqueToken().equals(myToken)) {
                c.unclaim();
                continue;
            }
            for (Object object : c.chunks.toArray()) {
                ChunkReference chunk = (ChunkReference) object;
                if (chunks.contains(chunk)) c.removeChunk(chunk);
            }
        }
        manageHandlers = true;

        boolean added;
        registryLock.writeLock().lock();
        try {
            added = registry.add(this);
        } finally {
            registryLock.writeLock().unlock();
        }
        if (added) {
            updateHandlers();
            validateMarkers();
            return true;
        }
        return false;
    }

    /**
     * Makes this claim non-canonical and stops enforcing the claim's permissions.
     * @return If false, this claim was already non-canonical.
     */
    public boolean unclaim() {
        manageHandlers = false;

        boolean removed;
        registryLock.writeLock().lock();
        try {
            removed = registry.remove(this);
        } finally {
            registryLock.writeLock().unlock();
        }

        if (removed) {
            removeHandlers();
            ownerChangeCallbacks.clear();
            if (MapService.isAvailable()) {
                MapService ms = MapService.getNonNull();
                ms.getMarkerAsync(this, (MapMarker marker) -> {
                    if (marker != null) marker.deleteMarker();
                });
            }
            return true;
        }
        return false;
    }

}
