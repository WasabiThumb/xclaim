package io.github.wasabithumb.xclaim.platform;

import org.jetbrains.annotations.NotNull;

/**
 * An object that exposes a platform-specific handle. May be used when the current platform is known.
 */
public interface PlatformObject {

    @NotNull Object handle();

}
