package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NewClaimPage extends Page {

    private static int head = 1;
    private static int nextIndex() {
        AtomicInteger index = new AtomicInteger(head);
        Claim.getAll().stream().sorted(Comparator.comparing(Claim::getName)).forEachOrdered((Claim c) -> {
            String root = XClaim.lang.get("new-claim").toLowerCase(Locale.ROOT) + " #";
            String n = c.getName().toLowerCase(Locale.ROOT);
            if (n.startsWith(root)) {
                String remainder = n.substring(root.length());
                int num;
                try {
                    num = Integer.parseInt(remainder);
                } catch (NumberFormatException e) {
                    return;
                }
                int v = index.get();
                if (num == v) index.set(v + 1);
            }
        });
        final int idx = index.get();
        head = idx + 1;
        return idx;
    }

    private static final ItemStack YES_STACK = DisplayItem.create(
            Material.GREEN_CONCRETE,
            XClaim.lang.getComponent("gui-new-confirm"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-new-confirm-line1"),
                    XClaim.lang.getComponent("gui-new-confirm-line2"),
                    XClaim.lang.getComponent("gui-new-confirm-line3")
            )
    );

    private static final ItemStack NO_STACK = DisplayItem.create(
            Material.RED_CONCRETE,
            XClaim.lang.getComponent("gui-new-cancel"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-new-cancel-line1"),
                    XClaim.lang.getComponent("gui-new-cancel-line2")
            )
    );

    public NewClaimPage(@NotNull GUIHandler parent) {
        super(parent);
    }

    @Override
    public void onEnter() {
        clear();
        setItem(11, YES_STACK);
        setItem(15, NO_STACK);
    }

    @Override
    public void onClick(int slot) {
        if (slot == 11) {
            // do stuff
            Player ply = getTarget();
            Chunk chunk = ply.getLocation().getChunk();
            Claim cur = Claim.getByChunk(chunk);
            if (cur != null) {
                if (!cur.getOwner().getUniqueId().equals(ply.getUniqueId())) {
                    if (!ply.hasPermission("xclaim.override")) {
                        Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("gui-new-claimed"));
                        getParent().close();
                        return;
                    }
                }
            }
            UUID uuid = ply.getUniqueId();
            XCPlayer xcp = XCPlayer.of(ply);
            int maxChunks = xcp.getMaxChunks();
            int maxClaims = xcp.getMaxClaims();
            int curClaims = 0;
            int curChunks = 0;
            for (Claim c : Claim.getAll()) {
                if (c.getOwner().getUniqueId().equals(uuid)) {
                    curClaims++;
                    curChunks += c.getChunks().size();
                }
            }
            if (curClaims >= maxClaims) {
                Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("gui-new-max-claims"));
                getParent().close();
                return;
            }
            if (curChunks >= maxChunks) {
                Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("gui-new-max-chunks"));
                getParent().close();
                return;
            }
            String name = XClaim.lang.get("new-claim") + " #" + nextIndex();
            Claim newClaim = new Claim(name, Set.of(chunk), ply);
            newClaim.claim();
            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("gui-new-success", name));
            ply.playSound(ply.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            getParent().close();
            if (XClaim.mainConfig.getBoolean("enter-chunk-editor-on-create", true)) {
                ChunkEditor.startEditing(ply, newClaim);
            }
        } else if (slot == 15) {
            switchPage(new MainPage(getParent()));
        }
    }

}
