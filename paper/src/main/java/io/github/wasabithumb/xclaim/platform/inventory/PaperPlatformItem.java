package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.user.PlatformConsoleUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformOfflineUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PaperPlatformItem extends BukkitPlatformItem {

    public PaperPlatformItem(@NotNull PaperPlatform platform, @NotNull ItemStack handle) {
        super(platform, handle);
    }

    protected @NotNull PaperPlatform platform() {
        return (PaperPlatform) this.platform;
    }

    @Override
    public @NotNull String displayName() {
        return this.platform().mm().serialize(this.handle.displayName());
    }

    @Override
    public PaperPlatformItem displayName(@NotNull String displayName) {
        this.modifyMeta((ItemMeta m) -> m.displayName(this.platform().mm().deserialize(displayName)));
        return this;
    }

    @Override
    public @NotNull List<String> lore() {
        final List<Component> lore = this.handle.lore();
        if (lore == null) return Collections.emptyList();
        final int len = lore.size();
        if (len == 0) return Collections.emptyList();

        final String[] ret = new String[len];
        for (int i=0; i < len; i++) {
            ret[i] = this.platform().mm().serialize(lore.get(i));
        }
        //noinspection Java9CollectionFactory
        return Collections.unmodifiableList(Arrays.asList(ret));
    }

    @Override
    public PaperPlatformItem lore(@NotNull List<String> lore) {
        final int len = lore.size();
        final Component[] parsed = new Component[len];

        for (int i=0; i < len; i++) {
            parsed[i] = this.platform().mm().deserialize(lore.get(i));
        }

        this.handle.lore(Arrays.asList(parsed));
        return this;
    }

    @Override
    public PaperPlatformItem skullOwner(@Nullable PlatformUser user) {
        this.modifyMeta((ItemMeta meta) -> {
            if (!(meta instanceof SkullMeta sm)) return;
            if (user instanceof PlatformPlayer ply) {
                sm.setOwningPlayer(this.platform().adapter().player(ply));
            } else if (user instanceof PlatformOfflineUser offline) {
                sm.setOwningPlayer(this.platform.adapter().offlineUser(offline));
            } else {
                sm.setOwningPlayer(null);
            }
        });
        return this;
    }

    @Override
    public byte @NotNull [] toBytes() {
        return this.handle.serializeAsBytes();
    }

}
