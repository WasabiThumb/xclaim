package io.github.wasabithumb.xclaim.platform.misc;

import io.github.wasabithumb.xclaim.util.MapUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SpigotPlatformBossBar implements PlatformBossBar {

    private static final Map<Color, BossBar.Color> COLOR_MAP =
            MapUtil.enums(Color.class, BossBar.Color.class);

    private static final Map<Overlay, BossBar.Overlay> OVERLAY_MAP =
            MapUtil.enums(Overlay.class, BossBar.Overlay.class);

    public static @NotNull SpigotPlatformBossBar create(
            @NotNull Component text,
            float progress,
            @NotNull Color color,
            @NotNull Overlay overlay
    ) {
        return new SpigotPlatformBossBar(BossBar.bossBar(
                text,
                progress,
                COLOR_MAP.get(color),
                OVERLAY_MAP.get(overlay)
        ));
    }

    //

    private final BossBar handle;
    private final Set<Audience> targets;

    public SpigotPlatformBossBar(@NotNull BossBar handle) {
        this.handle = handle;
        this.targets = Collections.synchronizedSet(new LinkedHashSet<>());
    }

    public @NotNull BossBar handle() {
        return this.handle;
    }

    public void show(@NotNull Audience a) {
        this.targets.add(a);
        a.showBossBar(this.handle);
    }

    public void hide(@NotNull Audience a) {
        this.targets.remove(a);
        a.hideBossBar(this.handle);
    }

    @Override
    public float progress() {
        return this.handle.progress();
    }

    @Override
    public @NotNull SpigotPlatformBossBar progress(float value) {
        this.handle.progress(value);
        return this;
    }

    @Override
    public void remove() {
        synchronized (this.targets) {
            for (Audience target : this.targets)
                target.hideBossBar(this.handle);
            this.targets.clear();
        }
    }

}
