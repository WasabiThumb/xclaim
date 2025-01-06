package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformEntityEvent;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PlatformEntityDeathEvent extends PlatformEntityEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.ENTITY_DEATH;

    //

    @NotNull List<PlatformItem> drops();

}
