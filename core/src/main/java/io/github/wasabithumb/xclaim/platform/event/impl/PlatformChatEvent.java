package io.github.wasabithumb.xclaim.platform.event.impl;

import io.github.wasabithumb.xclaim.platform.event.PlatformEventType;
import org.jetbrains.annotations.NotNull;

public interface PlatformChatEvent extends PlatformPlayerEvent {

    PlatformEventType TYPE = PlatformEventType.CHAT;

    @NotNull String message();

    @NotNull String plainMessage();

}
