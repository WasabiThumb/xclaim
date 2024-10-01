package codes.wasabi.xclaim.debug;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.*;

/**
 * Marks a status as Debuggable, meaning that the Debug system will look for {@link codes.wasabi.xclaim.debug.goal.DebugGoal DebugGoal}s
 * within the class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiStatus.Internal
public @interface Debuggable {
}
