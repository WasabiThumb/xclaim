package codes.wasabi.xclaim.command.argument.type;

import codes.wasabi.xclaim.XClaim;
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
        return XClaim.lang.get("arg-string-name");
    }

    @Override
    public @NotNull Collection<String> getSampleValues() {
        return Collections.singleton(XClaim.lang.get("arg-string-sample"));
    }

    @Override
    protected @NotNull String convert(@NotNull String string) {
        return string;
    }

}
