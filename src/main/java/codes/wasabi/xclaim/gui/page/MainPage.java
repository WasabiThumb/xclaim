package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.util.DisplayItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainPage extends Page {

    private static final ItemStack NEW_STACK = DisplayItem.create(Material.NETHER_STAR, "New Claim", NamedTextColor.GOLD);
    private static final ItemStack EDIT_TRUST_STACK = DisplayItem.create(Material.SKELETON_SKULL, "Edit Trusted Players", NamedTextColor.BLUE);
    private static final ItemStack EDIT_CHUNK_STACK = DisplayItem.create(Material.CRAFTING_TABLE, "Edit Claim Chunks", NamedTextColor.GREEN);
    private static final ItemStack RENAME_CHUNK_STACK = DisplayItem.create(Material.NAME_TAG, "Rename Claim", NamedTextColor.DARK_PURPLE);
    private static final ItemStack EDIT_PERM_STACK = DisplayItem.create(Material.SHIELD, "Edit Claim Permissions", NamedTextColor.DARK_AQUA);
    private static final ItemStack CLEAR_ALL_STACK = DisplayItem.create(Material.TNT, "Clear All Claims", NamedTextColor.DARK_RED);
    private static final ItemStack DELETE_STACK = DisplayItem.create(Material.BARRIER, "Delete Claim", NamedTextColor.RED);
    private static final ItemStack VERSION_STACK = DisplayItem.create(Material.ENCHANTING_TABLE, "Version Info", NamedTextColor.LIGHT_PURPLE);
    private static final ItemStack EXIT_STACK = DisplayItem.create(Material.ARROW, "Exit", NamedTextColor.GRAY);

    public MainPage(@NotNull GUIHandler parent) {
        super(parent);
    }

    @Override
    public void onEnter() {
        clear();
        setItem(9, NEW_STACK);
        setItem(10, EDIT_TRUST_STACK);
        setItem(11, EDIT_CHUNK_STACK);
        setItem(12, RENAME_CHUNK_STACK);
        setItem(13, EDIT_PERM_STACK);
        setItem(14, CLEAR_ALL_STACK);
        setItem(15, DELETE_STACK);
        setItem(16, VERSION_STACK);
        setItem(17, EXIT_STACK);
    }

    @Override
    public void onClick(int slot) {
        switch (slot) {
            case 9 -> switchPage(new NewClaimPage(getParent()));
            case 10 -> switchPage(new PlayerCombinatorPage(getParent()) {
                private final XCPlayer me = XCPlayer.of(getTarget());

                @Override
                protected @NotNull List<OfflinePlayer> getList() {
                    return me.getTrustedPlayers();
                }

                @Override
                protected void add(@NotNull OfflinePlayer ply) {
                    me.trustPlayer(ply);
                }

                @Override
                protected void remove(@NotNull OfflinePlayer ply) {
                    me.untrustPlayer(ply);
                }

                @Override
                protected void onSelect(@NotNull OfflinePlayer ply) {
                    remove(ply);
                }
            });
            case 11 -> switchPage(new ClaimSelectorPage(getParent(), claim -> {
                Player ply = getTarget();
                World w = ply.getWorld();
                World cw = claim.getWorld();
                if (cw != null) {
                    if (w != cw) {
                        ply.sendMessage(Component.text("* You can't have claims across worlds!").color(NamedTextColor.RED));
                        getParent().close();
                        return;
                    }
                }
                ChunkEditor.startEditing(ply, claim);
                getParent().close();
            }));
            case 12 -> switchPage(new ClaimSelectorPage(getParent(), claim -> {
                switchPage(MainPage.this);
                prompt("Enter a new name for the claim: ", (String name) -> {
                    if (name.length() > 50) {
                        getTarget().sendMessage(Component.text("* Name too long! Has to be less than 50 characters").color(NamedTextColor.RED));
                    } else {
                        claim.setName(name);
                    }
                    switchPage(new MainPage(getParent()));
                });
            }));
            case 13 -> switchPage(new ClaimSelectorPage(getParent(), claim -> {
                switchPage(new PermissionPage(getParent(), claim));
            }));
            case 14 -> switchPage(new ClearAllPage(getParent()));
            case 15 -> switchPage(new ClaimSelectorPage(getParent(), Claim::unclaim) {
                @Override
                protected boolean showClaim(@NotNull Claim claim, @NotNull OfflinePlayer ply) {
                    return claim.hasPermission(ply, Permission.DELETE);
                }
            });
            case 16 -> switchPage(new VersionInfoPage(getParent()));
            case 17 -> getParent().close();
        };
    }

}
