package codes.wasabi.xclaim.util;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;
import java.util.UUID;

public final class ColorUtil {

    private static final int[][] combinations;
    static {
        // Precompute combinations
        int[][] comb = new int[336][];
        int i = 0;
        for (int a=0; a < 8; a++) {
            for (int b=0; b < 8; b++) {
                if (b == a) continue;
                for (int c=0; c < 8; c++) {
                    if (c == b || c == a) continue;
                    comb[i] = new int[]{ a, b, c };
                    i++;
                }
            }
        }
        combinations = comb;
    }

    public static @NotNull Color uuidToColor(@NotNull UUID uuid) {
        /*
            This is probably over-engineered.
            The goal is to reliably produce the same pseudo-random color from a UUID.
            The problem is generating it with *high* entropy, given that Minecraft
            UUIDs follow a pattern rather than being random and so colors
            will be pretty similar otherwise.

            To solve this, we take the 128 bit UUID and split it into 8 x 16 bit shorts.
            We use a pseudo-random generator with a seed defined by the lower 64 bits of the UUID
            in order to pick 3 out of those 8 shorts.

            Those 3 x 16 bit shorts are combined to create a 48 bit value. An extra copy of
            the 3rd short is added on so that the final value conforms to the 64 bit "long" spec.
            The reason why 3 shorts are picked instead of 4 is because, according to the UUID spec,
            only 48 bits of the 64 in the seed are used. This is done in the order 3123 so that
            regardless of whether the 48 highest order or 48 lowest order bits are used, all 3
            short values influence the seed (312 and 123).

            This final long value is used to seed a pseudo-random generator, which generates
            the hue of the color.
        */
        long mostSig = uuid.getMostSignificantBits();
        long leastSig = uuid.getLeastSignificantBits();
        short[] shorts = new short[]{
                (short) ((mostSig >> 48) & 65535),
                (short) ((mostSig >> 32) & 65535),
                (short) ((mostSig >> 16) & 65535),
                (short) (mostSig & 65535),
                (short) ((leastSig >> 48) & 65535),
                (short) ((leastSig >> 32) & 65535),
                (short) ((leastSig >> 16) & 65535),
                (short) (leastSig & 65535)
        };
        Random proto = new Random(leastSig);
        int[] comb = combinations[proto.nextInt(336)];
        short a = shorts[comb[0]];
        short b = shorts[comb[1]];
        short c = shorts[comb[2]];
        long seed = c | (((long) b) << 16) | (((long) a) << 32) | (((long) c) << 48);
        proto = new Random(seed);
        float hue = proto.nextFloat();
        // deny greenish hues
        hue *= 0.77f;
        if (hue > 0.18f) hue += 0.23f;
        //
        return new Color(Color.HSBtoRGB(hue, 1f, 1f));
    }

}
