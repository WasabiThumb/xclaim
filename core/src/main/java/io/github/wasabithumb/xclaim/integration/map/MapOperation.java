package io.github.wasabithumb.xclaim.integration.map;

import io.github.wasabithumb.xclaim.claim.Claim;
import org.jetbrains.annotations.NotNull;

public sealed interface MapOperation {

    static @NotNull MapOperation update(@NotNull Claim claim) {
        return new MapOperation.Update(claim);
    }

    static @NotNull MapOperation delete(@NotNull Claim claim) {
        return new MapOperation.Delete(claim);
    }

    //

    @NotNull Claim getClaim();

    void apply(@NotNull MapMarker marker);

    //

    final class Update implements MapOperation {

        private final Claim claim;
        public Update(Claim claim) {
            this.claim = claim;
        }

        @Override
        public @NotNull Claim getClaim() {
            return this.claim;
        }

        @Override
        public void apply(@NotNull MapMarker marker) {
            marker.update(this.claim);
        }

    }

    //

    final class Delete implements MapOperation {

        private final Claim claim;
        public Delete(Claim claim) {
            this.claim = claim;
        }

        @Override
        public @NotNull Claim getClaim() {
            return this.claim;
        }

        @Override
        public void apply(@NotNull MapMarker marker) {
            marker.deleteMarker();
        }

    }

}
