package io.github.wasabithumb.xclaim.platform.event.adapter;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.*;

/**
 * Marks a class or constructor as an "event adapter", in that it takes one of the following forms:
 * <ul>
 *     <li>
 *         A constructor on a {@link io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent SpongePlatformEvent}
 *         which accepts a {@link io.github.wasabithumb.xclaim.platform.SpongePlatform SpongePlatform} and an
 *         implementation-specific {@link org.spongepowered.api.event.Event Event}.
 *     </li>
 *     <li>
 *         A static method which returns a
 *         {@link io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent SpongePlatformEvent} and accepts a
 *         {@link io.github.wasabithumb.xclaim.platform.SpongePlatform SpongePlatform} and an implementation-specific
 *         {@link org.spongepowered.api.event.Event Event}.
 *     </li>
 *     <li>
 *         A static method which returns void and accepts a
 *         {@link io.github.wasabithumb.xclaim.platform.SpongePlatform SpongePlatform},
 *         an implementation-specific {@link org.spongepowered.api.event.Event Event} and a
 *         {@link java.util.Queue Queue&lt;PlatformEvent&gt;} which may receive 1 or more
 *         {@link io.github.wasabithumb.xclaim.platform.event.SpongePlatformEvent SpongePlatformEvent}s.
 *     </li>
 * </ul>
 */
@ApiStatus.Internal
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Adapter { }
