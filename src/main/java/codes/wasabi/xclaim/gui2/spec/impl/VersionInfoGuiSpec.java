package codes.wasabi.xclaim.gui2.spec.impl;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import codes.wasabi.xclaim.gui2.spec.GuiSpec;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public final class VersionInfoGuiSpec implements GuiSpec {

    private static final UUID OWNER_UUID = UUID.fromString("938c730b-df4e-41eb-98fe-786835347c39");
    private static final String OWNER_NAME = "Wasabi_Thumbs";
    private static final ItemStack VERSION_STACK;
    private static final ItemStack AUTHOR_STACK;
    static {
        VERSION_STACK = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = VERSION_STACK.getItemMeta();
        if (meta != null) {
            PluginDescriptionFile description = XClaim.instance.getDescription();
            Platform p = Platform.get();
            p.metaDisplayName(meta, Component.text(XClaim.lang.get("gui-vinf-version")).color(NamedTextColor.GOLD));
            String apiVersion = Platform.get().getApiVersion(description);
            if (apiVersion == null) apiVersion = XClaim.lang.get("gui-vinf-mc-version-unspecified");
            p.metaLore(meta, Arrays.asList(
                    Component.text(description.getVersion()).color(NamedTextColor.LIGHT_PURPLE),
                    Component.text(XClaim.lang.get("gui-vinf-mc-version", apiVersion)).color(NamedTextColor.LIGHT_PURPLE)
            ));
        }
        VERSION_STACK.setItemMeta(meta);

        AUTHOR_STACK = Platform.get().preparePlayerSkull(new ItemStack(Platform.get().getPlayerHeadMaterial(), 1));
        meta = AUTHOR_STACK.getItemMeta();
        if (meta != null) {
            Platform p = Platform.get();
            p.metaDisplayName(meta, Component.text(XClaim.lang.get("gui-vinf-author")).color(NamedTextColor.GOLD));
            p.metaLore(meta, Collections.singletonList(Component.text(OWNER_NAME).color(NamedTextColor.LIGHT_PURPLE)));
            if (meta instanceof SkullMeta) {
                Platform.get().setOwningPlayer((SkullMeta) meta, OWNER_UUID, OWNER_NAME);
            }
        }
        AUTHOR_STACK.setItemMeta(meta);
    }
    private static final ItemStack BACK_STACK = DisplayItem.create(Material.BARRIER, XClaim.lang.get("gui-vinf-back"), NamedTextColor.RED);

    //

    @Override
    public @NotNull String layout() {
        return "version-info";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        instance.set(0, VERSION_STACK);
        instance.set(1, AUTHOR_STACK);
        instance.set(2, BACK_STACK);
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        if (slot.index() == 2) return GuiAction.transfer(GuiSpecs.main());
        return GuiAction.nothing();
    }

}
