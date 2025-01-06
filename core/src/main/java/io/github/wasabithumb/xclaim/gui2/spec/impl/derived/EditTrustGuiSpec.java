package io.github.wasabithumb.xclaim.gui2.spec.impl.derived;

import io.github.wasabithumb.xclaim.api.XCPlayer;
import io.github.wasabithumb.xclaim.gui2.GuiInstance;
import io.github.wasabithumb.xclaim.gui2.action.GuiAction;
import io.github.wasabithumb.xclaim.gui2.spec.impl.PlayerListGuiSpec;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUserManager;
import io.github.wasabithumb.xclaim.trust.TrustSet;
import io.github.wasabithumb.xclaim.util.ProxySet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class EditTrustGuiSpec extends PlayerListGuiSpec {

    @Override
    protected @NotNull Collection<PlatformUser> getEntries(@NotNull GuiInstance instance) {
        TrustSet trusted = instance.runtime()
                .trust()
                .get(instance.player().uuid());

        PlatformUserManager users = instance.platform().users();

        return new ProxySet<>(
                PlatformUser.class,
                trusted,
                users::getUser,
                PlatformUser::uuid
        );
    }

    @Override
    protected boolean addPlayer(@NotNull GuiInstance instance, @NotNull PlatformUser player) {
        instance.runtime().trust().trust(instance.player().uuid(), player.uuid());
        return true;
    }

    @Override
    protected @NotNull GuiAction onClickEntry(@NotNull GuiInstance instance, @NotNull PlatformUser player) {
        instance.runtime().trust().untrust(instance.player().uuid(), player.uuid());
        return GuiAction.repopulate();
    }

}
