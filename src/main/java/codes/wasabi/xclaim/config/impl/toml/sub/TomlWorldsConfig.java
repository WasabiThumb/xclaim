package codes.wasabi.xclaim.config.impl.toml.sub;

import codes.wasabi.xclaim.config.impl.toml.TomlConfig;
import codes.wasabi.xclaim.config.struct.sub.WorldsConfig;
import codes.wasabi.xclaim.util.ProxyList;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TomlWorldsConfig extends TomlConfig implements WorldsConfig {

    public TomlWorldsConfig(@Nullable Toml table) {
        super(table);
    }

    @Override
    public @UnknownNullability Long graceTime() {
        return this.getLong("grace-time");
    }

    @Override
    public @UnknownNullability Boolean useWhitelist() {
        return this.getBoolean("use-whitelist");
    }

    @Override
    public @UnknownNullability Collection<String> whitelist() {
        if (!this.valid || !Objects.equals(this.useWhitelist(), Boolean.TRUE)) return Collections.emptyList();
        return this.getStringList("whitelist");
    }

    @Override
    public @UnknownNullability Boolean useBlacklist() {
        return this.getBoolean("use-blacklist");
    }

    @Override
    public @UnknownNullability Collection<String> blacklist() {
        if (!this.valid || !Objects.equals(this.useBlacklist(), Boolean.TRUE)) return Collections.emptyList();
        return this.getStringList("blacklist");
    }

    @Override
    public @UnknownNullability Boolean caseSensitive() {
        return this.getBoolean("case-sensitive");
    }

    private @Nullable List<String> getStringList(@NotNull String key) {
        List<?> list;
        try {
            list = this.raw().getList(key);
        } catch (ClassCastException ignored) {
            return null;
        }
        return new ProxyList<>(list, Objects::toString);
    }

}
