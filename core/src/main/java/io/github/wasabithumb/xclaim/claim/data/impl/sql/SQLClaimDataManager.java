package io.github.wasabithumb.xclaim.claim.data.impl.sql;

import io.github.wasabithumb.xclaim.claim.flags.ClaimFlags;
import io.github.wasabithumb.xclaim.claim.permission.Permission;
import io.github.wasabithumb.xclaim.claim.permission.PermissionMap;
import io.github.wasabithumb.xclaim.claim.permission.TrustLevel;
import io.github.wasabithumb.xclaim.claim.data.ClaimData;
import io.github.wasabithumb.xclaim.claim.data.ClaimDataManager;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import io.github.wasabithumb.xclaim.util.BitManipulation;
import io.github.wasabithumb.xclaim.util.SQLHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public abstract class SQLClaimDataManager extends SQLHelper<SQLClaimDataManager.Context> implements ClaimDataManager {

    protected SQLClaimDataManager(@NotNull Logger logger) {
        super(logger);
    }

    @Override
    protected @NotNull SQLClaimDataManager.Context newStatements() {
        return new Context();
    }

    //

    @Override
    public @NotNull Set<ClaimData.Token> keys() {
        return this.use((Context ctx) -> {
            Set<ClaimData.Token> ret = new HashSet<>();
            ResultSet rs = ctx.psInfoSelectTokens.executeQuery();
            while (rs.next()) {
                ret.add(new ClaimData.Token.Int(rs.getInt(1)));
            }
            return Collections.unmodifiableSet(ret);
        }, Collections::emptySet);
    }

    @Override
    public @NotNull ClaimData create(@NotNull String name, @NotNull UUID owner, @NotNull PlatformWorld world) {
        Integer token = this.use((Context ctx) -> {
            ctx.psInfoInsert.setString(1, name);
            ctx.psInfoInsert.setString(2, owner.toString());
            ctx.psInfoInsert.setString(3, world.uuid().toString());
            ctx.psInfoInsert.setString(4, "");
            ctx.psInfoInsert.execute();

            ctx.psInfoSelectByName.setString(1, name);
            ResultSet rs = ctx.psInfoSelectByName.executeQuery();
            if (!rs.next()) return null;
            return rs.getInt(1);
        }, () -> null);
        if (token == null) throw new IllegalArgumentException("Claim with name \"" + name + "\" already exists");
        ClaimData cd = this.load0(token);
        if (cd == null) throw new AssertionError("Claim was successfuly created, but could not be loaded");
        return cd;
    }

    @Override
    public @Nullable ClaimData load(@NotNull ClaimData.Token key) {
        return this.load0(key.asInt());
    }

    private @Nullable ClaimData load0(final int key) {
        return this.use((Context ctx) -> this.load00(ctx, key), () -> null);
    }

    private @Nullable ClaimData load00(@NotNull Context ctx, int key) throws SQLException {
        ResultSet rs;
        ResultSetMetaData rsm;

        ctx.psInfoSelectByToken.setInt(1, key);
        rs = ctx.psInfoSelectByToken.executeQuery();
        if (!rs.next()) return null;

        // Data
        ClaimData.Builder builder = ClaimData.builder()
                .token(key)
                .name(rs.getString(1))
                .owner(UUID.fromString(rs.getString(2)))
                .world(UUID.fromString(rs.getString(3)))
                .flags(ClaimFlags.fromString(rs.getString(4)));

        // Chunks
        ctx.psChunksSelect.setInt(1, key);
        rs = ctx.psChunksSelect.executeQuery();
        int x, z;
        while (rs.next()) {
            x = rs.getInt(1);
            z = rs.getInt(2);
            builder.addChunk(BitManipulation.i32i64(x, z));
        }

        // Global Permissions
        ctx.psGlobalPermissionsSelect.setInt(1, key);
        rs = ctx.psGlobalPermissionsSelect.executeQuery();
        if (rs.next()) {
            rsm = rs.getMetaData();
            String name;
            for (int i=1; i <= rsm.getColumnCount(); i++) {
                name = rsm.getColumnName(i);
                if (name.equals("token")) continue;
                Permission perm;
                try {
                    perm = Permission.fromSQLName(name);
                } catch (IllegalArgumentException ignored) {
                    continue;
                }
                int value = rs.getInt(i);
                TrustLevel tl = perm.defaultTrust();
                try {
                    if (value != 255)
                        tl = TrustLevel.fromOrdinal(value);
                } catch (IllegalArgumentException ignored) { }
                builder.setGlobalPermission(perm, tl);
            }
        }

        // User Permissions
        ctx.psUserPermissionsSelectAll.setInt(1, key);
        rs = ctx.psUserPermissionsSelectAll.executeQuery();
        final int userIndex = rs.findColumn("user");
        while (rs.next()) {
            UUID user = UUID.fromString(rs.getString(userIndex));
            rsm = rs.getMetaData();
            String name;
            for (int i=1; i <= rsm.getColumnCount(); i++) {
                if (i == userIndex) continue;
                name = rsm.getColumnName(i);
                if (name.equals("token")) continue;
                Permission perm;
                try {
                    perm = Permission.fromSQLName(name);
                } catch (IllegalArgumentException ignored) {
                    continue;
                }
                boolean value = rs.getInt(i) != 0;
                if (value) builder.setUserPermission(user, perm);
            }
        }

        return builder.build();
    }

    @Override
    public void queueSync(@NotNull ClaimData data) {
        this.submit((Context ctx) -> {
            data.startSync();
            try {
                this.doSync(ctx, data);
            } finally {
                data.endSync();
            }
        });
    }

    @Override
    public void queueDrop(@NotNull ClaimData data) {
        this.submit((Context ctx) -> this.doDrop(ctx, data));
    }

    private void doSync(@NotNull Context ctx, @NotNull ClaimData data) throws SQLException {
        if (data.didUpdateName() || data.didUpdateOwner() || data.didUpdateFlags())
            this.doSyncNameOwnerFlags(ctx, data);
        if (data.didUpdateChunks())
            this.doSyncChunks(ctx, data);
        if (data.didUpdateGlobalPermissions())
            this.doSyncGlobalPermissions(ctx, data);
        if (data.didUpdateUserPermissions())
            this.doSyncUserPermissions(ctx, data);
    }

    private void doSyncNameOwnerFlags(@NotNull Context ctx, @NotNull ClaimData data) throws SQLException {
        ctx.psInfoUpdate.setString(1, data.getName());
        ctx.psInfoUpdate.setString(2, data.getOwner().toString());
        ctx.psInfoUpdate.setInt(3, data.getToken().asInt());
        ctx.psInfoUpdate.setString(4, data.getFlagsAsString());
        ctx.psInfoUpdate.execute();
    }

    private void doSyncChunks(@NotNull Context ctx, @NotNull ClaimData data) throws SQLException {
        Set<Long> add = data.getChunks();
        Set<Long> cur = new HashSet<>(add.size());
        ResultSet rs;
        int[] tmp;

        ctx.psChunksSelect.setInt(1, data.getToken().asInt());
        rs = ctx.psChunksSelect.executeQuery();
        while (rs.next()) cur.add(BitManipulation.i32i64(rs.getInt(1), rs.getInt(2)));

        ctx.psChunksInsert.setInt(1, data.getToken().asInt());
        for (Long chunk : add) {
            if (cur.contains(chunk)) continue;
            tmp = BitManipulation.i64i32(chunk);
            ctx.psChunksInsert.setInt(2, tmp[0]);
            ctx.psChunksInsert.setInt(3, tmp[1]);
            ctx.psChunksInsert.execute();
        }

        ctx.psChunksDeleteSingle.setInt(1, data.getToken().asInt());
        for (Long chunk : cur) {
            if (add.contains(chunk)) continue;
            tmp = BitManipulation.i64i32(chunk);
            ctx.psChunksDeleteSingle.setInt(2, tmp[0]);
            ctx.psChunksDeleteSingle.setInt(3, tmp[1]);
            ctx.psChunksDeleteSingle.execute();
        }
    }

    private void doSyncGlobalPermissions(@NotNull Context ctx, @NotNull ClaimData data) throws SQLException {
        TrustLevel tl;
        PreparedStatement ps;
        for (Permission p : Permission.values()) {
            tl = data.getGlobalPermission(p);
            ps = ctx.psGlobalPermissionsUpsert.get(p);

            ps.setInt(1, data.getToken().asInt());
            ps.setInt(2, tl.ordinal());
            ps.setInt(3, tl.ordinal());
            ps.execute();
        }
    }

    private void doSyncUserPermissions(@NotNull Context ctx, @NotNull ClaimData data) throws SQLException {
        Map<UUID, Set<Permission>> add = data.getUserPermissions();
        Set<UUID> has = new HashSet<>(add.size());

        ctx.psUserPermissionsSelectAll.setInt(1, data.getToken().asInt());
        ResultSet rs = ctx.psUserPermissionsSelectAll.executeQuery();
        final int userIndex = rs.findColumn("user");
        while (rs.next()) {
            String uuidString = rs.getString(userIndex);
            UUID uuid = UUID.fromString(uuidString);
            has.add(uuid);
            if (!add.containsKey(uuid)) continue;

            Set<Permission> perms = add.get(uuid);
            for (Permission p : Permission.values()) {
                PreparedStatement ps = ctx.psUserPermissionsUpdate.get(p);
                ps.setInt(1, perms.contains(p) ? 1 : 0);
                ps.setInt(2, data.getToken().asInt());
                ps.setString(3, uuidString);
            }
        }

        for (UUID key : add.keySet()) {
            if (has.contains(key)) continue;
            Iterator<Permission> perms = add.get(key).iterator();
            Permission next;
            PreparedStatement ps;
            String keyStr = key.toString();

            if (!perms.hasNext()) continue;
            next = perms.next();

            ps = ctx.psUserPermissionsInsert.get(next);
            ps.setInt(1, data.getToken().asInt());
            ps.setString(2, keyStr);
            ps.setInt(1, 1);

            while (perms.hasNext()) {
                next = perms.next();

                ps = ctx.psUserPermissionsUpdate.get(next);
                ps.setInt(1, 1);
                ps.setInt(2, data.getToken().asInt());
                ps.setString(3, keyStr);
                ps.execute();
            }
        }

        ctx.psUserPermissionsDeleteSingle.setInt(1, data.getToken().asInt());
        for (UUID key : has) {
            if (add.containsKey(key)) continue;
            ctx.psUserPermissionsDeleteSingle.setString(2, key.toString());
        }
    }

    private void doDrop(@NotNull Context ctx, @NotNull ClaimData data) throws SQLException {
        ctx.psUserPermissionsDelete.setInt(1, data.getToken().asInt());
        ctx.psUserPermissionsDelete.execute();

        ctx.psGlobalPermissionsDelete.setInt(1, data.getToken().asInt());
        ctx.psGlobalPermissionsDelete.execute();

        ctx.psChunksDelete.setInt(1, data.getToken().asInt());
        ctx.psChunksDelete.execute();

        ctx.psInfoDelete.setInt(1, data.getToken().asInt());
        ctx.psInfoDelete.execute();
    }

    //

    protected static class Context implements Statements {

        public PreparedStatement psInfoInsert;
        public PreparedStatement psInfoSelectTokens;
        public PreparedStatement psInfoSelectByToken;
        public PreparedStatement psInfoSelectByName;
        public PreparedStatement psInfoDelete;
        public PreparedStatement psInfoUpdate;

        public PreparedStatement psChunksInsert;
        public PreparedStatement psChunksSelect;
        public PreparedStatement psChunksDeleteSingle;
        public PreparedStatement psChunksDelete;

        public PreparedStatement psGlobalPermissionsSelect;
        public Map<Permission, PreparedStatement> psGlobalPermissionsUpsert;
        public PreparedStatement psGlobalPermissionsDelete;

        public PreparedStatement psUserPermissionsSelectAll;
        public Map<Permission, PreparedStatement> psUserPermissionsInsert;
        public Map<Permission, PreparedStatement> psUserPermissionsUpdate;
        public PreparedStatement psUserPermissionsDelete;
        public PreparedStatement psUserPermissionsDeleteSingle;

        private Set<String> getColumnNames(@NotNull Connection connection, @NotNull String name) throws SQLException {
            Set<String> ret = new HashSet<>();
            try (Statement s = connection.createStatement()) {
                ResultSet rs = s.executeQuery("SELECT * FROM " + name + " LIMIT 0");
                ResultSetMetaData meta = rs.getMetaData();
                for (int i=0; i < meta.getColumnCount(); i++) {
                    ret.add(meta.getColumnName(i + 1));
                }
            }
            return ret;
        }

        @Override
        public void prepare(@NotNull Connection connection) throws SQLException {
            try (Statement s = connection.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS xcClaimInfo (token INTEGER PRIMARY KEY, name VARCHAR(255) UNIQUE NOT NULL, owner VARCHAR(36) NOT NULL, world VARCHAR(36) NOT NULL, flags VARCHAR(32) NOT NULL)");
                s.execute("CREATE TABLE IF NOT EXISTS xcClaimChunks (token INTEGER NOT NULL, x INTEGER NOT NULL, z INTEGER NOT NULL, FOREIGN KEY (token) REFERENCES xcClaimInfo(token))");
                s.execute("CREATE TABLE IF NOT EXISTS xcClaimGlobalPermissions (token INTEGER UNIQUE NOT NULL, FOREIGN KEY (token) REFERENCES xcClaimInfo(token))");
                s.execute("CREATE TABLE IF NOT EXISTS xcClaimUserPermissions (token INTEGER NOT NULL, user VARCHAR(36) NOT NULL, FOREIGN KEY (token) REFERENCES xcClaimInfo(token))");
            }

            this.psInfoInsert        = connection.prepareStatement("INSERT INTO xcClaimInfo (name, owner, world, flags) VALUES (?, ?, ?, ?)");
            this.psInfoSelectTokens  = connection.prepareStatement("SELECT token FROM xcClaimInfo");
            this.psInfoSelectByToken = connection.prepareStatement("SELECT name, owner, world, flags FROM xcClaimInfo WHERE token=?");
            this.psInfoSelectByName  = connection.prepareStatement("SELECT token FROM xcClaimInfo WHERE name=?");
            this.psInfoDelete        = connection.prepareStatement("DELETE FROM xcClaimInfo WHERE token=?");
            this.psInfoUpdate        = connection.prepareStatement("UPDATE xcClaimInfo SET name=?, owner=?, flags=? WHERE token=?");

            this.psChunksInsert = connection.prepareStatement("INSERT INTO xcClaimChunks (token, x, z) VALUES (?, ?, ?)");
            this.psChunksSelect = connection.prepareStatement("SELECT x, z FROM xcClaimChunks WHERE token=?");
            this.psChunksDeleteSingle = connection.prepareStatement("DELETE FROM xcClaimChunks WHERE token=? AND x=? AND z=?");
            this.psChunksDelete = connection.prepareStatement("DELETE FROM xcClaimChunks WHERE token=?");

            this.psGlobalPermissionsSelect = connection.prepareStatement("SELECT * FROM xcClaimGlobalPermissions WHERE token=?");
            Set<String> globalPermissionsColumns = this.getColumnNames(connection, "xcClaimGlobalPermissions");
            Map<Permission, PreparedStatement> psGlobalPermissionsUpsert = new PermissionMap<>();
            for (Permission p : Permission.values()) {
                final String name = p.sqlName();
                if (!globalPermissionsColumns.contains(name)) {
                    try (Statement s = connection.createStatement()) {
                        s.execute("ALTER TABLE xcClaimGlobalPermissions ADD `" + name + "` TINYINT UNSIGNED NOT NULL DEFAULT 255");
                    }
                }
                psGlobalPermissionsUpsert.put(
                        p,
                        connection.prepareStatement("INSERT INTO xcClaimGlobalPermissions (token, `" + name +
                                "`) VALUES (?, ?) ON CONFLICT(token) DO UPDATE SET `" + name + "`=?")
                );
            }
            this.psGlobalPermissionsUpsert = psGlobalPermissionsUpsert;
            this.psGlobalPermissionsDelete = connection.prepareStatement("DELETE FROM xcClaimGlobalPermissions WHERE token=?");

            this.psUserPermissionsSelectAll = connection.prepareStatement("SELECT * FROM xcClaimUserPermissions WHERE token=?");
            Set<String> userPermissionsColumns = this.getColumnNames(connection, "xcClaimUserPermissions");
            Map<Permission, PreparedStatement> psUserPermissionsInsert = new PermissionMap<>();
            Map<Permission, PreparedStatement> psUserPermissionsUpdate = new PermissionMap<>();
            for (Permission p : Permission.values()) {
                final String name = p.sqlName();
                if (!userPermissionsColumns.contains(name)) {
                    try (Statement s = connection.createStatement()) {
                        s.execute("ALTER TABLE xcClaimUserPermissions ADD `" + name + "` TINYINT UNSIGNED NOT NULL DEFAULT 0");
                    }
                }
                psUserPermissionsInsert.put(
                        p,
                        connection.prepareStatement("INSERT INTO xcClaimUserPermissions (token, user, `" + name +
                                "`) VALUES (?, ?, ?)")
                );
                psUserPermissionsUpdate.put(
                        p,
                        connection.prepareStatement("UPDATE xcClaimUserPermissions SET `" + name +
                                "`=? WHERE token=? AND user=?")
                );
            }
            this.psUserPermissionsInsert = psUserPermissionsInsert;
            this.psUserPermissionsUpdate = psUserPermissionsUpdate;
            this.psUserPermissionsDelete = connection.prepareStatement("DELETE FROM xcClaimUserPermissions WHERE token=?");
            this.psUserPermissionsDeleteSingle = connection.prepareStatement("DELETE FROM xcClaimUserPermissions WHERE token=? AND user=?");
        }

        @Override
        public void close() throws SQLException {
            this.psInfoInsert.close();
            this.psInfoSelectTokens.close();
            this.psInfoSelectByToken.close();
            this.psInfoDelete.close();
            this.psInfoUpdate.close();

            this.psChunksInsert.close();
            this.psChunksSelect.close();
            this.psChunksDelete.close();
            this.psChunksDeleteSingle.close();

            this.psGlobalPermissionsSelect.close();
            for (PreparedStatement ps : this.psGlobalPermissionsUpsert.values())
                ps.close();
            this.psGlobalPermissionsDelete.close();

            this.psUserPermissionsSelectAll.close();
            for (PreparedStatement ps : this.psUserPermissionsInsert.values())
                ps.close();
            for (PreparedStatement ps : this.psUserPermissionsUpdate.values())
                ps.close();
            this.psUserPermissionsDelete.close();
            this.psUserPermissionsDeleteSingle.close();
        }

    }

}
