package codes.wasabi.xclaim.util.metric;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public final class MetricFormatter {

    public static @NotNull MetricFormatter instance() {
        return new MetricFormatter(MetricSet.all());
    }

    private static final DecimalFormat[] DECIMAL_FORMATS = new DecimalFormat[] {
            new DecimalFormat("0.##"),
            new DecimalFormat("0.#")
    };

    private final MetricSet metrics;
    public MetricFormatter(@NotNull MetricSet metrics) {
        this.metrics = metrics;
    }

    @Contract("_, null, true -> fail; _, !null, _ -> _; _, null, false -> _")
    private @NotNull String format00(double value, CharSequence postfix, boolean hasPostfix) {
        int magnitude = (int) Math.log10(value);

        final IMetric metric = this.metrics.getByMagnitude(magnitude);
        int overshot = magnitude - metric.magnitude();

        value /= Math.pow(10d, metric.magnitude());

        String ret;
        boolean spaced = false;
        if (overshot >= 0 && overshot < 2) {
            ret = DECIMAL_FORMATS[overshot].format(value);
        } else {
            ret = Long.toString(Math.round(value));
        }

        if (metric.hasPrefix()) {
            spaced = true;
            ret += " " + metric.prefix();
        }

        if (hasPostfix) {
            if (!spaced) ret += " ";
            ret += postfix;
        }

        return ret;
    }

    @Contract("_, null, true -> fail; _, !null, _ -> _; _, null, false -> _")
    private @NotNull String format0(double value, CharSequence postfix, boolean hasPostfix) {
        return (value < 0L) ?
                ("-" + format00(Math.abs(value), postfix, hasPostfix)) :
                format00(value, postfix, hasPostfix);
    }

    public @NotNull String format(double value, @Nullable CharSequence postfix) {
        return this.format0(value, postfix, postfix != null);
    }

    public @NotNull String format(double value) {
        return this.format0(value, null, false);
    }

}
