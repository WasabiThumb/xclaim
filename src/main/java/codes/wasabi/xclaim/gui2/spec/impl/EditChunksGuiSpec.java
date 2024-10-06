package codes.wasabi.xclaim.gui2.spec.impl;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.spec.helper.ClaimSelectorGuiSpec;
import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EditChunksGuiSpec extends ClaimSelectorGuiSpec {

    @Override
    protected @NotNull GuiAction onClickClaim(@NotNull GuiInstance instance, @NotNull Claim claim) {
        final Player ply = instance.player();
        final World w = ply.getWorld();
        final Audience audience = Platform.getAdventure().player(ply);

        if (!XClaim.mainConfig.worlds().checkLists(w)) {
            audience.sendMessage(XClaim.lang.getComponent("gui-edit-chunk-disallowed"));
            return GuiAction.exit();
        }

        final World cw = claim.getWorld();
        if (cw != null && !w.getUID().equals(cw.getUID())) {
            audience.sendMessage(XClaim.lang.getComponent("gui-edit-chunk-fail"));
            return GuiAction.exit();
        }

        ChunkEditor.startEditing(ply, claim);
        return GuiAction.exit();
    }

}
