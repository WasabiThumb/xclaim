package io.github.wasabithumb.xclaim.gui.spec.impl;

import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import io.github.wasabithumb.xclaim.util.ColorTag;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class VersionInfoGuiSpec implements GuiSpec {

    private static final UUID OWNER_UUID = UUID.fromString("938c730b-df4e-41eb-98fe-786835347c39");

    //

    @Override
    public @NotNull String layout() {
        return "version-info";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        instance.set(0, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.BOOK),
                instance.runtime().lang(I18N.GUI_VINF_VERSION),
                ColorTag.GOLD
                // TODO: More info!
        ));

        PlatformUser author = instance.platform().users().getUser(OWNER_UUID);
        instance.set(1, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.PLAYER_HEAD),
                instance.runtime().lang(I18N.GUI_VINF_AUTHOR),
                ColorTag.GOLD,
                ColorTag.LIGHT_PURPLE.format("Wasabi_Thumbs"),
                "<click:open_url:'https://wasabithumb.github.io/'><gray>https://wasabithumb.github.io/</gray></click>"
        ).skullOwner(author));

        instance.set(2, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.BARRIER),
                instance.runtime().lang(I18N.GUI_VINF_BACK),
                ColorTag.RED
        ));
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        if (slot.index() == 2) return GuiAction.transfer(GuiSpecs.main());
        return GuiAction.nothing();
    }

}
