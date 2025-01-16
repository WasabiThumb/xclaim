package io.github.wasabithumb.xclaim.platform;

import io.github.wasabithumb.xclaim.XClaimPlugin;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.event.SpigotPlatformEventManager;
import io.github.wasabithumb.xclaim.platform.inventory.SpigotPlatformCustomInventory;
import io.github.wasabithumb.xclaim.platform.inventory.SpigotPlatformItem;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.scheduler.BukkitPlatformScheduler;
import io.github.wasabithumb.xclaim.platform.user.BukkitPlatformUserManager;
import io.github.wasabithumb.xclaim.util.SpigotItemSerializer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class SpigotPlatform extends BukkitPlatform {

    private final BukkitAudiences audiences;
    private final MiniMessage mm;
    public SpigotPlatform(@NotNull XClaimPlugin plugin) {
        super(plugin);
        this.audiences = BukkitAudiences.create(plugin);
        this.mm = MiniMessage.miniMessage();
    }

    @ApiStatus.Internal
    public @NotNull BukkitAudiences audiences() {
        return this.audiences;
    }

    @ApiStatus.Internal
    public @NotNull MiniMessage mm() {
        return this.mm;
    }

    @Override
    protected @NotNull SpigotPlatformTypeAdapter createAdapter() {
        return new SpigotPlatformTypeAdapter(this);
    }

    @Override
    protected @NotNull BukkitPlatformScheduler createScheduler() {
        return BukkitPlatformScheduler.legacy(this.plugin);
    }

    @Override
    protected @NotNull BukkitPlatformUserManager createUsers() {
        return new BukkitPlatformUserManager(this);
    }

    @Override
    protected @NotNull SpigotPlatformEventManager createEvents() {
        return new SpigotPlatformEventManager(this);
    }

    @Override
    public @NotNull SpigotPlatformItem createItem(@NotNull PlatformMaterial material, int amount) {
        return new SpigotPlatformItem(this, new ItemStack(this.adapter.material(material), amount));
    }

    @Override
    public @NotNull PlatformItem createItem(byte @NotNull [] bytes) {
        return new SpigotPlatformItem(this, SpigotItemSerializer.deserialize(bytes));
    }

    @Override
    public @NotNull <D> SpigotPlatformCustomInventory<D> createInventory(int size, @NotNull String name, @NotNull D customData) {
        return new SpigotPlatformCustomInventory<>(this, size, name, customData);
    }

    @Override
    public @NotNull SpigotPlatformTypeAdapter adapter() {
        return (SpigotPlatformTypeAdapter) super.adapter();
    }

}
