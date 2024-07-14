package codes.wasabi.xclaim.placeholder;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface Placeholder {

    @Contract("_, _ -> new")
    static @NotNull Placeholder simple(@NotNull final String stem, @NotNull final Function<OfflinePlayer, String> compute) {
        return new Placeholder() {
            @Override
            public @NotNull String getStem() {
                return stem;
            }

            @Override
            public boolean hasPositionalArgument() {
                return false;
            }

            @Override
            public @Nullable String computeFor(@NotNull OfflinePlayer player, @Nullable String arg) {
                return compute.apply(player);
            }
        };
    }

    //

    @NotNull String getStem();

    boolean hasPositionalArgument();

    @Nullable String computeFor(@NotNull OfflinePlayer player, @Nullable String arg);

}
