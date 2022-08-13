package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.gui.ChunkEditor;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.platform.Platform;
import codes.wasabi.xclaim.util.DisplayItem;
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
    private static final ItemStack NEW_STACK = DisplayItem.create(Material.NETHER_STAR, XClaim.lang.getComponent("gui-main-new"));

    private static final int EDIT_TRUST_POS = 12;
    private static final ItemStack EDIT_TRUST_STACK = DisplayItem.create(Platform.get().getSkeletonSkullMaterial(), XClaim.lang.getComponent("gui-main-edit-trust"));

    private static final int EDIT_CHUNK_POS = 13;
    private static final ItemStack EDIT_CHUNK_STACK = DisplayItem.create(Platform.get().getCraftingTableMaterial(), XClaim.lang.getComponent("gui-main-edit-chunk"));

    private static final int RENAME_CHUNK_POS = 14;
    private static final ItemStack RENAME_CHUNK_STACK = DisplayItem.create(Material.NAME_TAG, XClaim.lang.getComponent("gui-main-rename-chunk"));

    private static final int EDIT_PERM_POS = 15;
    private static final ItemStack EDIT_PERM_STACK = DisplayItem.create(Material.SHIELD, XClaim.lang.getComponent("gui-main-edit-perm"));

    private static final int TRANSFER_OWNER_POS = 20;
    private static final ItemStack TRANSFER_OWNER_STACK = DisplayItem.create(Platform.get().getChestMinecartMaterial(), XClaim.lang.getComponent("gui-main-transfer-owner"));

    private static final int CLEAR_ALL_POS = 21;
    private static final ItemStack CLEAR_ALL_STACK = DisplayItem.create(Material.TNT, XClaim.lang.getComponent("gui-main-clear-all"));

    private static final int DELETE_POS = 22;
    private static final ItemStack DELETE_STACK = DisplayItem.create(Material.BARRIER, XClaim.lang.getComponent("gui-main-delete"));

    private static final int VERSION_POS = 23;
    private static final ItemStack VERSION_STACK = DisplayItem.create(Platform.get().getEnchantingTableMaterial(), XClaim.lang.getComponent("gui-main-version"));

    private static final int EXIT_POS = 24;
    private static final ItemStack EXIT_STACK = DisplayItem.create(Material.ARROW, XClaim.lang.getComponent("gui-main-exit"));

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
            case NEW_POS:
                switchPage(new NewClaimPage(getParent()));
                break;
            case EDIT_TRUST_POS:
                switchPage(new PlayerCombinatorPage(getParent()) {
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
                break;
            case EDIT_CHUNK_POS:
                switchPage(new ClaimSelectorPage(getParent(), claim -> {
                    Player ply = getTarget();
                    World w = ply.getWorld();
                    World cw = claim.getWorld();
                    if (cw != null) {
                        if (w != cw) {
                            Platform.getAdventure().player(ply).sendMessage(XClaim.lang.getComponent("gui-edit-chunk-fail"));
                            getParent().close();
                            return;
                        }
                    }
                    ChunkEditor.startEditing(ply, claim);
                    getParent().close();
                }));
                break;
            case RENAME_CHUNK_POS:
                switchPage(new ClaimSelectorPage(getParent(), claim -> {
                    switchPage(MainPage.this);
                    prompt(XClaim.lang.get("gui-rename-chunk-prompt"), (String name) -> {
                        if (name.length() > 50) {
                            Platform.getAdventure().player(getTarget()).sendMessage(XClaim.lang.getComponent("gui-rename-chunk-fail"));
                        } else {
                            claim.setName(name);
                        }
                        switchPage(new MainPage(getParent()));
                    });
                }));
                break;
            case EDIT_PERM_POS:
                switchPage(new ClaimSelectorPage(getParent(), claim -> switchPage(new PermissionPage(getParent(), claim))));
                break;
            case TRANSFER_OWNER_POS:
                switchPage(new ClaimSelectorPage(getParent(), claim -> switchPage(new TransferPage(getParent(), claim))));
                break;
            case CLEAR_ALL_POS:
                switchPage(new ClearAllPage(getParent()));
                break;
            case DELETE_POS:
                switchPage(new ClaimSelectorPage(getParent(), Claim::unclaim) {
                    @Override
                    protected boolean showClaim(@NotNull Claim claim, @NotNull OfflinePlayer ply) {
                        return claim.hasPermission(ply, Permission.DELETE);
                    }
                });
                break;
            case VERSION_POS:
                switchPage(new VersionInfoPage(getParent()));
                break;
            case EXIT_POS:
                getParent().close();
                break;
        }
    }

}
