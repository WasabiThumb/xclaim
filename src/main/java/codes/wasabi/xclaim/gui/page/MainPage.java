package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.platform.Platform;
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
import java.util.Map;

public class MainPage extends Page {

    private static final int NEW_POS = 11;
    private static final ItemStack NEW_STACK = DisplayItem.create(Material.NETHER_STAR, "New Claim", NamedTextColor.GOLD);

    private static final int EDIT_TRUST_POS = 12;
    private static final ItemStack EDIT_TRUST_STACK = DisplayItem.create(Material.SKELETON_SKULL, "Edit Trusted Players", NamedTextColor.BLUE);

    private static final int EDIT_CHUNK_POS = 13;
    private static final ItemStack EDIT_CHUNK_STACK = DisplayItem.create(Material.CRAFTING_TABLE, "Edit Claim Chunks", NamedTextColor.GREEN);

    private static final int RENAME_CHUNK_POS = 14;
    private static final ItemStack RENAME_CHUNK_STACK = DisplayItem.create(Material.NAME_TAG, "Rename Claim", NamedTextColor.DARK_PURPLE);

    private static final int EDIT_PERM_POS = 15;
    private static final ItemStack EDIT_PERM_STACK = DisplayItem.create(Material.SHIELD, "Edit Claim Permissions", NamedTextColor.DARK_AQUA);

    private static final int TRANSFER_OWNER_POS = 20;
    private static final ItemStack TRANSFER_OWNER_STACK = DisplayItem.create(Material.CHEST_MINECART, "Transfer Owner", NamedTextColor.GRAY);

    private static final int CLEAR_ALL_POS = 21;
    private static final ItemStack CLEAR_ALL_STACK = DisplayItem.create(Material.TNT, "Clear All Claims", NamedTextColor.DARK_RED);

    private static final int DELETE_POS = 22;
    private static final ItemStack DELETE_STACK = DisplayItem.create(Material.BARRIER, "Delete Claim", NamedTextColor.RED);

    private static final int VERSION_POS = 23;
    private static final ItemStack VERSION_STACK = DisplayItem.create(Material.ENCHANTING_TABLE, "Version Info", NamedTextColor.LIGHT_PURPLE);

    private static final int EXIT_POS = 24;
    private static final ItemStack EXIT_STACK = DisplayItem.create(Material.ARROW, "Exit", NamedTextColor.GRAY);

    private static final Map<Integer, ItemStack> assoc = Map.of(
            NEW_POS, NEW_STACK,
            EDIT_TRUST_POS, EDIT_TRUST_STACK,
            EDIT_CHUNK_POS, EDIT_CHUNK_STACK,
            RENAME_CHUNK_POS, RENAME_CHUNK_STACK,
            EDIT_PERM_POS, EDIT_PERM_STACK,
            TRANSFER_OWNER_POS, TRANSFER_OWNER_STACK,
            CLEAR_ALL_POS, CLEAR_ALL_STACK,
            DELETE_POS, DELETE_STACK,
            VERSION_POS, VERSION_STACK,
            EXIT_POS, EXIT_STACK
    );

    public MainPage(@NotNull GUIHandler parent) {
        super(parent);
    }

    @Override
    public void onEnter() {
        clear();
        for (Map.Entry<Integer, ItemStack> entry : assoc.entrySet()) {
            setItem(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void onClick(int slot) {
        switch (slot) {
            case NEW_POS -> switchPage(new NewClaimPage(getParent()));
            case EDIT_TRUST_POS -> switchPage(new PlayerCombinatorPage(getParent()) {
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
            case EDIT_CHUNK_POS -> switchPage(new ClaimSelectorPage(getParent(), claim -> {
                Player ply = getTarget();
                World w = ply.getWorld();
                World cw = claim.getWorld();
                if (cw != null) {
                    if (w != cw) {
                        Platform.getAdventure().player(ply).sendMessage(Component.text("* You can't have claims across worlds!").color(NamedTextColor.RED));
                        getParent().close();
                        return;
                    }
                }
                ChunkEditor.startEditing(ply, claim);
                getParent().close();
            }));
            case RENAME_CHUNK_POS -> switchPage(new ClaimSelectorPage(getParent(), claim -> {
                switchPage(MainPage.this);
                prompt("Enter a new name for the claim: ", (String name) -> {
                    if (name.length() > 50) {
                        Platform.getAdventure().player(getTarget()).sendMessage(Component.text("* Name too long! Has to be less than 50 characters").color(NamedTextColor.RED));
                    } else {
                        claim.setName(name);
                    }
                    switchPage(new MainPage(getParent()));
                });
            }));
            case EDIT_PERM_POS -> switchPage(new ClaimSelectorPage(getParent(), claim -> switchPage(new PermissionPage(getParent(), claim))));
            case TRANSFER_OWNER_POS -> switchPage(new ClaimSelectorPage(getParent(), claim -> switchPage(new TransferPage(getParent(), claim))));
            case CLEAR_ALL_POS -> switchPage(new ClearAllPage(getParent()));
            case DELETE_POS -> switchPage(new ClaimSelectorPage(getParent(), Claim::unclaim) {
                @Override
                protected boolean showClaim(@NotNull Claim claim, @NotNull OfflinePlayer ply) {
                    return claim.hasPermission(ply, Permission.DELETE);
                }
            });
            case VERSION_POS -> switchPage(new VersionInfoPage(getParent()));
            case EXIT_POS -> getParent().close();
        }
    }

}
