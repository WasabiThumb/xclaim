package io.github.wasabithumb.xclaim.placeholder;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.ClaimManager;
import io.github.wasabithumb.xclaim.config.struct.sub.RulesConfig;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorldManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPlaceholder implements Placeholder {

    protected final XClaim runtime;
    public AbstractPlaceholder(@NotNull XClaim runtime) {
        this.runtime = runtime;
    }

    //

    protected @NotNull ClaimManager claims() {
        return this.runtime.claims();
    }

    protected @Nullable PlatformWorld matchWorld(@NotNull String world) {
        PlatformWorldManager worlds = this.runtime.platform().worlds();
        PlatformWorld ret = worlds.getWorld(world);
        if (ret != null) return ret;
        for (PlatformWorld candidate : worlds.getAll()) {
            if (world.equalsIgnoreCase(candidate.name())) return candidate;
        }
        return null;
    }

    protected @NotNull RulesConfig rules() {
        return this.runtime.rootConfig().rules();
    }

}
