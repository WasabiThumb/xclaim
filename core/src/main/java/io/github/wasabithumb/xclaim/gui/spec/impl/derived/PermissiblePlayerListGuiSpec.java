package io.github.wasabithumb.xclaim.gui.spec.impl.derived;

import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.gui.GuiInstance;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.gui.spec.impl.PlayerListGuiSpec;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.util.ProxyList;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class PermissiblePlayerListGuiSpec extends PlayerListGuiSpec {

    private final Claim claim;
    private final Set<UUID> added = new LinkedHashSet<>();
    public PermissiblePlayerListGuiSpec(@NotNull Claim claim) {
        this.claim = claim;
    }

    @Override
    protected boolean addPlayer(@NotNull GuiInstance instance, @NotNull PlatformUser player) {
        return this.added.add(player.uuid());
    }

    @Override
    protected @NotNull Collection<PlatformUser> getEntries(@NotNull GuiInstance instance) {
        final Set<UUID> intrinsic = this.claim.getUserPermissionKeys();
        final int intrinsicCount = intrinsic.size();
        final List<UUID> backing = new ArrayList<>(intrinsicCount + this.added.size());

        backing.addAll(intrinsic);
        for (UUID ply : this.added) {
            if (intrinsic.contains(ply)) continue;
            backing.add(ply);
        }

        final List<PlatformUser> ret = new ProxyList<>(
                backing,
                instance.platform().users()::getUser,
                PlatformUser::uuid
        );
        ret.subList(0, intrinsicCount).sort(Comparator.comparing((PlatformUser user) -> {
            if (user.isPlayer()) {
                return user.asPlayer().name();
            } else {
                return user.displayName();
            }
        }));
        return ret;
    }

    @Override
    protected @NotNull GuiAction onClickEntry(@NotNull GuiInstance instance, @NotNull PlatformUser entry) {
        return GuiAction.transfer(GuiSpecs.individualPermissionList(this.claim, entry));
    }

    @Override
    protected @NotNull GuiSpec getReturn() {
        return GuiSpecs.permissionOverview(this.claim);
    }

}
