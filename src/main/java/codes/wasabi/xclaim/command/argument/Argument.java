package codes.wasabi.xclaim.command.argument;

import codes.wasabi.xclaim.command.argument.type.Type;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Argument {

    private final Type<?> type;
    private final String name;
    private final String description;
    public Argument(@NotNull Type<?> type, @NotNull String name, @NotNull String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public final @NotNull Type<?> type() {
        return type;
    }

    public final @NotNull String name() {
        return name;
    }

    public final @NotNull String description() {
        return description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, description);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof Argument) {
            if (equals((Argument) obj)) return true;
        }
        return super.equals(obj);
    }

    public boolean equals(Argument other) {
        if (this == other) return true;
        if (other == null) return false;
        if (!Objects.equals(type, other.type)) return false;
        if (!Objects.equals(name, other.name)) return false;
        return Objects.equals(description, other.description);
    }

    @Override
    public String toString() {
        return "Argument[type=" + type + ",name=" + name + ",description=" + description + "]";
    }

}
