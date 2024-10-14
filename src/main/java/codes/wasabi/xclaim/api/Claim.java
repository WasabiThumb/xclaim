package codes.wasabi.xclaim.api;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.TrustLevel;
import codes.wasabi.xclaim.api.enums.permission.PermissionHandler;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.map.MapService;
import codes.wasabi.xclaim.map.MapServiceOp;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.platform.PlatformPersistentDataContainer;
import codes.wasabi.xclaim.platform.PlatformPersistentDataType;
import codes.wasabi.xclaim.util.BoundingBox;
import codes.wasabi.xclaim.util.ChunkReference;
import codes.wasabi.xclaim.util.StringUtil;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
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

    private static final Set<RegistryEntry> registry = new HashSet<>();
    private static final ReentrantReadWriteLock registryLock = new ReentrantReadWriteLock();

    public static @NotNull @UnmodifiableView Set<Claim> getAll() {
        Set<Claim> ret;
        registryLock.readLock().lock();
        try {
            ret = new HashSet<>(registry.size());
            for (RegistryEntry entry : registry) ret.add(entry.claim());
        } finally {
            registryLock.readLock().unlock();
        }
        return Collections.unmodifiableSet(ret);
    }

    /**
     * Returns true if a claim with the specified name exists in the registry in a manner more efficient than
     * {@link #getByName(String)}.
     */
    public static boolean exists(@NotNull CharSequence name) {
        registryLock.readLock().lock();
        try {
            return registry.contains(RegistryEntry.fake(name));
        } finally {
            registryLock.readLock().unlock();
        }
    }

    public static @Nullable Claim getByName(@NotNull String name, boolean ignoreCase) {
        registryLock.readLock().lock();
        try {
            if (!registry.contains(RegistryEntry.fake(name))) return null;
            for (RegistryEntry re : registry) {
                if (re.claimNameEquals(name, ignoreCase)) {
                    return re.claim();
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
            for (RegistryEntry re : registry) {
                if (re.claim().owner.getUniqueId().equals(owner.getUniqueId())) set.add(re.claim());
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
        return getByChunk(ChunkReference.ofChunk(chunk));
    }

    public static @Nullable Claim getByChunk(@NotNull ChunkReference cr) {
        registryLock.readLock().lock();
        try {
            for (RegistryEntry re : registry) {
                for (ChunkReference chk : re.claim().chunks) {
                    if (chk.matches(cr)) return re.claim();
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
            ms.queueOperation(MapServiceOp.update(this));
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
                if (!registry.contains(RegistryEntry.fake(targetName))) break;
                boolean ok = true;
                Claim c;
                for (RegistryEntry re : registry) {
                    c = re.claim();
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
                List<Claim> collides = new ArrayList<>(1);
                registryLock.readLock().lock();
                try {
                    Claim c;
                    for (RegistryEntry re : registry) {
                        c = re.claim();
                        if (c == this) continue;
                        if (c.chunks.contains(ref)) {
                            collides.add(c);
                        }
                    }
                } finally {
                    registryLock.readLock().unlock();
                }
                for (Claim collided : collides) collided.removeChunk(ref);
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

    public boolean containsChunk(@NotNull ChunkReference cr) {
        return this.chunks.contains(cr);
    }

    public long minSquareDistance(@NotNull ChunkReference cr) {
        if (this.chunks.contains(cr)) return 0L;
        long ret = Long.MAX_VALUE;
        long dist;
        long tmp;
        synchronized (this.chunks) {
            for (ChunkReference mcr : this.chunks) {
                if (!mcr.world.getUID().equals(cr.world.getUID())) continue;
                tmp = mcr.x - cr.x;
                dist = (tmp * tmp);
                tmp = mcr.z - cr.z;
                dist += (tmp * tmp);
                if (dist < ret) {
                    ret = dist;
                    if (dist == 0L) break;
                }
            }
        }
        return ret;
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
        if (XClaim.mainConfig.rules().exemptOwner()) {
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
                long required = XClaim.mainConfig.veteranTime();
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
        Claim c;
        for (RegistryEntry re : new HashSet<>(registry)) {
            c = re.claim();
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
            added = registry.add(RegistryEntry.of(this));
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
            removed = registry.remove(RegistryEntry.of(this));
        } finally {
            registryLock.writeLock().unlock();
        }

        if (removed) {
            removeHandlers();
            ownerChangeCallbacks.clear();
            if (MapService.isAvailable()) {
                MapService ms = MapService.getNonNull();
                ms.queueOperation(MapServiceOp.delete(this));
            }
            return true;
        }
        return false;
    }

    public boolean isCanonical() {
        registryLock.readLock().lock();
        try {
            return registry.contains(RegistryEntry.of(this));
        } finally {
            registryLock.readLock().unlock();
        }
    }

    @Override
    public int hashCode() {
        return RegistryEntry.of(this).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Claim) {
            return RegistryEntry.of(this).equals(RegistryEntry.of((Claim) obj));
        }
        return super.equals(obj);
    }

    // 1.15 : Efficient registry membership test by name

    private interface RegistryEntry {

        static @NotNull RegistryEntry of(@NotNull Claim claim) {
            return new RegistryEntry.Real(claim);
        }

        static @NotNull RegistryEntry fake(@NotNull CharSequence string) {
            return new RegistryEntry.Fake(string);
        }

        //

        @NotNull CharSequence claimName();

        boolean claimNameEquals(@NotNull CharSequence other, boolean ignoreCase);

        default boolean claimNameEquals(@NotNull CharSequence other) {
            return this.claimNameEquals(other, true);
        }

        @NotNull Claim claim() throws UnsupportedOperationException;

        //

        abstract class Abstract implements RegistryEntry {

            @Override
            public boolean claimNameEquals(final @NotNull CharSequence b, boolean ignoreCase) {
                final CharSequence a = this.claimName();
                final int al = a.length();
                if (al == 0) return false;
                final int bl = b.length();
                if (al != bl) return false;

                char ca;
                char cb;
                for (int i=0; i < al; i++) {
                    ca = a.charAt(i);
                    if (ignoreCase) ca = Character.toLowerCase(ca);
                    cb = b.charAt(i);
                    if (ignoreCase) cb = Character.toLowerCase(cb);

                    if (ca != cb) return false;
                }
                return true;
            }

            @Override
            public int hashCode() {
                final PrimitiveIterator.OfInt name = this.claimName().codePoints().iterator();
                int hash = 7;
                while (name.hasNext()) {
                    hash = 31 * hash + Character.toLowerCase(name.nextInt());
                }
                return hash;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null) return false;
                if (obj instanceof RegistryEntry) {
                    if (this.claimNameEquals(((RegistryEntry) obj).claimName()))
                        return true;
                }
                return super.equals(obj);
            }

        }

        final class Real extends Abstract {

            private final Claim value;
            Real(@NotNull Claim value) {
                this.value = value;
            }

            @Override
            public @NotNull String claimName() {
                return this.value.name;
            }

            @Override
            public @NotNull Claim claim() {
                return this.value;
            }

        }

        final class Fake extends Abstract {

            private final CharSequence name;
            Fake(@NotNull CharSequence name) {
                this.name = name;
            }

            @Override
            public @NotNull CharSequence claimName() {
                return this.name;
            }

            @Contract(" -> fail")
            @Override
            public @NotNull Claim claim() {
                throw new UnsupportedOperationException();
            }

        }

    }

}
