package io.github.wasabithumb.xclaim.trust.impl.sql;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.wasabithumb.xclaim.trust.DirectTrustSet;
import io.github.wasabithumb.xclaim.trust.TrustManager;
import io.github.wasabithumb.xclaim.trust.TrustSet;
import io.github.wasabithumb.xclaim.util.SQLHelper;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

public abstract class SQLTrustManager extends SQLHelper<SQLTrustManager.Context> implements TrustManager {

    protected final LoadingCache<UUID, TrustSet> cache;
    protected SQLTrustManager(@NotNull Logger logger) {
        super(logger);
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(5L))
                .build(this::getFresh);
    }

    @Override
    protected @NotNull Context newStatements() {
        return new Context();
    }

    @Override
    public @NotNull Collection<UUID> keys() {
        return this.use((Context ctx) -> {
            Set<UUID> set = new LinkedHashSet<>();
            ResultSet rs = ctx.psList.executeQuery();
            while (rs.next()) set.add(UUID.fromString(rs.getString(1)));
            return set;
        }, Collections::emptySet);
    }

    @Override
    public @NotNull TrustSet get(@NotNull UUID target) {
        return this.cache.get(target);
    }

    protected @NotNull TrustSet getFresh(@NotNull UUID target) {
        Set<UUID> ret = this.use((Context ctx) -> {
            Set<UUID> set = new LinkedHashSet<>();
            ctx.psSelect.setString(1, target.toString());
            ResultSet rs = ctx.psSelect.executeQuery();
            if (!rs.next()) return set;
            String data = rs.getString(1);
            if (data == null) return set;
            if (data.isEmpty()) return set;
            for (String part : data.split(":")) {
                set.add(UUID.fromString(part));
            }
            return set;
        }, HashSet::new);
        return new DirectTrustSet(this, target, Collections.synchronizedSet(ret));
    }

    @Override
    public void trust(@NotNull UUID target, @NotNull UUID player) {
        this.use((Context ctx) -> {
            final String key = target.toString();
            final String value = player.toString();
            ctx.psSelect.setString(1, key);
            ResultSet rs = ctx.psSelect.executeQuery();
            String data;
            if (rs.next() && (data = rs.getString(1)) != null && !data.isEmpty()) {
                int z = 0;
                char n;
                for (int i=0; i < data.length(); i++) {
                    n = data.charAt(i);
                    if (n == ':') {
                        z = 0;
                        continue;
                    }
                    if (z == -1) continue;
                    if (n != value.charAt(z++)) {
                        z = -1;
                        continue;
                    }
                    if (z == 36) return;
                }
                data += ":";
            } else {
                data = "";
            }
            data += value;
            ctx.psInsert.setString(1, key);
            ctx.psInsert.setString(2, data);
            ctx.psInsert.setString(3, data);
            ctx.psInsert.execute();
        });
    }

    @Override
    public void untrust(@NotNull UUID target, @NotNull UUID player) {
        this.use((Context ctx) -> {
            final String key = target.toString();
            final String value = player.toString();
            ctx.psSelect.setString(1, key);
            ResultSet rs = ctx.psSelect.executeQuery();
            if (!rs.next()) return;
            String data = rs.getString(1);
            if (data == null || data.isEmpty()) return;
            int i = -1;
            int z = 0;
            char n;
            while (true) {
                if (++i >= data.length()) return;
                n = data.charAt(i);
                if (n == ':') {
                    z = 0;
                    continue;
                }
                if (z == -1) continue;
                if (n != value.charAt(z++)) {
                    z = -1;
                    continue;
                }
                if (z == 36) break;
                i++;
            }

            final StringBuilder newData = new StringBuilder(data.length());
            final int start = i - 35;
            final boolean isNotFirst = (start > 0);
            final boolean isNotLast = i < (data.length() - 1);

            if (isNotFirst) {
                newData.append(data, 0, start - 1);
                if (isNotLast) newData.append(':');
            }
            if (isNotLast) {
                newData.append(data, i + 2, data.length());
            }

            final String newDataStr = newData.toString();
            ctx.psInsert.setString(1, key);
            ctx.psInsert.setString(2, newDataStr);
            ctx.psInsert.setString(3, newDataStr);
            ctx.psInsert.execute();
        });
    }

    //

    protected static class Context implements Statements {

        transient PreparedStatement psInsert;
        transient PreparedStatement psSelect;
        transient PreparedStatement psList;

        @Override
        public void prepare(@NotNull Connection connection) throws SQLException {
            this.psInsert = connection.prepareStatement("INSERT INTO xcTrust (owner, players) VALUES (?, ?) ON CONFLICT(owner) DO UPDATE SET players = ?");
            this.psSelect = connection.prepareStatement("SELECT players FROM xcTrust WHERE owner = ?");
            this.psList   = connection.prepareStatement("SELECT owner FROM xcTrust");
        }

        @Override
        public void close() throws SQLException {
            this.psInsert.close();
            this.psSelect.close();
            this.psList.close();
        }

    }

}
