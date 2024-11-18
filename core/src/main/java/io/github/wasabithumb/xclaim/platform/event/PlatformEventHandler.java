package io.github.wasabithumb.xclaim.platform.event;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface PlatformEventHandler {
    @NotNull PlatformEventCategory category() default PlatformEventCategory.NORMAL;
}
