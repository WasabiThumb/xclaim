package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.data.material.BukkitPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class BukkitPlatformItem implements PlatformItem {

    protected final BukkitPlatform platform;
    protected final ItemStack handle;
    BukkitPlatformItem(@NotNull BukkitPlatform platform, @NotNull ItemStack handle) {
        this.platform = platform;
        this.handle = handle;
    }

    @Contract("_ -> this")
    public @NotNull BukkitPlatformItem modifyMeta(@NotNull Consumer<ItemMeta> consumer) {
        ItemMeta meta = this.handle.getItemMeta();
        if (meta == null) meta = Bukkit.getItemFactory().getItemMeta(this.handle.getType());
        consumer.accept(meta);
        this.handle.setItemMeta(meta);
        return this;
    }

    @Override
    public @NotNull ItemStack handle() {
        return this.handle;
    }

    @Override
    public @NotNull PlatformMaterial type() {
        return BukkitPlatformMaterial.of(this.handle.getType());
    }

    @Override
    public int amount() {
        return this.handle.getAmount();
    }

    @Override
    public BukkitPlatformItem hideExtra() {
        return this.modifyMeta((ItemMeta m) -> {
            m.addItemFlags(ItemFlag.values());
        });
    }

}
