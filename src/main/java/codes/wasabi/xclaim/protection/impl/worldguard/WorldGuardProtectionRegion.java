package codes.wasabi.xclaim.protection.impl.worldguard;

import codes.wasabi.xclaim.protection.ProtectionRegion;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

import java.util.*;

public class WorldGuardProtectionRegion implements ProtectionRegion {

    private static final Map<StateFlag, Permission> permissionMap = Collections.unmodifiableMap(new HashMap<StateFlag, Permission>() {{
        put(Flags.BUILD, Permission.BUILD);
        put(Flags.BLOCK_BREAK, Permission.BREAK);
        put(Flags.ENTRY, Permission.ENTER);
        put(Flags.EXIT, Permission.EXIT);
        put(Flags.USE, Permission.USE);
        put(Flags.INTERACT, Permission.INTERACT);
        put(Flags.PASSTHROUGH, Permission.PASSTHROUGH);
    }});

    private final ProtectedRegion region;
    private final WorldGuardPlugin plugin;

    public WorldGuardProtectionRegion(ProtectedRegion region, WorldGuardPlugin plugin) {
        this.region = region;
        this.plugin = plugin;
    }

    public ProtectedRegion getHandle() {
        return region;
    }

    @Override
    public EnumSet<Permission> getPermissions(Player player) {
        LocalPlayer lp = this.plugin.wrapPlayer(player);
        Set<Permission> ret = new HashSet<>();

        Association association = lp.getAssociation(Collections.singletonList(this.region));

        for (Map.Entry<StateFlag, Permission> entry : permissionMap.entrySet()) {
            if (this.checkPermission(entry.getKey(), association)) ret.add(entry.getValue());
        }

        return EnumSet.copyOf(ret);
    }

    private boolean checkPermission(StateFlag flag, Association association) {
        StateFlag.State state = this.region.getFlag(flag);
        if (state == null) return true;
        RegionGroup group = this.region.getFlag(flag.getRegionGroupFlag());
        if (group == null) return (state == StateFlag.State.ALLOW) || Objects.equals(association, Association.OWNER);
        return (state == StateFlag.State.ALLOW && group.contains(association)) || Objects.equals(association, Association.OWNER);
    }

}
