package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.PaperPlatform;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PaperPlatformItem extends BukkitPlatformItem {

    private static final Method M_ITEM_META_GLINT;
    static {
        Method m1 = null;
        try {
            m1 = ItemMeta.class.getMethod("setEnchantmentGlintOverride", Boolean.class);
        } catch (ReflectiveOperationException | SecurityException ignored) { }
        M_ITEM_META_GLINT = m1;
    }

    //

    public PaperPlatformItem(@NotNull PaperPlatform platform, @NotNull ItemStack handle) {
        super(platform, handle);
    }

    //

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
    public byte @NotNull [] toBytes() {
        return this.handle.serializeAsBytes();
    }

    @Override
    public boolean isConsumable() {
        try {
            Method m1 = ItemStack.class.getMethod("getDataTypes");
            Object types = m1.invoke(this.handle);

            Class<?> c2 = Class.forName("io.papermc.paper.datacomponent.DataComponentTypes");
            Field f1 = c2.getField("CONSUMABLE");
            Object type = f1.get(null);

            Method m2 = Set.class.getMethod("contains", Object.class);
            return (Boolean) m2.invoke(types, type);
        } catch (ReflectiveOperationException | SecurityException ignored) { }

        return this.handle.getType().isEdible();
    }

    @Override
    public PaperPlatformItem holographic() {
        if (M_ITEM_META_GLINT == null) {
            super.holographic();
            return this;
        }
        this.modifyMeta((ItemMeta meta) -> {
            try {
                M_ITEM_META_GLINT.invoke(meta, Boolean.TRUE);
            } catch (ReflectiveOperationException | SecurityException e) {
                throw new AssertionError(e);
            }
        });
        return this;
    }

}
