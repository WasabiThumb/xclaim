package codes.wasabi.xclaim.command.argument.type;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RangeType extends Type<Integer> {

    private final int f;
    private final int t;
    private final int radix;
    private final List<String> sampleValues;
    public RangeType(int from, int to, int radix) {
        f = Math.min(from, to);
        t = Math.max(from, to);
        this.radix = radix;
        sampleValues = new ArrayList<>();
        for (int i=f; i <= t; i++) {
            sampleValues.add(Integer.toString(i, radix));
        }
    }

    public RangeType(int from, int to) {
        this(from, to, 10);
    }

    @Override
    public @NotNull Class<Integer> getTypeClass() {
        return Integer.class;
    }

    @Override
    public @NotNull String getTypeName() {
        return "Integer between " + sampleValues.get(0) + " and " + sampleValues.get(sampleValues.size() - 1);
    }

    @Override
    public @NotNull Collection<String> getSampleValues() {
        return sampleValues;
    }

    @Override
    protected @NotNull Integer convert(@NotNull String string) {
        return Integer.valueOf(string, radix);
    }

    @Override
    protected boolean validate(@NotNull Integer value) {
        return (f <= value) && (value <= t);
    }

}
