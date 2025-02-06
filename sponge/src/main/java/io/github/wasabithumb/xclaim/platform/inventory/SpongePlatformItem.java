package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.data.material.PlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.material.SpongePlatformMaterial;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.registry.RegistryTypes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SpongePlatformItem implements PlatformItem {

    @ApiStatus.Internal
    public static @NotNull SpongePlatformItem fromBytes(SpongePlatform platform, byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             Reader r = new InputStreamReader(bis, StandardCharsets.UTF_8)
        ) {
            int c;

            StringBuilder key = new StringBuilder();
            while ((c = r.read()) != '\0') {
                if (c == -1) throw new EOFException();
                key.append((char) c);
            }

            ItemType type = Sponge.game().registry(RegistryTypes.ITEM_TYPE)
                    .value(ResourceKey.resolve(key.toString()));

            int quantity = 0;
            while ((c = r.read()) != '\0') {
                if (c == -1) throw new EOFException();
                quantity *= 10;
                quantity += Character.digit((char) c, 10);
            }

            DataContainer data = DataFormats.SNBT.get().readFrom(r);

            ItemStack item = ItemStack.of(type, quantity);
            item.setRawData(data);
            return new SpongePlatformItem(platform, item);
        } catch (IOException e) {
            platform.logger().log(Level.WARN, "Failed to deserialize item", e);
            return new SpongePlatformItem(platform, ItemStack.of(ItemTypes.AIR.get(), 0));
        }
    }

    //

    private final SpongePlatform platform;
    private final ItemStack handle;

    public SpongePlatformItem(
            @NotNull SpongePlatform platform,
            @NotNull ItemStack handle
    ) {
        this.platform = platform;
        this.handle = handle;
    }

    //

    @Override
    public @NotNull ItemStack handle() {
        return this.handle;
    }

    @Override
    public @NotNull PlatformMaterial type() {
        return SpongePlatformMaterial.adaptItem(this.handle.type());
    }

    @Override
    public int amount() {
        return this.handle.quantity();
    }

    @Override
    public @NotNull String displayName() {
        Component c = this.handle.get(Keys.DISPLAY_NAME)
                .orElseGet(this.handle::asComponent);
        return this.platform.mm().serialize(c);
    }

    @Override
    public PlatformItem displayName(@NotNull String displayName) {
        this.handle.offer(Keys.CUSTOM_NAME, this.platform.mm().deserialize(displayName));
        return this;
    }

    @Override
    public @NotNull List<String> lore() {
        List<Component> lore = this.handle.get(Keys.LORE)
                .orElseGet(Collections::emptyList);

        List<String> ret = new ArrayList<>(lore.size());
        for (Component line : lore) {
            ret.add(this.platform.mm().serialize(line));
        }
        return Collections.unmodifiableList(ret);
    }

    @Override
    public PlatformItem lore(@NotNull List<String> lore) {
        List<Component> parsed = new ArrayList<>(lore.size());
        for (String line : lore) {
            parsed.add(this.platform.mm().deserialize(line));
        }
        this.handle.offer(Keys.LORE, parsed);
        return this;
    }

    @Override
    public PlatformItem skullOwner(@Nullable PlatformUser user) {
        GameProfile gp;
        if (user != null && user.handle() instanceof User u) {
            if (u instanceof Player p) {
                gp = p.profile();
            } else {
                gp = u.profile();
            }
        } else {
            gp = null;
        }
        this.handle.offer(Keys.GAME_PROFILE, gp);
        return this;
    }

    @Override
    public PlatformItem hideExtra() {
        this.handle.offer(Keys.HIDE_CAN_DESTROY, true);
        this.handle.offer(Keys.HIDE_ATTRIBUTES, true);
        this.handle.offer(Keys.HIDE_ENCHANTMENTS, true);
        this.handle.offer(Keys.HIDE_UNBREAKABLE, true);
        this.handle.offer(Keys.HIDE_MISCELLANEOUS, true);
        this.handle.offer(Keys.HIDE_CAN_PLACE, true);
        return this;
    }

    @Override
    public PlatformItem holographic() {
        List<Enchantment> enchants = this.handle.get(Keys.APPLIED_ENCHANTMENTS)
                .map(ArrayList::new)
                .orElseGet(ArrayList::new);

        enchants.add(Enchantment.of(EnchantmentTypes.SHARPNESS.get(), 1));

        this.handle.offer(Keys.APPLIED_ENCHANTMENTS, enchants);
        return this;
    }

    @Override
    public byte @NotNull [] toBytes() {
        byte[] ret;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             Writer w = new OutputStreamWriter(bos, StandardCharsets.UTF_8)
        ) {
            w.write(this.handle.type().key(RegistryTypes.ITEM_TYPE).formatted());
            w.write('\0');
            w.write(Integer.toString(this.handle.quantity()));
            w.write('\0');
            DataFormats.SNBT.get().writeTo(w, this.handle.toContainer());

            w.flush();
            ret = bos.toByteArray();
        } catch (IOException e) {
            this.platform.logger().log(Level.WARN, "Failed to serialize item", e);
            return new byte[0];
        }
        return ret;
    }

    @Override
    public boolean isConsumable() {
        return this.handle.supports(Keys.REPLENISHED_FOOD) &&
                this.handle.get(Keys.REPLENISHED_FOOD).isPresent();
    }

}
