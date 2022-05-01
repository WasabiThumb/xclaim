package codes.wasabi.xclaim.command.argument.type;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class FloatType extends Type<Float> {

    @Override
    public @NotNull Class<Float> getTypeClass() {
        return Float.class;
    }

    @Override
    public @NotNull String getTypeName() {
        return "Decimal";
    }

    @Override
    public @NotNull Collection<String> getSampleValues() {
        return Arrays.asList("0.0", "1.0");
    }

    @Override
    protected @NotNull Float convert(@NotNull String string) throws NumberFormatException {
        return Float.valueOf(string);
    }

}
