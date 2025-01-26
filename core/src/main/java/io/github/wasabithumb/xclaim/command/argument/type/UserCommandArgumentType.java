package io.github.wasabithumb.xclaim.command.argument.type;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.util.collections.ProxyList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@ApiStatus.Internal
final class UserCommandArgumentType implements CommandArgumentType<PlatformUser> {

    @Override
    public @NotNull Translatable name() {
        return I18N.ARG_OFFLINE_PLAYER_NAME;
    }

    @Override
    public @NotNull Class<PlatformUser> typeClass() {
        return PlatformUser.class;
    }

    @Override
    public @NotNull ParseResult<PlatformUser> parse(@NotNull XClaim runtime, @NotNull PlatformUser user, @NotNull String input) {
        return ParseResult.ofNullable(
                runtime.platform().users().matchUser(input),
                "User not found"
        );
    }

    @Override
    public @NotNull @Unmodifiable List<String> suggest(@NotNull XClaim runtime, @NotNull PlatformUser user) {
        List<PlatformPlayer> players = runtime.platform().users().players();
        return new ProxyList<>(players, PlatformPlayer::name);
    }

}
