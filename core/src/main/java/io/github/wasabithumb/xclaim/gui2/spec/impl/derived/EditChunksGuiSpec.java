package io.github.wasabithumb.xclaim.gui2.spec.impl.derived;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.api.Claim;
import io.github.wasabithumb.xclaim.gui.ChunkEditor;
import io.github.wasabithumb.xclaim.gui2.GuiInstance;
import io.github.wasabithumb.xclaim.gui2.action.GuiAction;
import io.github.wasabithumb.xclaim.gui2.spec.impl.ClaimSelectorGuiSpec;
import io.github.wasabithumb.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class EditChunksGuiSpec extends ClaimSelectorGuiSpec {

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
