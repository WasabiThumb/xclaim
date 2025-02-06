package io.github.wasabithumb.xclaim.platform.event.adapter;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.event.PlatformEvent;
import io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent;
import org.jetbrains.annotations.*;
import org.spongepowered.api.event.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

@ApiStatus.Internal
public interface AdapterInstance<E extends Event> {

    static @NotNull @Unmodifiable List<AdapterInstance<?>> declared(
            @NotNull Class<? extends SpongePlatformEvent<?>> cls
    ) {
        List<AdapterInstance<?>> ret = new ArrayList<>(1);
        ConstructorAdapterInstance.find(cls, ret::add);
        MethodAdapterInstance.find(cls, ret::add);
        return Collections.unmodifiableList(ret);
    }

    //

    @NotNull Class<E> spongeClass();

    @Contract(mutates = "param3")
    void adapt(@NotNull SpongePlatform platform, @NotNull E spongeEvent, @NotNull Queue<PlatformEvent> out);

}
