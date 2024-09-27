package codes.wasabi.xclaim.debug.goal;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.*;

/**
 * Marks a method as a debug goal, for use with {@link codes.wasabi.xclaim.command.sub.DebugCommand DebugCommand}.
 * Running debug goals requires "enableDebug" to be set at compile time.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiStatus.Internal
public @interface DebugGoal {
    boolean async() default false;
}
