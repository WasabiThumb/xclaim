package io.github.wasabithumb.xclaim.config.impl.yaml.helpers;

import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.function.BiFunction;

@ApiStatus.Internal
public class YamlLimits {

    @Contract("null -> null; !null -> new")
    public static YamlLimits of(ConfigurationSection section) {
        if (section == null) return null;
        return new YamlLimits(section);
    }

    protected final @NotNull ConfigurationSection section;
    public YamlLimits(@NotNull ConfigurationSection section) {
        this.section = section;
    }

    private @Nullable Long getLong(
            @Nullable PlatformUser target,
            @NotNull String key,
            @NotNull Comparator<Long> cmp,
            @NotNull BiFunction<ConfigurationSection, String, Long> extractor
    ) {
        long max = 0L;
        boolean any = false;

        ConfigurationSection sub;
        long value;
        for (String group : this.section.getKeys(false)) {
            if (!this.inGroup(target, group)) continue;
            sub = this.section.getConfigurationSection(group);
            if (sub == null) continue;

            if (!sub.contains(key)) continue;
            value = extractor.apply(sub, key);

            if (any) {
                if (cmp.compare(value, max) > 0) {
                    max = value;
                }
            } else {
                max = value;
                any = true;
            }
        }

        return any ? max : null;
    }

    /**
     * Returns the greatest value inherited by "target" for the given "key" via the provided comparator
     */
    public @Nullable Long getLong(@Nullable PlatformUser target, @NotNull String key, @NotNull Comparator<Long> cmp) {
        return this.getLong(target, key, cmp, ConfigurationSection::getLong);
    }

    /**
     * Returns the greatest value inherited by "target" for the given "key" via the provided comparator
     */
    public @Nullable Integer getInt(@Nullable PlatformUser target, @NotNull String key, @NotNull Comparator<Integer> cmp) {
        Long l = this.getLong(target, key, new IntAsLongComparator(cmp), (a, b) -> (long) a.getInt(b));
        if (l == null) return null;
        return l.intValue();
    }

    private boolean inGroup(@Nullable PlatformUser target, @NotNull String groupName) {
        if (groupName.equals("default")) return true;
        if (target == null) return false;
        if (target.isOp()) return true;
        return target.hasPermission("xclaim.group." + groupName);
    }

    //

    private record IntAsLongComparator(Comparator<Integer> backing) implements Comparator<Long> {

        @Override
        public int compare(Long a, Long b) {
            return this.backing.compare(Math.toIntExact(a), Math.toIntExact(b));
        }

    }

}
