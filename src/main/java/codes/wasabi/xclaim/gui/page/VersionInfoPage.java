package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class VersionInfoPage extends Page {

    private static final ItemStack VERSION_STACK;

    static {
        ItemStack ver = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = ver.getItemMeta();
        if (meta != null) {
            PluginDescriptionFile description = XClaim.instance.getDescription();
            Platform p = Platform.get();
            p.metaDisplayName(meta, Component.text(XClaim.lang.get("gui-vinf-version")).color(NamedTextColor.GOLD));
            p.metaLore(meta, Arrays.asList(
                    Component.text(description.getVersion()).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text(XClaim.lang.get("gui-vinf-mc-version", Objects.requireNonNullElse(Platform.get().getApiVersion(description), XClaim.lang.get("gui-vinf-mc-version-unspecified")))).color(NamedTextColor.LIGHT_PURPLE)
            ));
        }
        ver.setItemMeta(meta);
        VERSION_STACK = ver;
    }

    private static final ItemStack AUTHOR_STACK;

    static {
        UUID authorID = new UUID(-7814744758566370837L, -7422362746895434695L);
        OfflinePlayer author = Bukkit.getOfflinePlayer(authorID);
        ItemStack skull = Platform.get().preparePlayerSkull(new ItemStack(Platform.get().getPlayerHeadMaterial(), 1));
        ItemMeta meta = skull.getItemMeta();
        if (meta != null) {
            Platform p = Platform.get();
            p.metaDisplayName(meta, Component.text(XClaim.lang.get("gui-vinf-author")).color(NamedTextColor.GOLD));
            String name = Objects.requireNonNullElse(author.getName(), "Wasabi_Thumbs");
            p.metaLore(meta, Collections.singletonList(Component.text(name).color(NamedTextColor.LIGHT_PURPLE)));
            if (meta instanceof SkullMeta) Platform.get().setOwningPlayer((SkullMeta) meta, author);
        }
        skull.setItemMeta(meta);
        AUTHOR_STACK = skull;
    }

    private static final ItemStack BACK_STACK = DisplayItem.create(Material.BARRIER, XClaim.lang.get("gui-vinf-back"), NamedTextColor.RED);

    public VersionInfoPage(@NotNull GUIHandler parent) {
        super(parent);
    }

    @Override
    public void onEnter() {
        // 012345678
        //   a b c
        clear();
        setItem(11, VERSION_STACK);
        setItem(13, AUTHOR_STACK);
        setItem(15, BACK_STACK);
    }

    @Override
    public void onClick(int slot) {
        if (slot == 15) {
            switchPage(new MainPage(getParent()));
        } else if (slot == 13) {
            getTarget().playSound(getTarget().getLocation(), Sound.ENTITY_GHAST_AMBIENT, 1f, 1f);
        }
    }

}
