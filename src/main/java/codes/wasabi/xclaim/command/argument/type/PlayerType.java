package codes.wasabi.xclaim.command.argument.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerType extends Type<Player> {

    @Override
    public @NotNull Class<Player> getTypeClass() {
        return Player.class;
    }

    @Override
    public @NotNull String getTypeName() {
        return "Player";
    }

    @Override
    public @NotNull Collection<String> getSampleValues() {
        return Bukkit.getOnlinePlayers().stream().flatMap((Player ply) -> Stream.of(ply.getName())).collect(Collectors.toList());
    }

    @Override
    protected @NotNull Player convert(@NotNull String string) throws NullPointerException {
        Player uncased = null;
        for (Player ply : Bukkit.getOnlinePlayers()) {
            String name = ply.getName();
            if (name.equalsIgnoreCase(string)) {
                uncased = ply;
                if (name.equals(string)) return ply;
            }
        }
        return Objects.requireNonNull(uncased);
    }

}
