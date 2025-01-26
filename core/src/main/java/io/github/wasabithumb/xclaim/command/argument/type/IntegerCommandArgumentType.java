package io.github.wasabithumb.xclaim.command.argument.type;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.i18n.Translatable;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import io.github.wasabithumb.xclaim.util.BitManipulation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApiStatus.Internal
final class IntegerCommandArgumentType implements CommandArgumentType<Integer> {

    private static final long NO_BOUNDS = -9223372034707292161L;

    //

    private final long bounds;

    IntegerCommandArgumentType(int min, int max) throws IllegalArgumentException {
        if (min > max) throw new IllegalArgumentException("Minimum value " + min +" is greater than maximum value " + max);
        this.bounds = BitManipulation.i32i64(min, max);
    }

    IntegerCommandArgumentType() {
        this.bounds = NO_BOUNDS;
    }

    //

    @Override
    public @NotNull Translatable name() {
        return I18N.ARG_INT_NAME;
    }

    @Override
    public @NotNull Class<Integer> typeClass() {
        return Integer.class;
    }

    @Override
    public @NotNull ParseResult<Integer> parse(@NotNull XClaim runtime, @NotNull PlatformUser user, @NotNull String input) {
        int value;
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return ParseResult.error("Not a number");
        }
        if (this.bounds != NO_BOUNDS) {
            int[] bounds = BitManipulation.i64i32(this.bounds);
            if (value < bounds[0]) return ParseResult.error("Must be at least " + bounds[0]);
            if (value > bounds[1]) return ParseResult.error("Must be at most " + bounds[1]);
        }
        return ParseResult.success(value);
    }

    @Override
    public @NotNull @Unmodifiable List<String> suggest(@NotNull XClaim runtime, @NotNull PlatformUser user) {
        if (this.bounds == NO_BOUNDS) return Collections.emptyList();

        int[] bounds = BitManipulation.i64i32(this.bounds);
        int range = bounds[1] - bounds[0] + 1;
        if (range > 1024) {
            // Failsafe to prevent huge suggestion
            return Collections.emptyList();
        }

        List<String> ret = new ArrayList<>(range);
        for (int i=bounds[0]; i < bounds[1]; i++) {
            ret.add(Integer.toString(i));
        }
        return Collections.unmodifiableList(ret);
    }

}
