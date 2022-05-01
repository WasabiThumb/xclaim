package codes.wasabi.xclaim.command.argument.type;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class IntType extends Type<Integer> {

    @Override
    public @NotNull Class<Integer> getTypeClass() {
        return Integer.class;
    }

    @Override
    public @NotNull String getTypeName() {
        return "Integer";
    }

    @Override
    public @NotNull Collection<String> getSampleValues() {
        return Arrays.asList("0", "1");
    }

    @Override
    protected @NotNull Integer convert(@NotNull String string) throws NumberFormatException {
        return Integer.valueOf(string);
    }

}
