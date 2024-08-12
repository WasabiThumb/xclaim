package codes.wasabi.xclaim.gui.page;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.api.Claim;
import codes.wasabi.xclaim.api.XCPlayer;
import codes.wasabi.xclaim.api.enums.Permission;
import codes.wasabi.xclaim.api.enums.TrustLevel;
import codes.wasabi.xclaim.api.event.XClaimEvent;
import codes.wasabi.xclaim.api.event.XClaimGrantUserPermissionEvent;
import codes.wasabi.xclaim.api.event.XClaimRevokeUserPermissionEvent;
import codes.wasabi.xclaim.api.event.XClaimSetPermissionEvent;
import codes.wasabi.xclaim.gui.GUIHandler;
import codes.wasabi.xclaim.gui.Page;
import codes.wasabi.xclaim.platform.Platform;
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
            Material.BUCKET,
            XClaim.lang.getComponent("gui-perm-general"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-perm-general-line1"),
                    XClaim.lang.getComponent("gui-perm-general-line2"),
                    XClaim.lang.getComponent("gui-perm-general-line3")
            )
    );

    private static final ItemStack PLAYER_STACK = DisplayItem.create(
            Platform.get().getSkeletonSkullMaterial(),
            XClaim.lang.getComponent("gui-perm-player"),
            Arrays.asList(
                    XClaim.lang.getComponent("gui-perm-player-line1"),
                    XClaim.lang.getComponent("gui-perm-player-line2"),
                    XClaim.lang.getComponent("gui-perm-player-line3"),
                    XClaim.lang.getComponent("gui-perm-player-line4"),
                    XClaim.lang.getComponent("gui-perm-player-line5")
            )
    );

    private static final ItemStack BACK_STACK = DisplayItem.create(Material.BARRIER, XClaim.lang.getComponent("gui-perm-back"));

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
            case 0:
                setItem(10, GLOBAL_STACK);
                setItem(13, PLAYER_STACK);
                setItem(16, BACK_STACK);
                break;
            case 1:
                pickKeys.clear();
                int i = 0;
                for (Permission p : Permission.values()) {
                    if (i > 17) break;
                    pickKeys.put(i, p);
                    TrustLevel tl = claim.getPermission(p);
                    Material mat;
                    TextColor col;
                    switch (tl) {
                        case NONE:
                            mat = Platform.get().getRedToken();
                            col = NamedTextColor.RED;
                            break;
                        case TRUSTED:
                            mat = Platform.get().getOrangeToken();
                            col = NamedTextColor.GOLD;
                            break;
                        case VETERANS:
                            mat = Platform.get().getYellowToken();
                            col = NamedTextColor.YELLOW;
                            break;
                        case ALL:
                            mat = Platform.get().getLimeToken();
                            col = NamedTextColor.GREEN;
                            break;
                        default:
                            return;
                    }
                    List<Component> lore = new ArrayList<>();
                    for (String s : WordWrap.wrap(p.getDescription(), 25).split(System.lineSeparator())) {
                        lore.add(Component.text(s).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                    }
                    setItem(i, DisplayItem.create(mat, Component.text(p.getPrintName()).color(col), lore));
                    i++;
                }
                setItem(22, BACK_STACK);
                break;
            case 2:
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
                break;
            case 3:
                ItemStack noneItem = DisplayItem.create(
                        Platform.get().getRedToken(),
                        XClaim.lang.getComponent("gui-perm-tl-none"),
                        Arrays.asList(
                                XClaim.lang.getComponent("gui-perm-tl-none-line1"),
                                XClaim.lang.getComponent("gui-perm-tl-none-line2")
                        )
                );
                ItemStack trustedItem = DisplayItem.create(
                        Platform.get().getOrangeToken(),
                        XClaim.lang.getComponent("gui-perm-tl-trusted"),
                        Arrays.asList(
                                XClaim.lang.getComponent("gui-perm-tl-trusted-line1"),
                                XClaim.lang.getComponent("gui-perm-tl-trusted-line2"),
                                XClaim.lang.getComponent("gui-perm-tl-trusted-line3")
                        )
                );
                ItemStack vetItem = DisplayItem.create(
                        Platform.get().getYellowToken(),
                        XClaim.lang.getComponent("gui-perm-tl-veterans"),
                        Arrays.asList(
                                XClaim.lang.getComponent("gui-perm-tl-veterans-line1"),
                                XClaim.lang.getComponent("gui-perm-tl-veterans-line2"),
                                XClaim.lang.getComponent("gui-perm-tl-veterans-line3")
                        )
                );
                ItemStack allItem = DisplayItem.create(
                        Platform.get().getLimeToken(),
                        XClaim.lang.getComponent("gui-perm-tl-all"),
                        Arrays.asList(
                                XClaim.lang.getComponent("gui-perm-tl-all-line1"),
                                XClaim.lang.getComponent("gui-perm-tl-all-line2")
                        )
                );
                TrustLevel curTrust = claim.getPermission(modifyingPermission);
                ItemStack enchanted;
                switch (curTrust) {
                    case ALL:
                        enchanted = allItem;
                        break;
                    case VETERANS:
                        enchanted = vetItem;
                        break;
                    case TRUSTED:
                        enchanted = trustedItem;
                        break;
                    default:
                        enchanted = noneItem;
                        break;
                }
                ItemMeta meta = enchanted.getItemMeta();
                if (meta != null) meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                enchanted.setItemMeta(meta);
                setItem(10, curTrust == TrustLevel.NONE ? enchanted : noneItem);
                setItem(12, curTrust == TrustLevel.TRUSTED ? enchanted : trustedItem);
                setItem(14, curTrust == TrustLevel.VETERANS ? enchanted : vetItem);
                setItem(16, curTrust == TrustLevel.ALL ? enchanted : allItem);
                break;
            case 4:
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
                for (int i1 = 0; i1 < 18; i1++) {
                    if (it.hasNext()) {
                        Permission perm = it.next();
                        boolean value = set.contains(perm);
                        Component text = XClaim.lang.getComponent(value ? "gui-perm-enabled" : "gui-perm-disabled");
                        TextColor tc = (value ? NamedTextColor.GREEN : NamedTextColor.RED);
                        Material mat = (value ? Platform.get().getLimeToken() : Platform.get().getRedToken());
                        setItem(i1, DisplayItem.create(mat, Component.text(perm.getPrintName()).color(tc), Collections.singletonList(text.color(NamedTextColor.GRAY))));
                        pickKeys.put(i1, perm);
                    } else {
                        break;
                    }
                }
                setItem(22, BACK_STACK);
                break;
            case 5:
                setItem(11, DisplayItem.create(Platform.get().getGreenConcreteMaterial(), XClaim.lang.getComponent("gui-perm-enabled"), Arrays.asList(
                        XClaim.lang.getComponent("gui-perm-enabled-line1"),
                        XClaim.lang.getComponent("gui-perm-enabled-line2")
                )));
                setItem(15, DisplayItem.create(Platform.get().getRedConcreteMaterial(), XClaim.lang.getComponent("gui-perm-disabled"), Arrays.asList(
                        XClaim.lang.getComponent("gui-perm-disabled-line1"),
                        XClaim.lang.getComponent("gui-perm-disabled-line2"),
                        XClaim.lang.getComponent("gui-perm-disabled-line3")
                )));
                break;
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
            case 0:
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
                break;
            case 1:
                if (slot == 22) {
                    subPage = 0;
                    populate();
                    break;
                }
                Object ob = pickKeys.get(slot);
                if (ob != null) {
                    if (ob instanceof Permission) {
                        modifyingPermission = ((Permission) ob);
                        subPage = 3;
                        populate();
                    }
                }
                break;
            case 3:
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
                if (!XClaimEvent.dispatch(new XClaimSetPermissionEvent(
                        this.getTarget(),
                        claim,
                        modifyingPermission,
                        claim.getPermission(modifyingPermission),
                        set
                ))) break;
                claim.setPermission(modifyingPermission, set);
                subPage = 1;
                populate();
                break;
            case 4:
                if (slot == 22) {
                    subPage = 2;
                    populate();
                } else if (slot < 18) {
                    Object match = pickKeys.get(slot);
                    if (match != null) {
                        if (match instanceof Permission) {
                            modifyingPermission = ((Permission) match);
                            subPage = 5;
                            populate();
                        }
                    }
                }
                break;
            case 5:
                boolean value;
                if (slot == 11) {
                    value = true;
                } else if (slot == 15) {
                    value = false;
                } else {
                    break;
                }
                if (!XClaimEvent.dispatch(value ?
                        new XClaimGrantUserPermissionEvent(
                                this.getTarget(),
                                claim,
                                modifyingPermission,
                                XCPlayer.of(managingPlayer)
                        ) :
                        new XClaimRevokeUserPermissionEvent(
                                this.getTarget(),
                                claim,
                                modifyingPermission,
                                XCPlayer.of(managingPlayer)
                        )
                )) return;
                claim.setUserPermission(managingPlayer, modifyingPermission, value);
                subPage = 4;
                populate();
                break;
        }
    }

}
