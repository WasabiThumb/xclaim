package codes.wasabi.xclaim.gui2.spec.impl.derived;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.spec.GuiSpec;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
import codes.wasabi.xclaim.gui2.spec.impl.PlayerListGuiSpec;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class PermissiblePlayerListGuiSpec extends PlayerListGuiSpec {

    private final Claim claim;
    private final Set<XCPlayer> added = new LinkedHashSet<>();
    public PermissiblePlayerListGuiSpec(@NotNull Claim claim) {
        this.claim = claim;
    }

    @Override
    protected boolean addPlayer(@NotNull GuiInstance instance, @NotNull OfflinePlayer player) {
        return this.added.add(XCPlayer.of(player));
    }

    @Override
    protected @NotNull Collection<OfflinePlayer> getEntries(@NotNull GuiInstance instance) {
        final Set<XCPlayer> intrinsic = this.claim.getUserPermissions().keySet();
        final int intrinsicCount = intrinsic.size();
        final List<OfflinePlayer> ret = new ArrayList<>(intrinsicCount + this.added.size());

        for (XCPlayer ply : intrinsic) ret.add(ply.getOfflinePlayer());
        for (XCPlayer ply : this.added) {
            if (intrinsic.contains(ply)) continue;
            ret.add(ply.getOfflinePlayer());
        }

        ret.subList(0, intrinsicCount).sort(Comparator.comparing(OfflinePlayer::getName));
        return ret;
    }

    @Override
    protected @NotNull GuiAction onClickEntry(@NotNull GuiInstance instance, @NotNull OfflinePlayer entry) {
        return GuiAction.transfer(GuiSpecs.individualPermissionList(this.claim, entry));
    }

    @Override
    protected @NotNull GuiSpec getReturn() {
        return GuiSpecs.permissionOverview(this.claim);
    }

}
