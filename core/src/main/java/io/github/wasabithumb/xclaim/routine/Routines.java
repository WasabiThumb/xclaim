package io.github.wasabithumb.xclaim.routine;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.routine.impl.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class Routines {

    private final MoveRoutine move;
    private final GraceRoutine grace;

    @ApiStatus.Internal
    public Routines(@NotNull XClaim runtime) {
        this.move = new MoveRoutine(runtime);
        this.grace = new GraceRoutine(runtime);
    }

    public @NotNull Routine @NotNull [] all() {
        return new Routine[] {
                this.move,
                this.grace
        };
    }

    @ApiStatus.Internal
    public void start() {
        for (Routine r : this.all())
            r.start();
    }

    @ApiStatus.Internal
    public void stop() {
        for (Routine r : this.all())
            r.stop();
    }

    //

    public @NotNull MoveRoutine move() {
        return this.move;
    }

    public @NotNull GraceRoutine grace() {
        return this.grace;
    }

}
