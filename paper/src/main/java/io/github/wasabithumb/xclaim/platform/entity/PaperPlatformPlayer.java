package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.misc.PaperPlatformBossBar;
import io.github.wasabithumb.xclaim.platform.misc.PlatformBossBar;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperPlatformPlayer extends BukkitPlatformPlayer {

    public PaperPlatformPlayer(@NotNull PaperPlatform platform, @NotNull Player handle) {
        super(platform, handle);
    }

    protected @NotNull PaperPlatform platform() {
        return (PaperPlatform) this.platform;
    }

    @Override
    public @NotNull String displayName() {
        return this.platform().mm().serialize(this.handle().displayName());
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
