package codes.wasabi.xclaim.debug.writer;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

class AudienceDebugWriter extends AbstractDebugWriter {

    private final Audience audience;
    public AudienceDebugWriter(@NotNull Audience audience) {
        this.audience = audience;
    }

    @Override
    protected void println(@NotNull CharSequence text, @NotNull TextColor color) {
        this.audience.sendMessage(Component.text(text.toString()).color(color));
    }

}
