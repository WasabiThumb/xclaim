package io.github.wasabithumb.xclaim.integration;

import org.jetbrains.annotations.ApiStatus;

/**
 * Exceptions that the constructor of a {@link Integration} may throw if the environment prevents the integration
 * from being used.
 */
@ApiStatus.Internal
public class IntegrationException extends RuntimeException {

    public IntegrationException(String message) {
        super(message);
    }

    public IntegrationException(String message, Throwable cause) {
        super(message, cause);
    }

}
