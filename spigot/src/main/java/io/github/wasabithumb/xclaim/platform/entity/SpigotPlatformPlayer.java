package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.SpigotPlatform;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.misc.SpigotPlatformBossBar;
import io.github.wasabithumb.xclaim.platform.misc.PlatformBossBar;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpigotPlatformPlayer extends BukkitPlatformPlayer {

    public SpigotPlatformPlayer(@NotNull SpigotPlatform platform, @NotNull Player handle) {
        super(platform, handle);
    }

    protected @NotNull SpigotPlatform platform() {
        return (SpigotPlatform) this.platform;
    }

    protected @NotNull Audience audience() {
        return this.platform().audiences().player(this.handle());
    }

    @Override
    public @NotNull String displayName() {
        return this.platform().mm().serialize(LegacyComponentSerializer.legacySection().deserialize(
                this.handle().getDisplayName()
        ));
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.audience().sendMessage(this.platform().mm().deserialize(message));
    }

    @Override
    public void sendActionBar(@NotNull String text) {
        this.audience().sendActionBar(this.platform().mm().deserialize(text));
    }

    @Override
    public @NotNull SpigotPlatformBossBar createBossBar(
            @NotNull String text,
            float progress,
            @NotNull PlatformBossBar.Color color,
            @NotNull PlatformBossBar.Overlay overlay
    ) {
        SpigotPlatformBossBar ret = SpigotPlatformBossBar.create(
                this.platform().mm().deserialize(text),
                progress,
                color,
                overlay
        );
        ret.show(this.audience());
        return ret;
    }

    @Override
    public boolean canBoostElytra() {
        return false;
    }

    @Override
    public void boostElytra(@Nullable PlatformItem item) { }

}
