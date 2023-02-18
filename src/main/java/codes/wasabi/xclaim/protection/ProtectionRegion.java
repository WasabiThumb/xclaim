package codes.wasabi.xclaim.protection;

import org.bukkit.entity.Player;

import java.util.EnumSet;

public interface ProtectionRegion {

    EnumSet<Permission> getPermissions(Player player);

    enum Permission {
        BUILD,
        BREAK,
        USE,
        INTERACT,
        PASSTHROUGH,
        ENTER,
        EXIT
    }

}
