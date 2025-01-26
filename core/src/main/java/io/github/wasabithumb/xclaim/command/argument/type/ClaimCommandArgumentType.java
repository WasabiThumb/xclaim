package io.github.wasabithumb.xclaim.command.argument.type;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

@ApiStatus.Internal
final class ClaimCommandArgumentType implements CommandArgumentType<Claim> {

    private final BiPredicate<Claim, PlatformUser> condition;

    ClaimCommandArgumentType(@NotNull BiPredicate<Claim, PlatformUser> condition) {
        this.condition = condition;
    }

    //

    @Override
    public @NotNull Translatable name() {
        return I18N.ARG_STRING_NAME; // TODO
    }

    @Override
    public @NotNull Class<Claim> typeClass() {
        return Claim.class;
    }

    @Override
    public @NotNull ParseResult<Claim> parse(@NotNull XClaim runtime, @NotNull PlatformUser user, @NotNull String input) {
        return ParseResult.ofNullable(
                runtime.claims().getByName(input),
                "No claim exists by that name" // TODO
        );
    }

    @Override
    public @NotNull @Unmodifiable List<String> suggest(@NotNull XClaim runtime, @NotNull PlatformUser user) {
        List<String> names = new ArrayList<>();

        for (Claim c : runtime.claims().getAll()) {
            if (!this.condition.test(c, user)) continue;
            names.add(c.name());
        }

        return Collections.unmodifiableList(names);
    }

}
