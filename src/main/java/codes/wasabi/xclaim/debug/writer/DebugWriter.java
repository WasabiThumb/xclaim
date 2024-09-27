package codes.wasabi.xclaim.debug.writer;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public interface DebugWriter {

    static @NotNull DebugWriter of(@NotNull Audience audience) {
        return new AudienceDebugWriter(audience);
    }

    //

    void color(@NotNull TextColor color);

    void println(@NotNull CharSequence text);

    default void println() {
        this.println("");
    }

    void raise(@NotNull Throwable t);

}
