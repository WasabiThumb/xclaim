package io.github.wasabithumb.xclaim.platform;

import io.github.wasabithumb.xclaim.XClaimPlugin;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.event.PaperPlatformEventManager;
import io.github.wasabithumb.xclaim.platform.inventory.PaperPlatformCustomInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PaperPlatformItem;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.scheduler.BukkitPlatformScheduler;
import io.github.wasabithumb.xclaim.platform.scheduler.FoliaPlatformScheduler;
import io.github.wasabithumb.xclaim.platform.scheduler.FoliaPlatformSchedulerReflection;
import io.github.wasabithumb.xclaim.platform.user.PaperPlatformUserManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class PaperPlatform extends BukkitPlatform {

    private final MiniMessage mm = MiniMessage.miniMessage();
    public PaperPlatform(@NotNull XClaimPlugin plugin) {
        super(plugin);
    }

    @ApiStatus.Internal
    public @NotNull MiniMessage mm() {
        return this.mm;
    }

    @Override
    protected @NotNull PaperPlatformTypeAdapter createAdapter() {
        return new PaperPlatformTypeAdapter(this);
    }

    @Override
    protected @NotNull BukkitPlatformScheduler createScheduler() {
        FoliaPlatformSchedulerReflection folia = FoliaPlatformSchedulerReflection.tryInit();
        if (folia != null) return new FoliaPlatformScheduler(this.plugin, folia);
        return BukkitPlatformScheduler.legacy(this.plugin);
    }

    @Override
    protected @NotNull PaperPlatformUserManager createUsers() {
        return new PaperPlatformUserManager(this);
    }

    @Override
    protected @NotNull PaperPlatformEventManager createEvents() {
        return new PaperPlatformEventManager(this);
    }

    @Override
    public @NotNull PaperPlatformItem createItem(@NotNull PlatformMaterial material, int amount) {
        return new PaperPlatformItem(this, new ItemStack(this.adapter.material(material), amount));
    }

    @Override
    public @NotNull PlatformItem createItem(byte @NotNull [] bytes) {
        return new PaperPlatformItem(this, ItemStack.deserializeBytes(bytes));
    }

    @Override
    public @NotNull <D> PaperPlatformCustomInventory<D> createInventory(int size, @NotNull String name, @NotNull D customData) {
        return new PaperPlatformCustomInventory<>(this, size, name, customData);
    }

    @Override
    public @NotNull PaperPlatformTypeAdapter adapter() {
        return (PaperPlatformTypeAdapter) super.adapter();
    }
}
