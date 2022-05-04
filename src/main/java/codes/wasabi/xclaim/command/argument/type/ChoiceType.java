package codes.wasabi.xclaim.command.argument.type;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ChoiceType extends Type<String> {

    private final Collection<String> choices;
    public ChoiceType(@NotNull Collection<String> choices) {
        this.choices = choices;
    }

    public ChoiceType(@NotNull String @NotNull ... choices) {
        this(Arrays.asList(choices));
    }

    @Override
    public @NotNull Class<String> getTypeClass() {
        return String.class;
    }

    @Override
    protected @NotNull String convert(@NotNull String string) {
        return string;
    }

    @Override
    public @NotNull String getTypeName() {
        int len = choices.size();
        if (len == 0) return "Nothing";
        StringBuilder sb = new StringBuilder("Choice of ");
        Iterator<String> iter = choices.iterator();
        for (int i=0; i < len; i++) {
            sb.append(iter.next());
            if ((i + 1) < len) {
                if ((i + 2) == len) {
                    sb.append(" and ");
                } else {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public @NotNull Collection<String> getSampleValues() {
        return choices;
    }

    @Override
    protected boolean validate(@NotNull String value) {
        return choices.contains(value);
    }

}
