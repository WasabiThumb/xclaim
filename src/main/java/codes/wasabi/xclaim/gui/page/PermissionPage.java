package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.TrustLevel;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.util.DisplayItem;
import codes.wasabi.xclaim.util.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class PermissionPage extends Page {

    private static final ItemStack GLOBAL_STACK = DisplayItem.create(
            Material.CAULDRON,
            Component.text("General Permissions").color(NamedTextColor.DARK_GRAY),
            Arrays.asList(
                    Component.text("Manage permissions for").color(NamedTextColor.GRAY),
                    Component.text("general groups (trusted").color(NamedTextColor.GRAY),
                    Component.text("players, veterans, etc)").color(NamedTextColor.GRAY)
            )
    );

    private static final ItemStack PLAYER_STACK = DisplayItem.create(
            Material.SKELETON_SKULL,
            Component.text("Player Permissions").color(NamedTextColor.DARK_AQUA),
            Arrays.asList(
                    Component.text("Manage per-player").color(NamedTextColor.AQUA),
                    Component.text("permission exceptions").color(NamedTextColor.AQUA),
                    Component.text("(for instance, stop").color(NamedTextColor.AQUA),
                    Component.text("a specific player").color(NamedTextColor.AQUA),
                    Component.text("from entering)").color(NamedTextColor.AQUA)
            )
    );

    private static final ItemStack BACK_STACK = DisplayItem.create(Material.BARRIER, "Back", NamedTextColor.RED);

    private final Claim claim;
    private int subPage = 0;
    public PermissionPage(@NotNull GUIHandler parent, @NotNull Claim claim) {
        super(parent);
        this.claim = claim;
    }

    private final Map<Integer, Object> pickKeys = new HashMap<>();
    private Permission modifyingPermission;
    private OfflinePlayer managingPlayer;
    private void populate() {
        clear();
        switch (subPage) {
            case 0 -> {
                setItem(10, GLOBAL_STACK);
                setItem(13, PLAYER_STACK);
                setItem(16, BACK_STACK);
            }
            case 1 -> {
                pickKeys.clear();
                int i = 0;
                for (Permission p : Permission.values()) {
                    if (i > 17) break;
                    pickKeys.put(i, p);
                    TrustLevel tl = claim.getPermission(p);
                    Material mat;
                    TextColor col;
                    switch (tl) {
                        case NONE -> {
                            mat = Material.RED_DYE;
                            col = NamedTextColor.RED;
                        }
                        case TRUSTED -> {
                            mat = Material.ORANGE_DYE;
                            col = NamedTextColor.GOLD;
                        }
                        case VETERANS -> {
                            mat = Material.YELLOW_DYE;
                            col = NamedTextColor.YELLOW;
                        }
                        case ALL -> {
                            mat = Material.LIME_DYE;
                            col = NamedTextColor.GREEN;
                        }
                        default -> {
                            return;
                        }
                    }
                    List<Component> lore = new ArrayList<>();
                    for (String s : WordWrap.wrap(p.getDescription(), 25).split(System.lineSeparator())) {
                        lore.add(Component.text(s).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                    }
                    setItem(i, DisplayItem.create(mat, Component.text(p.getPrintName()).color(col), lore));
                    i++;
                }
                setItem(22, BACK_STACK);
            }
            case 2 -> {
                PlayerCombinatorPage combinator = new PlayerCombinatorPage(getParent()) {
                    @Override
                    protected @NotNull List<OfflinePlayer> getList() {
                        return claim.getUserPermissions().keySet().stream().map(XCPlayer::getOfflinePlayer).collect(Collectors.toList());
                    }

                    @Override
                    protected void add(@NotNull OfflinePlayer ply) {
                        claim.setUserPermission(ply, Permission.ENTER, true);
                    }

                    @Override
                    protected void remove(@NotNull OfflinePlayer ply) {
                        claim.clearUserPermissions(ply);
                    }

                    @Override
                    protected void onSelect(@NotNull OfflinePlayer ply) {
                        switchPage(PermissionPage.this);
                        PermissionPage.this.managingPlayer = ply;
                        PermissionPage.this.subPage = 4;
                        PermissionPage.this.populate();
                    }

                    @Override
                    protected void goBack() {
                        switchPage(PermissionPage.this);
                    }
                };
                switchPage(combinator);
            }
            case 3 -> {
                ItemStack noneItem = DisplayItem.create(
                        Material.RED_DYE,
                        Component.text("None").color(NamedTextColor.RED),
                        Arrays.asList(
                                Component.text("Nobody except you"),
                                Component.text("has this permission.")
                        )
                );
                ItemStack trustedItem = DisplayItem.create(
                        Material.ORANGE_DYE,
                        Component.text("Trusted").color(NamedTextColor.GOLD),
                        Arrays.asList(
                                Component.text("Only players you have"),
                                Component.text("specifically trusted"),
                                Component.text("have this permission,")
                        )
                );
                ItemStack vetItem = DisplayItem.create(
                        Material.YELLOW_DYE,
                        Component.text("Veterans").color(NamedTextColor.YELLOW),
                        Arrays.asList(
                                Component.text("Only players who have"),
                                Component.text("played for some time"),
                                Component.text("have this permission,")
                        )
                );
                ItemStack allItem = DisplayItem.create(
                        Material.LIME_DYE,
                        Component.text("All").color(NamedTextColor.GREEN),
                        Arrays.asList(
                                Component.text("All players can access"),
                                Component.text("this permission,")
                        )
                );
                TrustLevel curTrust = claim.getPermission(modifyingPermission);
                ItemStack enchanted = switch (curTrust) {
                    case ALL -> allItem;
                    case NONE -> noneItem;
                    case VETERANS -> vetItem;
                    case TRUSTED -> trustedItem;
                };
                ItemMeta meta = enchanted.getItemMeta();
                if (meta != null) meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                enchanted.setItemMeta(meta);
                setItem(10, curTrust == TrustLevel.NONE ? enchanted : noneItem);
                setItem(12, curTrust == TrustLevel.TRUSTED ? enchanted : trustedItem);
                setItem(14, curTrust == TrustLevel.VETERANS ? enchanted : vetItem);
                setItem(16, curTrust == TrustLevel.ALL ? enchanted : allItem);
            }
            case 4 -> {
                UUID target = managingPlayer.getUniqueId();
                EnumSet<Permission> set = EnumSet.noneOf(Permission.class);
                for (Map.Entry<XCPlayer, EnumSet<Permission>> entry : claim.getUserPermissions().entrySet()) {
                    if (entry.getKey().getUniqueId().equals(target)) {
                        set = entry.getValue();
                        break;
                    }
                }
                Iterator<Permission> it = Arrays.stream(Permission.values()).iterator();
                pickKeys.clear();
                for (int i = 0; i < 18; i++) {
                    if (it.hasNext()) {
                        Permission perm = it.next();
                        boolean value = set.contains(perm);
                        TextColor tc = (value ? NamedTextColor.GREEN : NamedTextColor.RED);
                        String lore = (value ? "Enabled" : "Disabled");
                        Material mat = (value ? Material.LIME_DYE : Material.RED_DYE);
                        setItem(i, DisplayItem.create(mat, Component.text(perm.getPrintName()).color(tc), Collections.singletonList(Component.text(lore).color(NamedTextColor.GRAY))));
                        pickKeys.put(i, perm);
                    } else {
                        break;
                    }
                }
                setItem(22, BACK_STACK);
            }
            case 5 -> {
                setItem(11, DisplayItem.create(Material.GREEN_CONCRETE, Component.text("Enabled").color(NamedTextColor.GREEN), Arrays.asList(
                        Component.text("Make this player have").color(NamedTextColor.GRAY),
                        Component.text("this permission.").color(NamedTextColor.GRAY)
                )));
                setItem(15, DisplayItem.create(Material.RED_CONCRETE, Component.text("Disabled").color(NamedTextColor.RED), Arrays.asList(
                        Component.text("Unset this permission for").color(NamedTextColor.GRAY),
                        Component.text("this player. Permission will").color(NamedTextColor.GRAY),
                        Component.text("defer to general groups.").color(NamedTextColor.GRAY)
                )));
            }
        }
    }

    @Override
    public void onEnter() {
        subPage = 0;
        populate();
    }

    @Override
    public void onClick(int slot) {
        switch (subPage) {
            case 0 -> {
                if (slot == 10) {
                    subPage = 1;
                } else if (slot == 13) {
                    subPage = 2;
                } else if (slot == 16) {
                    switchPage(new MainPage(getParent()));
                    break;
                } else  {
                    break;
                }
                populate();
            }
            case 1 -> {
                if (slot == 22) {
                    subPage = 0;
                    populate();
                    break;
                }
                Object ob = pickKeys.get(slot);
                if (ob != null) {
                    if (ob instanceof Permission perm) {
                        modifyingPermission = perm;
                        subPage = 3;
                        populate();
                    }
                }
            }
            case 3 -> {
                if (slot < 10) break;
                if (slot > 16) break;
                TrustLevel set;
                switch (slot) {
                    case 10:
                        set = TrustLevel.NONE;
                        break;
                    case 12:
                        set = TrustLevel.TRUSTED;
                        break;
                    case 14:
                        set = TrustLevel.VETERANS;
                        break;
                    case 16:
                        set = TrustLevel.ALL;
                        break;
                    default:
                        return;
                }
                claim.setPermission(modifyingPermission, set);
                subPage = 1;
                populate();
            }
            case 4 -> {
                if (slot == 22) {
                    subPage = 2;
                    populate();
                } else if (slot < 18) {
                    Object match = pickKeys.get(slot);
                    if (match != null) {
                        if (match instanceof Permission perm) {
                            modifyingPermission = perm;
                            subPage = 5;
                            populate();
                        }
                    }
                }
            }
            case 5 -> {
                if (slot == 11) {
                    claim.setUserPermission(managingPlayer, modifyingPermission, true);
                    subPage = 4;
                    populate();
                } else if (slot == 15) {
                    claim.setUserPermission(managingPlayer, modifyingPermission, false);
                    subPage = 4;
                    populate();
                }
            }
        }
    }

}
