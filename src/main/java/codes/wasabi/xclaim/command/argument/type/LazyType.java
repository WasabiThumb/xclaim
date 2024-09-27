package codes.wasabi.xclaim.command.argument.type;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Supplier;

public class LazyType<T> extends Type<T> {

    private final Supplier<Type<T>> supplier;
    private Type<T> value = null;
    public LazyType(Supplier<Type<T>> supplier) {
        this.supplier = supplier;
    }

    private @NotNull Type<T> getValue() {
        synchronized (this) {
            if (this.value != null) return this.value;
            return this.value = this.supplier.get();
        }
    }

    @Override
    public @NotNull Class<T> getTypeClass() {
        return this.getValue().getTypeClass();
    }

    @Override
    protected @NotNull T convert(@NotNull String string) throws Exception {
        return this.getValue().convert(string);
    }

    @Override
    public @NotNull String getTypeName() {
        return this.getValue().getTypeName();
    }

    @Override
    public @NotNull Collection<String> getSampleValues() {
        return this.getValue().getSampleValues();
    }

    @Override
    protected boolean validate(@NotNull T value) {
        return this.getValue().validate(value);
    }

}
