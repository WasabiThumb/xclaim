package io.github.wasabithumb.xclaim.gui2.spec.impl;

import io.github.wasabithumb.xclaim.gui2.GuiInstance;
import io.github.wasabithumb.xclaim.gui2.action.GuiAction;
import io.github.wasabithumb.xclaim.gui2.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui2.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui2.spec.GuiSpecs;
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
                instance.runtime().lang("gui-vinf-version"),
                ColorTag.GOLD
                // TODO: More info!
        ));

        PlatformUser author = instance.platform().users().getUser(OWNER_UUID);
        instance.set(1, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.PLAYER_HEAD),
                instance.runtime().lang("gui-vinf-author"),
                ColorTag.GOLD,
                ColorTag.LIGHT_PURPLE.format("Wasabi_Thumbs"),
                "<click:open_url:'https://wasabithumb.github.io/'><gray>https://wasabithumb.github.io/</gray></click>"
        ).skullOwner(author));

        instance.set(2, DisplayItem.format(
                instance.platform().createItem(NamedPlatformMaterial.BARRIER),
                instance.runtime().lang("gui-vinf-back"),
                ColorTag.RED
        ));
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        if (slot.index() == 2) return GuiAction.transfer(GuiSpecs.main());
        return GuiAction.nothing();
    }

}
