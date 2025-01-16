package io.github.wasabithumb.xclaim;

import io.github.wasabithumb.xclaim.asset.AssetManager;
import io.github.wasabithumb.xclaim.claim.data.ClaimDataManager;
import io.github.wasabithumb.xclaim.command.CommandManager;
import io.github.wasabithumb.xclaim.config.struct.RootConfig;
import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.trust.TrustManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

@ApiStatus.Internal
public interface XClaimBootstrap {

    @NotNull Logger logger();

    @NotNull Platform platform();

    @NotNull AssetManager assets();

    default boolean supportsLegacyConfig() {
        return false;
    }

    default @NotNull RootConfig loadLegacyConfig(@NotNull InputStream root) throws Exception {
        throw new UnsupportedOperationException();
    }

    default boolean supportsLegacyTrust() {
        return false;
    }

    default @NotNull TrustManager loadLegacyTrust(@NotNull File file) throws Exception {
        throw new UnsupportedOperationException();
    }

    default boolean supportsLegacyClaimData() {
        return false;
    }

    default @NotNull ClaimDataManager loadLegacyClaimData(@NotNull File file) throws Exception {
        throw new UnsupportedOperationException();
    }

}
