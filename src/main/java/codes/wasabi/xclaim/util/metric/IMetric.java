package codes.wasabi.xclaim.util.metric;

import org.jetbrains.annotations.NotNull;

public interface IMetric {

    int magnitude();

    @NotNull CharSequence prefix();

    boolean hasPrefix();

}
