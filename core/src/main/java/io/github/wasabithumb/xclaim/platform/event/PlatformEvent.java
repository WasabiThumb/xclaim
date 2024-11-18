package io.github.wasabithumb.xclaim.platform.event;

import io.github.wasabithumb.xclaim.platform.PlatformObject;

public interface PlatformEvent extends PlatformObject {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
