package codes.wasabi.xclaim.map;

import codes.wasabi.xclaim.api.Claim;
import org.jetbrains.annotations.NotNull;

public interface MapServiceOp {

    static @NotNull MapServiceOp update(@NotNull Claim claim) {
        return new MapServiceOp.Update(claim);
    }

    static @NotNull MapServiceOp delete(@NotNull Claim claim) {
        return new MapServiceOp.Delete(claim);
    }

    //

    @NotNull Claim getClaim();

    void apply(@NotNull MapMarker marker);

    //

    final class Update implements MapServiceOp {

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

    final class Delete implements MapServiceOp {

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
