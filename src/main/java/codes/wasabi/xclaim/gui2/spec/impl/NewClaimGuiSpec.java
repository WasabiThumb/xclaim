package codes.wasabi.xclaim.gui2.spec.impl;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.api.event.XClaimCreateClaimEvent;
import codes.wasabi.xclaim.api.event.XClaimEvent;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.gui2.GuiInstance;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import codes.wasabi.xclaim.gui2.spec.GuiSpec;
import codes.wasabi.xclaim.gui2.spec.GuiSpecs;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.protection.ProtectionRegion;
import codes.wasabi.xclaim.protection.ProtectionService;
import codes.wasabi.xclaim.util.DisplayItem;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class NewClaimGuiSpec implements GuiSpec {

    private static final ItemStack YES_STACK = DisplayItem.create(
            Platform.get().getGreenConcreteMaterial(),
            XClaim.lang.getComponent("gui-new-confirm"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-new-confirm-line1"),
                    XClaim.lang.getComponent("gui-new-confirm-line2"),
                    XClaim.lang.getComponent("gui-new-confirm-line3")
            )
    );

    private static final ItemStack NO_STACK = DisplayItem.create(
            Platform.get().getRedConcreteMaterial(),
            XClaim.lang.getComponent("gui-new-cancel"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-new-cancel-line1"),
                    XClaim.lang.getComponent("gui-new-cancel-line2")
            )
    );


    private int idCounter = 1;

    @Override
    public @NotNull String layout() {
        return "new-claim";
    }

    @Override
    public void populate(@NotNull GuiInstance instance) {
        instance.set(0, YES_STACK);
        instance.set(1, NO_STACK);
    }

    @Override
    public @NotNull GuiAction onClick(@NotNull GuiInstance instance, @NotNull GuiSlot slot, int index) {
        if (slot.index() == 0) {
            this.confirm(instance.player());
            return GuiAction.exit();
        } else if (slot.index() == 1) {
            return GuiAction.transfer(GuiSpecs.main());
        }
        return GuiAction.nothing();
    }

    //

    private void confirm(final @NotNull Player ply) {
        Chunk chunk = ply.getLocation().getChunk();
        if (!XClaim.mainConfig.worlds().checkLists(chunk.getWorld())) {
            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("gui-new-disallowed"));
            return;
        }
        if (ProtectionService.isAvailable()) {
            ProtectionService service = ProtectionService.getNonNull();
            Collection<ProtectionRegion> regions = service.getRegionsAt(chunk);
            boolean all = true;
            for (ProtectionRegion region : regions) {
                EnumSet<ProtectionRegion.Permission> set = region.getPermissions(ply);
                boolean access = Arrays.stream(ProtectionRegion.Permission.values()).allMatch(set::contains);
                if (!access) {
                    all = false;
                    break;
                }
            }
            if (!all) {
                Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-protection-deny"));
                return;
            }
        }
        Claim cur = Claim.getByChunk(chunk);
        if (cur != null) {
            if (!cur.getOwner().getUniqueId().equals(ply.getUniqueId())) {
                if (!ply.hasPermission("xclaim.override")) {
                    Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("gui-new-claimed"));
                    return;
                }
            }
        }
        UUID uuid = ply.getUniqueId();
        XCPlayer xcp = XCPlayer.of(ply);
        int maxChunks = xcp.getMaxChunks();
        int maxClaims = xcp.getMaxClaims();
        int maxWorldClaims = xcp.getMaxClaimsInWorld();
        int curClaims = 0;
        int curChunks = 0;
        int curInWorld = 0;
        String curWorldName = ply.getWorld().getName();
        for (Claim c : Claim.getAll()) {
            if (c.getOwner().getUniqueId().equals(uuid)) {
                curClaims++;
                curChunks += c.getChunks().size();
                World w = c.getWorld();
                if (w != null) {
                    if (w.getName().equals(curWorldName)) curInWorld++;
                }
            }
        }
        if (curClaims >= maxClaims || curInWorld >= maxWorldClaims) {
            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("gui-new-max-claims"));
            return;
        }
        if (curChunks >= maxChunks) {
            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("gui-new-max-chunks"));
            return;
        }
        if (ChunkEditor.violatesDistanceCheck(ply, chunk)) {
            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("chunk-editor-min-distance-deny"));
            return;
        }
        final String name = this.nextClaimName();
        Claim newClaim = new Claim(name, Collections.singleton(chunk), ply);
        if (!XClaimEvent.dispatch(new XClaimCreateClaimEvent(ply, newClaim))) return;
        newClaim.claim();
        Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("gui-new-success", name));
        ply.playSound(ply.getLocation(), Platform.get().getLevelSound(), 1f, 1f);
        if (XClaim.mainConfig.editor().startOnCreate()) {
            ChunkEditor.startEditing(ply, newClaim);
        }
    }

    private @NotNull String nextClaimName() {
        final String root = XClaim.lang.get("new-claim") + " #";
        final int rootLen = root.length();
        final StringBuilder ret = new StringBuilder(root);

        synchronized (this) {
            do {
                ret.setLength(rootLen);
                ret.append(this.idCounter++);
            } while (Claim.exists(ret));
        }

        return ret.toString();
    }

}
