package io.github.wasabithumb.xclaim.integration;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.*;

/**
 * Annotation that an {@link Integration} may use on any of its fields to have it
 * be injected by the loader. Supported types:
 * <ul>
 *     <li>{@link io.github.wasabithumb.xclaim.XClaim XClaim}</li>
 *     <li>{@link io.github.wasabithumb.xclaim.XClaimBootstrap XClaimBootstrap}</li>
 *     <li>{@link io.github.wasabithumb.xclaim.i18n.Lang Lang}</li>
 *     <li>{@link io.github.wasabithumb.xclaim.platform.Platform Platform}</li>
 *     <li>{@link io.github.wasabithumb.xclaim.platform.PlatformTypeAdapter PlatformTypeAdapter}</li>
 *     <li>{@link java.util.logging.Logger Logger}</li>
 *     <li>Any {@link io.github.wasabithumb.xclaim.config.struct.Config Config}</li>
 * </ul>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiStatus.Internal
public @interface IntegrationInject {
}
