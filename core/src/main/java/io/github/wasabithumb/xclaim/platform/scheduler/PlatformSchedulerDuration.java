package io.github.wasabithumb.xclaim.platform.scheduler;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract sealed class PlatformSchedulerDuration {

    public static @NotNull PlatformSchedulerDuration instant() {
        return Instant.INSTANCE;
    }

    @Contract("_ -> new")
    public static @NotNull PlatformSchedulerDuration ticks(long ticks) {
        if (ticks < 1L) return instant();
        return new Ticks(ticks);
    }

    @Contract("_ -> new")
    public static @NotNull PlatformSchedulerDuration millis(long millis) {
        if (millis < 1L) return instant();
        return new Millis(millis);
    }

    @Contract("_ -> new")
    public static @NotNull PlatformSchedulerDuration seconds(double seconds) {
        return millis(Math.round(seconds * 1000d));
    }

    //

    public abstract boolean isInstant();

    public abstract long ticks();

    public abstract long millis();

    protected abstract boolean equals(@NotNull PlatformSchedulerDuration other);

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof PlatformSchedulerDuration other) {
            return this.equals(other);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.millis());
    }

    //

    private static final class Instant extends PlatformSchedulerDuration {

        private static final Instant INSTANCE = new Instant();

        @Override
        public boolean isInstant() {
            return true;
        }

        @Override
        public long ticks() {
            return 0;
        }

        @Override
        public long millis() {
            return 0;
        }

        @Override
        protected boolean equals(@NotNull PlatformSchedulerDuration other) {
            return other.isInstant();
        }

    }

    private static final class Ticks extends PlatformSchedulerDuration {

        private final long value;
        Ticks(long value) {
            this.value = value;
        }

        @Override
        public boolean isInstant() {
            return false;
        }

        @Override
        public long ticks() {
            return this.value;
        }

        @Override
        public long millis() {
            return Math.floorDiv(this.value * 1000L, 20L);
        }

        @Override
        protected boolean equals(@NotNull PlatformSchedulerDuration other) {
            return this.value == other.ticks();
        }

    }

    private static final class Millis extends PlatformSchedulerDuration {

        private final long value;
        Millis(long value) {
            this.value = value;
        }

        @Override
        public boolean isInstant() {
            return false;
        }

        @Override
        public long ticks() {
            return Math.ceilDiv(this.value * 20L, 1000L);
        }

        @Override
        public long millis() {
            return this.value;
        }

        @Override
        protected boolean equals(@NotNull PlatformSchedulerDuration other) {
            return this.value == other.millis();
        }

    }

}
