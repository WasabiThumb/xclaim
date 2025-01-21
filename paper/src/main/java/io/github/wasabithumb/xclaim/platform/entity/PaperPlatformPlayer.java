package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.misc.PaperPlatformBossBar;
import io.github.wasabithumb.xclaim.platform.misc.PlatformBossBar;
import io.github.wasabithumb.xclaim.util.identity.Identities;
import io.github.wasabithumb.xclaim.util.identity.Identity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PaperPlatformPlayer extends BukkitPlatformPlayer {

    public PaperPlatformPlayer(@NotNull PaperPlatform platform, @NotNull Player handle) {
        super(platform, handle);
    }

    protected @NotNull PaperPlatform platform() {
        return (PaperPlatform) this.platform;
    }

    protected @NotNull Identity identity() {
        return Identities.get(this.handle());
    }

    @Override
    public @NotNull UUID uuid() {
        return this.identity().uuid();
    }

    @Override
    public @NotNull String name() {
        return this.identity().name();
    }

    @Override
    public @NotNull String displayName() {
        return this.identity().displayName();
    }

    @Override
    public boolean isOp() {
        return this.identity().isOp();
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.identity().hasPermission(permission);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.handle.sendMessage(this.platform().mm().deserialize(message));
    }

    @Override
    public void sendActionBar(@NotNull String text) {
        this.handle.sendActionBar(this.platform().mm().deserialize(text));
    }

    @Override
    public @NotNull PaperPlatformBossBar createBossBar(
            @NotNull String text,
            float progress,
            @NotNull PlatformBossBar.Color color,
            @NotNull PlatformBossBar.Overlay overlay
    ) {
        PaperPlatformBossBar ret = PaperPlatformBossBar.create(
                this.platform().mm().deserialize(text),
                progress,
                color,
                overlay
        );
        ret.show(this.handle());
        return ret;
    }

    @Override
    public boolean canBoostElytra() {
        return true;
    }

    @Override
    public void boostElytra(@Nullable PlatformItem item) {
        if (item == null) return;
        this.handle().boostElytra(this.platform.adapter().item(item));
    }

}
