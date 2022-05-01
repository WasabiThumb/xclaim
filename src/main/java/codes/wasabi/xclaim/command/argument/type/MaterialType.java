package codes.wasabi.xclaim.command.argument.type;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MaterialType extends Type<Material> {

    @Override
    public @NotNull Class<Material> getTypeClass() {
        return Material.class;
    }

    @Override
    public @NotNull String getTypeName() {
        return "Material";
    }

    @Override
    public @NotNull Collection<String> getSampleValues() {
        return Arrays.stream(Material.values()).flatMap((Material m) -> Stream.of(m.name())).collect(Collectors.toList());
    }

    @Override
    protected @NotNull Material convert(@NotNull String string) {
        return Objects.requireNonNull(Material.matchMaterial(string));
    }

}
