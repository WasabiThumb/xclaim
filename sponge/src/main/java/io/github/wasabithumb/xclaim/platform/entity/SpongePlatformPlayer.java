package io.github.wasabithumb.xclaim.platform.entity;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.data.sound.PlatformSound;
import io.github.wasabithumb.xclaim.platform.data.sound.SpongePlatformSound;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.inventory.SpongePlatformCustomInventory;
import io.github.wasabithumb.xclaim.platform.inventory.SpongePlatformInventory;
import io.github.wasabithumb.xclaim.platform.misc.PlatformBossBar;
import io.github.wasabithumb.xclaim.platform.misc.SpongePlatformBossBar;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Color;
import org.spongepowered.math.vector.Vector3d;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpongePlatformPlayer extends SpongePlatformEntity implements PlatformPlayer {

    public SpongePlatformPlayer(@NotNull SpongePlatform platform, @NotNull ServerPlayer handle) {
        super(platform, handle);
    }

    //

    @Override
    public @NotNull ServerPlayer handle() {
        return (ServerPlayer) super.handle();
    }

    //


    @Override
    public @NotNull String name() {
        return this.handle().name();
    }

    @Override
    public @NotNull PlatformInventory getInventory() {
        return new SpongePlatformInventory(this.platform, this.handle().inventory());
    }

    @Override
    public int getHeldItemSlot() {
        return this.handle().inventory().hotbar().selectedSlotIndex();
    }

    @Override
    public void sendActionBar(@NotNull String text) {
        this.handle().sendActionBar(this.platform.mm().deserialize(text));
    }

    @Override
    public @NotNull PlatformBossBar createBossBar(
            @NotNull String text,
            float progress,
            @NotNull PlatformBossBar.Color color,
            @NotNull PlatformBossBar.Overlay overlay
    ) {
        return new SpongePlatformBossBar(
                this.platform.mm().deserialize(text),
                progress,
                color,
                overlay,
                this.handle()
        );
    }

    @Override
    public void playSound(@NotNull PlatformSound sound) {
        Sound s = Sound.sound(
                SpongePlatformSound.adapt(sound),
                Sound.Source.MASTER,
                1f,
                1f
        );
        this.handle().playSound(s);
    }

    @Override
    public boolean isGliding() {
        return this.handle().get(Keys.IS_ELYTRA_FLYING).orElse(false);
    }

    @Override
    public boolean canBoostElytra() {
        return false;
    }

    @Override
    public void boostElytra(@Nullable PlatformItem item) { }

    @Override
    public void openBook(@Nullable PlatformItem item) {
        if (item == null) return;

        ItemStack is = (ItemStack) item.handle();
        Book.Builder b = Book.builder();

        is.get(Keys.DISPLAY_NAME)
                .ifPresent(b::title);

        is.get(Keys.AUTHOR)
                .ifPresent(b::author);

        List<Component> pages;
        if (is.supports(Keys.PAGES)) {
            pages = is.get(Keys.PAGES)
                    .orElseGet(Collections::emptyList);
        } else {
            pages = is.get(Keys.PLAIN_PAGES)
                    .map((List<String> plain) -> {
                        List<Component> ret = new ArrayList<>(plain.size());
                        for (String page : plain)
                            ret.add(Component.text(page));
                        return ret;
                    })
                    .orElseGet(Collections::emptyList);
        }
        b.pages(pages);

        this.handle().openBook(b.build());
    }

    @Override
    public void openInventory(@NotNull PlatformInventory inventory) {
        SpongePlatformInventory spi = (SpongePlatformInventory) inventory;
        if (spi instanceof SpongePlatformCustomInventory<?> custom) {
            if (this.handle().openInventory(custom.asViewable(), custom.title()).isPresent()) return;
            this.handle().openInventory(custom.asViewable());
        } else {
            this.handle().openInventory(spi.handle());
        }
    }

    @Override
    public void closeInventory() {
        this.handle().closeInventory();
    }

    @Override
    public long getFirstPlayed() {
        return this.handle().get(Keys.FIRST_DATE_JOINED)
                .map(Instant::toEpochMilli)
                .orElse(0L);
    }

    @Override
    public void sendRedstoneParticle(int rgb, double x, double y, double z) {
        ParticleEffect pe = ParticleEffect.builder()
                .type(ParticleTypes.DUST.get())
                .offset(new Vector3d(0.02d, 0.02d, 0.02d))
                .quantity(1)
                .option(ParticleOptions.COLOR.get(), Color.ofRgb(rgb))
                .build();

        this.handle().spawnParticles(pe, new Vector3d(x, y, z));
    }

    //

    @Override
    public @NotNull String displayName() {
        return this.platform.mm().serialize(this.handle().displayName().get());
    }

    @Override
    public boolean isOffline() {
        return !this.handle().isOnline();
    }

    @Override
    @Contract("-> true")
    public boolean isPlayer() {
        return true;
    }

    @Override
    @Contract("-> this")
    public @NotNull SpongePlatformPlayer asPlayer() {
        return this;
    }

    @Override
    public boolean isOp() {
        return this.handle().hasPermission("*");
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.handle().hasPermission(permission);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.handle().sendMessage(this.platform.mm().deserialize(message));
    }

}
