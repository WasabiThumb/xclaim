package codes.wasabi.xclaim.util.metric;

import org.jetbrains.annotations.NotNull;

import java.nio.CharBuffer;

public enum Metric implements IMetric {

    NANO(-9, 'n'),
    MICRO(-6, 'μ'),
    MILLI(-3, 'm'),
    UNIT(0, ' '),
    KILO(3, 'k'),
    MEGA(6, 'M'),
    GIGA(9, 'G'),
    TERA(12, 'T');

    private final int magnitude;
    private final char prefix;
    Metric(int magnitude, char prefix) {
        this.magnitude = magnitude;
        this.prefix = prefix;
    }

    @Override
    public final int magnitude() {
        return this.magnitude;
    }

    @Override
    public @NotNull CharSequence prefix() {
        return CharBuffer.wrap(new char[] { this.prefix });
    }

    @Override
    public boolean hasPrefix() {
        return this.prefix != ' ';
    }

    public final char prefixChar() {
        return this.prefix;
    }

}
