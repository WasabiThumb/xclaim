package codes.wasabi.xclaim.command.argument;

import codes.wasabi.xclaim.command.argument.type.Type;
import org.jetbrains.annotations.NotNull;

public record Argument(@NotNull Type<?> type, @NotNull String name, @NotNull String description) {

}
