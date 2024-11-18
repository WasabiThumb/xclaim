package io.github.wasabithumb.xclaim.platform.world;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class PlatformLocation {

    private PlatformWorld world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public PlatformLocation(@UnknownNullability PlatformWorld world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public PlatformLocation(@UnknownNullability PlatformWorld world, double x, double y, double z) {
        this(world, x, y, z, 0f, 0f);
    }

    //

    public @UnknownNullability PlatformWorld world() {
        return this.world;
    }

    @Contract("_ -> this")
    public PlatformLocation world(@UnknownNullability PlatformWorld world) {
        this.world = world;
        return this;
    }

    public double x() {
        return this.x;
    }

    public int blockX() {
        return (int) Math.floor(this.x);
    }

    @Contract("_ -> this")
    public PlatformLocation x(double x) {
        this.x = x;
        return this;
    }

    public double y() {
        return this.y;
    }

    public int blockY() {
        return (int) Math.floor(this.y);
    }

    @Contract("_ -> this")
    public PlatformLocation y(double y) {
        this.y = y;
        return this;
    }

    public double z() {
        return this.z;
    }

    public int blockZ() {
        return (int) Math.floor(this.z);
    }

    @Contract("_ -> this")
    public PlatformLocation z(double z) {
        this.z = z;
        return this;
    }

    public float yaw() {
        return this.yaw;
    }

    @Contract("_ -> this")
    public PlatformLocation yaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public float pitch() {
        return this.pitch;
    }

    @Contract("_ -> this")
    public PlatformLocation pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    //

    @Contract(" -> new")
    public @NotNull PlatformLocation copy() {
        return new PlatformLocation(
                this.world,
                this.x, this.y, this.z,
                this.yaw, this.pitch
        );
    }

}
