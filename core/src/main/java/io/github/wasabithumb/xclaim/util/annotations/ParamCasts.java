package io.github.wasabithumb.xclaim.util.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Indicates that a parameter of wider type will have a narrowing cast performed to the
 * specified type within the method body. Thus, passing a value that may not be cast to this type
 * should be expected to throw.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
public @interface ParamCasts {
    @NotNull Class<?> value();
}
