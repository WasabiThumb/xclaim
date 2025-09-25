package io.github.wasabithumb.xclaim.claim.flags;

import static io.github.wasabithumb.xclaim.claim.flags.ClaimFlagImpl.create;

import io.github.wasabithumb.xclaim.i18n.Translatable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public sealed interface ClaimFlag permits ClaimFlagImpl {

    ClaimFlag NO_PVP     = create("no-pvp", 'p');
    ClaimFlag FIREPROOF  = create("fireproof", 'f');
    ClaimFlag WATERPROOF = create("waterproof", 'w');

    @Contract("-> new")
    static @NotNull ClaimFlag @NotNull [] values() {
        return ClaimFlagImpl.values();
    }

    //

    @ApiStatus.Internal
    char magic();

    @ApiStatus.Internal
    int value();

    @NotNull String name();

    default @NotNull Translatable title() {
        return Translatable.keyed("flag-" + this.name());
    }

}
