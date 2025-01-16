package io.github.wasabithumb.xclaim.platform.scheduler.target;

import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract sealed class PlatformSchedulerTarget {

    public static @NotNull PlatformSchedulerTarget async() {
        return Basic.ASYNC;
    }

    public static @NotNull PlatformSchedulerTarget server() {
        return Basic.SERVER;
    }

    @Contract("_ -> new")
    public static @NotNull PlatformSchedulerTarget entity(@NotNull PlatformEntity entity) {
        return new Entity(entity);
    }

    @Contract("_ -> new")
    public static @NotNull PlatformSchedulerTarget chunk(@NotNull PlatformChunk chunk) {
        return new Chunk(chunk);
    }

    //

    public abstract @NotNull PlatformSchedulerTargetType type();

    public @NotNull PlatformEntity entity() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Target of type " + this.type() + " has no associated entity");
    }

    public @NotNull PlatformChunk chunk() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Target of type " + this.type() + " has no associated chunk");
    }

    //

    private static final class Basic extends PlatformSchedulerTarget {

        private static final PlatformSchedulerTarget ASYNC = new Basic(PlatformSchedulerTargetType.ASYNC);
        private static final PlatformSchedulerTarget SERVER = new Basic(PlatformSchedulerTargetType.SERVER);

        private final PlatformSchedulerTargetType type;
        Basic(@NotNull PlatformSchedulerTargetType type) {
            this.type = type;
        }

        @Override
        public @NotNull PlatformSchedulerTargetType type() {
            return this.type;
        }

    }

    private static final class Entity extends PlatformSchedulerTarget {

        private final PlatformEntity value;
        Entity(@NotNull PlatformEntity value) {
            this.value = value;
        }

        @Override
        public @NotNull PlatformSchedulerTargetType type() {
            return PlatformSchedulerTargetType.ENTITY;
        }

        @Override
        public @NotNull PlatformEntity entity() {
            return this.value;
        }

    }

    private static final class Chunk extends PlatformSchedulerTarget {

        private final PlatformChunk value;
        Chunk(@NotNull PlatformChunk value) {
            this.value = value;
        }

        @Override
        public @NotNull PlatformSchedulerTargetType type() {
            return PlatformSchedulerTargetType.CHUNK;
        }

        @Override
        public @NotNull PlatformChunk chunk() throws UnsupportedOperationException {
            return this.value;
        }

    }

}
