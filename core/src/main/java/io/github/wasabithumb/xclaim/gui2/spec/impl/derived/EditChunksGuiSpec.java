package io.github.wasabithumb.xclaim.gui2.spec.impl.derived;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.config.struct.sub.WorldsConfig;
import io.github.wasabithumb.xclaim.gui2.GuiInstance;
import io.github.wasabithumb.xclaim.gui2.action.GuiAction;
import io.github.wasabithumb.xclaim.gui2.spec.impl.ClaimSelectorGuiSpec;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.world.PlatformWorld;
import org.jetbrains.annotations.NotNull;

public final class EditChunksGuiSpec extends ClaimSelectorGuiSpec {

    @Override
    protected @NotNull GuiAction onClickClaim(@NotNull GuiInstance instance, @NotNull Claim claim) {
        final PlatformPlayer ply = instance.player();
        final WorldsConfig cfg = instance.runtime().rootConfig().worlds();
        final PlatformWorld w = ply.location().world();

        if (!cfg.checkLists(w)) {
            ply.sendMessage(instance.runtime().lang("gui-edit-chunk-disallowed"));
            return GuiAction.exit();
        }

        final PlatformWorld cw = claim.world();
        if (cw != null && !cw.uuid().equals(w.uuid())) {
            ply.sendMessage(instance.runtime().lang("gui-edit-chunk-fail"));
            return GuiAction.exit();
        }

        instance.runtime().gui().editor().enter(ply, claim);
        return GuiAction.exit();
    }

}
