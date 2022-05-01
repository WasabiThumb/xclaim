package codes.wasabi.xclaim.command.argument.type;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class StringType extends Type<String> {

    @Override
    public @NotNull Class<String> getTypeClass() {
        return String.class;
    }

    @Override
    public @NotNull String getTypeName() {
        return "Text";
    }

    @Override
    public @NotNull Collection<String> getSampleValues() {
        return Collections.singleton("text");
    }

    @Override
    protected @NotNull String convert(@NotNull String string) {
        return string;
    }

}
