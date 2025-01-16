package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.data.material.BukkitPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.user.PlatformOfflineUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        if (meta != null) consumer.accept(meta);
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
        return this.modifyMeta((ItemMeta m) -> m.addItemFlags(ItemFlag.values()));
    }

    @Override
    public BukkitPlatformItem holographic() {
        return this.modifyMeta((ItemMeta m) -> m.addEnchant(Enchantment.DAMAGE_ALL, 1, true));
    }

    @Override
    public BukkitPlatformItem skullOwner(@Nullable PlatformUser user) {
        this.modifyMeta((ItemMeta meta) -> {
            if (!(meta instanceof SkullMeta sm)) return;
            if (user instanceof PlatformPlayer ply) {
                sm.setOwningPlayer(this.platform.adapter().player(ply));
            } else if (user instanceof PlatformOfflineUser offline) {
                sm.setOwningPlayer(this.platform.adapter().offlineUser(offline));
            } else {
                sm.setOwningPlayer(null);
            }
        });
        return this;
    }

}
