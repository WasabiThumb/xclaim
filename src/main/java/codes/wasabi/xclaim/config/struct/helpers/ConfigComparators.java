package codes.wasabi.xclaim.config.struct.helpers;

import it.unimi.dsi.fastutil.ints.IntComparator;

public final class ConfigComparators {

    /**
     * A comparator that reports the natural comparison, treating values less than 0 as infinity.
     */
    public static final IntComparator INT_NATURAL_OR_INF = (a, b) -> {
        int flag = 0;
        if (a < 0) flag |= 2;
        if (b < 0) flag |= 1;
        switch (flag) {
            case 0:
                return a - b;
            case 1:
                return -1;
            case 2:
                return 1;
            case 3:
                return 0;
        }
        throw new AssertionError();
    };

}
