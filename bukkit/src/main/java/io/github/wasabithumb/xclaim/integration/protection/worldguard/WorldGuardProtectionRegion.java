package io.github.wasabithumb.xclaim.integration.protection.worldguard;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.wasabithumb.xclaim.integration.protection.ProtectionPermission;
import io.github.wasabithumb.xclaim.integration.protection.ProtectionRegion;
import io.github.wasabithumb.xclaim.platform.PlatformTypeAdapter;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.user.PlatformOfflineUser;
import io.github.wasabithumb.xclaim.platform.user.PlatformUser;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WorldGuardProtectionRegion implements ProtectionRegion {

    private static final PermissionMapping[] PERMISSIONS = new PermissionMapping[] {
            new PermissionMapping(Flags.BUILD, ProtectionPermission.BUILD),
            new PermissionMapping(Flags.BLOCK_BREAK, ProtectionPermission.BREAK),
            new PermissionMapping(Flags.ENTRY, ProtectionPermission.ENTER),
            new PermissionMapping(Flags.EXIT, ProtectionPermission.EXIT),
            new PermissionMapping(Flags.USE, ProtectionPermission.USE),
            new PermissionMapping(Flags.INTERACT, ProtectionPermission.INTERACT),
            new PermissionMapping(Flags.PASSTHROUGH, ProtectionPermission.PASSTHROUGH)
    };

    private final ProtectedRegion handle;
    private final PlatformTypeAdapter adapter;
    private final WorldGuardPlugin plugin;

    WorldGuardProtectionRegion(
            @NotNull ProtectedRegion handle,
            @NotNull PlatformTypeAdapter adapter,
            @NotNull WorldGuardPlugin plugin
    ) {
        this.handle = handle;
        this.adapter = adapter;
        this.plugin = plugin;
    }

    @Override
    public @NotNull Set<ProtectionPermission> getPermissions(@NotNull PlatformUser user) {
        LocalPlayer lp;
        if (user.isPlayer()) {
            lp = this.plugin.wrapPlayer((Player) this.adapter.player(user.asPlayer()));
        } else if (user instanceof PlatformOfflineUser offline) {
            lp = this.plugin.wrapOfflinePlayer((OfflinePlayer) this.adapter.offlineUser(offline));
        } else {
            return Collections.unmodifiableSet(EnumSet.allOf(ProtectionPermission.class));
        }

        Set<ProtectionPermission> ret = EnumSet.noneOf(ProtectionPermission.class);
        Association assoc = lp.getAssociation(Collections.singletonList(this.handle));
        for (PermissionMapping mapping : PERMISSIONS) {
            if (this.checkPermission(mapping.handle, assoc))
                ret.add(mapping.tag);
        }
        return Collections.unmodifiableSet(ret);
    }

    private boolean checkPermission(@NotNull StateFlag flag, @NotNull Association association) {
        if (Association.OWNER.equals(association)) return true;

        StateFlag.State state = this.handle.getFlag(flag);
        if (state == null) return true;

        RegionGroup group = this.handle.getFlag(flag.getRegionGroupFlag());
        if (group != null && !group.contains(association)) {
            return false;
        }

        return state == StateFlag.State.ALLOW;
    }

    //

    private record PermissionMapping(
            @NotNull StateFlag handle,
            @NotNull ProtectionPermission tag
    ) { }

}
