package io.github.wasabithumb.xclaim.util;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.Random;
import java.util.UUID;

public final class ColorUtil {

    public static @NotNull Color uuidToColor(@NotNull UUID uuid) {
        long mostSig = uuid.getMostSignificantBits();
        long leastSig = uuid.getLeastSignificantBits();
        long[] parts = new long[] {
                ((mostSig >> 48) & 0xFFFF),
                ((mostSig >> 32) & 0xFFFF),
                ((mostSig >> 16) & 0xFFFF),
                (mostSig & 0xFFFF),
                ((leastSig >> 48) & 0xFFFF),
                ((leastSig >> 32) & 0xFFFF),
                ((leastSig >> 16) & 0xFFFF),
                (leastSig & 0xFFFF)
        };

        Random rand = new Random(leastSig);
        for (int u=0; u < 3; u++) {
            int v = u + rand.nextInt(8 - u);
            long tmp = parts[u];
            parts[u] = parts[v];
            parts[v] = tmp;
        }
        rand = new Random(parts[2] | (parts[1] << 16) | (parts[0] << 32) | (parts[2] << 48));

        int hue = rand.nextInt(260);
        if (hue > 65) hue += 100;
        return new Color(Color.HSBtoRGB(hue / 360f, 1f, 1f));
    }

}
