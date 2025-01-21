package io.github.wasabithumb.xclaim.gui.spec.impl;

import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import org.jetbrains.annotations.NotNull;

public final class MainGuiSpec implements GuiSpec {

    @Override
    public @NotNull String layout() {
        return "main";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        instance.set(0, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.NETHER_STAR),
                instance.runtime().lang(I18N.GUI_MAIN_NEW)
        ));
        instance.set(1, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.SKELETON_SKULL),
                instance.runtime().lang(I18N.GUI_MAIN_EDIT_TRUST)
        ));
        instance.set(2, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.CRAFTING_TABLE),
                instance.runtime().lang(I18N.GUI_MAIN_EDIT_CHUNK)
        ));
        instance.set(3, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.NAME_TAG),
                instance.runtime().lang(I18N.GUI_MAIN_RENAME_CHUNK)
        ));
        instance.set(4, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.SHIELD),
                instance.runtime().lang(I18N.GUI_MAIN_EDIT_PERM)
        ));
        instance.set(5, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.CHEST_MINECART),
                instance.runtime().lang(I18N.GUI_MAIN_TRANSFER_OWNER)
        ));
        instance.set(6, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.TNT),
                instance.runtime().lang(I18N.GUI_MAIN_CLEAR_ALL)
        ));
        instance.set(7, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.BARRIER),
                instance.runtime().lang(I18N.GUI_MAIN_DELETE)
        ));
        instance.set(8, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.ENCHANTING_TABLE),
                instance.runtime().lang(I18N.GUI_MAIN_VERSION)
        ));
        instance.set(9, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.ARROW),
                instance.runtime().lang(I18N.GUI_MAIN_EXIT)
        ));
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        return switch (slot.index()) {
            case 0 -> GuiAction.transfer(GuiSpecs.newClaim());
            case 1 -> GuiAction.transfer(GuiSpecs.editTrust());
            case 2 -> GuiAction.transfer(GuiSpecs.editChunks());
            case 3 -> GuiAction.transfer(GuiSpecs.renameClaim());
            case 4 -> GuiAction.transfer(GuiSpecs.editPerms());
            case 5 -> GuiAction.transfer(GuiSpecs.transferableClaimSelector());
            case 6 -> GuiAction.transfer(GuiSpecs.clearAll());
            case 7 -> GuiAction.transfer(GuiSpecs.deletingClaimSelector());
            case 8 -> GuiAction.transfer(GuiSpecs.versionInfo());
            case 9 -> GuiAction.exit();
            default -> GuiAction.nothing();
        };
    }

}
