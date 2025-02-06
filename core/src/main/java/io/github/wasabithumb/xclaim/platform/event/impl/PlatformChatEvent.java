package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformPlayerEvent;
import org.jetbrains.annotations.NotNull;

public interface PlatformChatEvent extends PlatformPlayerEvent {

    @SuppressWarnings("unused")
    PlatformEventType TYPE = PlatformEventType.CHAT;

    //

    @NotNull String message();

    @NotNull String plainMessage();

}
