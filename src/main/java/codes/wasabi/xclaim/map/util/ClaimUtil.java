package codes.wasabi.xclaim.map.util;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.util.ColorUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClaimUtil {

    private static final Map<UUID, Color> colorMap = new HashMap<>();
    public static @NotNull Color getClaimColor(@NotNull Claim claim) {
        XCPlayer ply = claim.getOwner();
        UUID uuid = ply.getUniqueId();
        Color color = colorMap.get(uuid);
        if (color == null) {
            color = ColorUtil.uuidToColor(uuid);
            colorMap.put(uuid, color);
        }
        return color;
    }

}
