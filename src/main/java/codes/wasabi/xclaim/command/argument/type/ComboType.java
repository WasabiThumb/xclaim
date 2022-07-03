package codes.wasabi.xclaim.command.argument.type;

import codes.wasabi.xclaim.XClaim;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ComboType extends Type<Object> {

    private final Type<?>[] types;
    private final String typeName;
    private final Set<String> samples;
    public ComboType(Type<?> @NotNull ... types) throws IllegalArgumentException {
        if (types.length < 1) throw new IllegalArgumentException("ComboType must be comprised of at least 1 type");
        this.types = types;
        Set<String> names = new HashSet<>();
        Set<String> sampleValues = new HashSet<>();
        for (Type<?> type : types) {
            sampleValues.addAll(type.getSampleValues());
            names.add(type.getTypeName());
        }
        samples = Collections.unmodifiableSet(sampleValues);
        StringBuilder sb = new StringBuilder();
        if (names.size() > 5) {
            sb.append(XClaim.lang.get("arg-combo-many"));
        } else {
            String sep = XClaim.lang.get("arg-combo-separator");
            String or = XClaim.lang.get("arg-combo-or");
            int i = 0;
            for (String name : names) {
                if (i > 0) {
                    if (i < (names.size() - 1)) {
                        sb.append(sep);
                    } else {
                        sb.append(or);
                    }
                }
                sb.append(name);
                i++;
            }
        }
        typeName = sb.toString();
    }

    @Override
    public @NotNull String getTypeName() {
        return typeName;
    }

    @Override
    public @NotNull Class<Object> getTypeClass() {
        return Object.class;
    }

    @Override
    public @NotNull Collection<String> getSampleValues() {
        return samples;
    }

    @Override
    protected @NotNull Object convert(@NotNull String string) throws IllegalArgumentException {
        for (Type<?> type : types) {
            try {
                return type.parse(string);
            } catch (IllegalArgumentException ignored) { }
        }
        throw new IllegalArgumentException("None of the types in this ComboType can successfully parse input \"" + string + "\"");
    }

}
