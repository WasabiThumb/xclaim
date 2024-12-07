package io.github.wasabithumb.xclaim.trust.impl.yaml;

import io.github.wasabithumb.xclaim.trust.AbstractTrustSet;
import io.github.wasabithumb.xclaim.util.ProxyList;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class YamlTrustSet extends AbstractTrustSet {

    private final String key;
    private transient boolean checked = false;
    public YamlTrustSet(@NotNull YamlTrustManager manager, @NotNull UUID owner) {
        super(manager, owner);
        this.key = owner.toString();
    }

    @Override
    protected @NotNull YamlTrustManager manager() {
        return (YamlTrustManager) super.manager();
    }

    protected @NotNull List<UUID> list(boolean mutable) {
        final ConfigurationSection yaml = this.manager().data();
        if (!yaml.contains(this.key)) return Collections.emptyList();
        List<String> list = yaml.getStringList(this.key);
        if (mutable) list = new ArrayList<>(list);
        return new ProxyList<>(list, (String data) -> {
            UUID uuid;
            try {
                uuid = UUID.fromString(data);
            } catch (IllegalArgumentException | NullPointerException ignored) {
                throw new AssertionError("YAML value \"" + data + "\" is not a UUID (in " + this.key + ")");
            }
            return uuid;
        }, mutable ? UUID::toString : null);
    }

    protected void checkList(@NotNull List<UUID> list) {
        if (this.checked) return;
        final Set<UUID> seen = new LinkedHashSet<>();
        for (UUID value : list) {
            if (!seen.add(value)) throw new AssertionError("YAML trust data has repeat UUID: " + list + " (in " + this.key + ")");
        }
        this.checked = true;
    }

    protected void writeList(@NotNull List<UUID> list) {
        this.manager().data().set(
                this.key,
                new ArrayList<>(new ProxyList<>(list, UUID::toString))
        );
    }

    @Override
    public @NotNull Iterator<UUID> iterator() {
        final List<UUID> list = this.list(false);
        this.checkList(list);
        return list.iterator();
    }

    @Override
    public int size() {
        final List<UUID> list = this.list(false);
        this.checkList(list);
        return list.size();
    }

    @Override
    protected boolean containsInternal(@NotNull UUID uuid) {
        return this.list(false).contains(uuid);
    }

    @Override
    protected boolean addInternal(@NotNull UUID uuid) {
        List<UUID> list = this.list(true);
        if (list.contains(uuid)) return false;
        list.add(uuid);
        this.writeList(list);
        return true;
    }

    @Override
    protected boolean removeInternal(@NotNull UUID uuid) {
        List<UUID> list = this.list(true);
        if (!list.contains(uuid)) return false;
        list.remove(uuid);
        this.writeList(list);
        return true;
    }

    @Override
    protected void clearInternal() {
        this.manager().data().set(this.key, null);
    }

}
