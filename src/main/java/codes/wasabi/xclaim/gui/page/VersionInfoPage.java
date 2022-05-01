package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
        ver.editMeta((ItemMeta meta) -> {
            PluginDescriptionFile description = XClaim.instance.getDescription();
            meta.displayName(Component.text("Version").color(NamedTextColor.GOLD));
            meta.lore(Arrays.asList(
                    Component.text(description.getVersion()).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text("Made for MC Version " + Objects.requireNonNullElse(description.getAPIVersion(), "Unspecified")).color(NamedTextColor.LIGHT_PURPLE)
            ));
        });
        VERSION_STACK = ver;
    }

    private static final ItemStack AUTHOR_STACK;

    static {
        UUID authorID = new UUID(-7814744758566370837L, -7422362746895434695L);
        OfflinePlayer author = Bukkit.getOfflinePlayer(authorID);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        skull.editMeta((ItemMeta meta) -> {
            meta.displayName(Component.text("Author").color(NamedTextColor.GOLD));
            String name = Objects.requireNonNullElse(author.getName(), "Wasabi_Thumbs");
            meta.lore(Collections.singletonList(Component.text(name).color(NamedTextColor.LIGHT_PURPLE)));
        });
        skull.editMeta(SkullMeta.class, (SkullMeta sm) -> sm.setOwningPlayer(author));
        AUTHOR_STACK = skull;
    }

    private static final ItemStack BACK_STACK = DisplayItem.create(Material.BARRIER, "Back", NamedTextColor.RED);

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
        if (slot == 15) switchPage(new MainPage(getParent()));
    }

}
