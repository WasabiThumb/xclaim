package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
            String n = c.getName().toLowerCase(Locale.ROOT);
            if (n.startsWith("new claim #")) {
                String remainder = n.substring(11);
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
            Component.text("Confirm").color(NamedTextColor.DARK_GREEN),
            Arrays.asList(
                    Component.text("Create a new claim").color(NamedTextColor.GREEN),
                    Component.text("starting in your current").color(NamedTextColor.GREEN),
                    Component.text("chunk.").color(NamedTextColor.GREEN)
            )
    );

    private static final ItemStack NO_STACK = DisplayItem.create(
            Material.RED_CONCRETE,
            Component.text("Cancel").color(NamedTextColor.DARK_RED),
            Arrays.asList(
                    Component.text("Return to the").color(NamedTextColor.RED),
                    Component.text("main menu.").color(NamedTextColor.RED)
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
                        ply.sendMessage(Component.text("* This chunk is already claimed!").color(NamedTextColor.RED));
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
                ply.sendMessage(Component.text("* You've reached your maximum number of claims! Try deleting some.").color(NamedTextColor.RED));
                getParent().close();
                return;
            }
            if (curChunks >= maxChunks) {
                ply.sendMessage(Component.text("* Can't create this claim, it will exceed your maximum number of chunks.").color(NamedTextColor.RED));
                getParent().close();
                return;
            }
            String name = "New Claim #" + nextIndex();
            Claim newClaim = new Claim(name, Set.of(chunk), ply);
            newClaim.claim();
            ply.sendMessage(
                    Component
                            .empty()
                            .append(
                                    Component.text("* Created new claim ").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
                            ).append(
                                    Component.text(name).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD).decorate(TextDecoration.ITALIC)
                            )
            );
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
