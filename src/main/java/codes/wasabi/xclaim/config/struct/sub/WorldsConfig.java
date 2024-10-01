package codes.wasabi.xclaim.config.struct.sub;

import codes.wasabi.xclaim.config.struct.Config;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

public interface WorldsConfig extends Config {

    @UnknownNullability Long graceTime();

    @UnknownNullability Boolean useWhitelist();

    @UnknownNullability Collection<String> whitelist();

    @UnknownNullability Boolean useBlacklist();

    @UnknownNullability Collection<String> blacklist();

    @UnknownNullability Boolean caseSensitive();

    //

    default boolean checkLists(@NotNull String name) {
        Collection<String> list;
        Predicate<String> predicate;
        if (Objects.equals(this.caseSensitive(), Boolean.TRUE)) {
            predicate = name::equals;
        } else {
            predicate = name::equalsIgnoreCase;
        }

        if (Objects.equals(this.useWhitelist(), Boolean.TRUE)) {
            list = this.whitelist();
            ok: {
                if (list == null) break ok;
                for (String white : list) {
                    if (predicate.test(white)) break ok;
                }
                return false;
            }
        }

        if (Objects.equals(this.useBlacklist(), Boolean.TRUE)) {
            list = this.blacklist();
            for (String black : list) {
                if (predicate.test(black)) return false;
            }
        }

        return true;
    }

    default boolean checkLists(@NotNull World world) {
        return this.checkLists(world.getName());
    }

}
