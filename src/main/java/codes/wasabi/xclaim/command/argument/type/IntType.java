package codes.wasabi.xclaim.command.argument.type;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class IntType extends Type<Integer> {

    private final int radix;
    private final List<String> sampleValues;
    public IntType(int radix) {
        this.radix = radix;
        sampleValues = Arrays.asList(
                Integer.toString(0, radix),
                Integer.toString(1, radix)
        );
    }

    public IntType() {
        this(10);
    }

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
        return sampleValues;
    }

    @Override
    protected @NotNull Integer convert(@NotNull String string) throws NumberFormatException {
        return Integer.valueOf(string, radix);
    }

}
