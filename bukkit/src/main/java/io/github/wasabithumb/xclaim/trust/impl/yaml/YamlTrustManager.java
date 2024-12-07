package io.github.wasabithumb.xclaim.trust.impl.yaml;

import io.github.wasabithumb.xclaim.trust.TrustManager;
import io.github.wasabithumb.xclaim.util.ProxySet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class YamlTrustManager implements TrustManager {

    protected final File file;
    protected final FileConfiguration yaml;
    public YamlTrustManager(@NotNull File file, @NotNull FileConfiguration yaml) {
        this.file = file;
        this.yaml = yaml;
    }

    @ApiStatus.Internal
    public @NotNull ConfigurationSection data() {
        return this.yaml;
    }

    @Override
    public @NotNull Collection<UUID> keys() {
        Set<String> keys = this.yaml.getKeys(false);
        return new ProxySet<>(UUID.class, keys, UUID::fromString, UUID::toString);
    }

    @Override
    public @NotNull YamlTrustSet get(@NotNull UUID target) {
        return new YamlTrustSet(this, target);
    }

    @Override
    public void trust(@NotNull UUID target, @NotNull UUID player) {
        this.get(target).add(player);
    }

    @Override
    public void untrust(@NotNull UUID target, @NotNull UUID player) {
        this.get(target).remove(player);
    }

    @Override
    public void close() throws IOException {
        this.yaml.save(this.file);
    }

}
