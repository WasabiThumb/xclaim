package io.github.wasabithumb.xclaim.platform.misc;

import io.github.wasabithumb.xclaim.util.collections.MapUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record SpongePlatformBossBar(
        BossBar handle,
        Audience viewer
) implements PlatformBossBar {

    private static final Map<Color, BossBar.Color> COLOR_MAP =
            MapUtil.enums(PlatformBossBar.Color.class, BossBar.Color.class);

    private static final Map<Overlay, BossBar.Overlay> OVERLAY_MAP =
            MapUtil.enums(PlatformBossBar.Overlay.class, BossBar.Overlay.class);

    //

    public SpongePlatformBossBar {
        viewer.showBossBar(handle);
    }

    public SpongePlatformBossBar(Component title, float progress, Color color, Overlay overlay, Audience viewer) {
        this(
                BossBar.bossBar(title, progress, COLOR_MAP.get(color), OVERLAY_MAP.get(overlay)),
                viewer
        );
    }

    //

    @Override
    public float progress() {
        return this.handle.progress();
    }

    @Override
    public @NotNull PlatformBossBar progress(float value) {
        this.handle.progress(value);
        return this;
    }

    @Override
    public void remove() {
        this.viewer.hideBossBar(this.handle);
    }

}
