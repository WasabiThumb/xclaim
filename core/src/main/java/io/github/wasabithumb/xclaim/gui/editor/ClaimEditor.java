package io.github.wasabithumb.xclaim.gui.editor;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.claim.Claim;
import io.github.wasabithumb.xclaim.claim.transaction.impl.ModifyChunksClaimTransaction;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.platform.data.PlatformPersistentDataContainer;
import io.github.wasabithumb.xclaim.platform.data.type.PlatformPersistentDataType;
import io.github.wasabithumb.xclaim.platform.data.material.NamedPlatformMaterial;
import io.github.wasabithumb.xclaim.platform.data.sound.NamedPlatformSound;
import io.github.wasabithumb.xclaim.platform.entity.PlatformEntity;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventCategory;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.PlatformListener;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformInventoryInteractEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.*;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.world.PlatformChunk;
import io.github.wasabithumb.xclaim.util.ColorTag;
import io.github.wasabithumb.xclaim.util.DisplayItem;
import io.github.wasabithumb.xclaim.util.InventorySerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ClaimEditor {

    private static final int SLOT_CLAIM = 1;
    private static final int SLOT_UNCLAIM = 4;
    private static final int SLOT_QUIT = 7;
    private static final String KEY_EDITING = "ce_editing";
    private static final String KEY_INVENTORY = "ce_inventory";

    //

    private final XClaim runtime;
    private final Worker worker;
    private final Map<UUID, Claim> cache;
    private final PlatformItem claimItem;
    private final PlatformItem unclaimItem;
    private final PlatformItem quitItem;
    public ClaimEditor(@NotNull XClaim runtime) {
        this.runtime = runtime;
        this.worker = new Worker(this);
        this.cache = Collections.synchronizedMap(new HashMap<>());
        this.claimItem = DisplayItem.format(
                runtime.platform().createItem(NamedPlatformMaterial.LIME_DYE),
                runtime.lang(I18N.CHUNK_EDITOR_CLAIM)
        );
        this.unclaimItem = DisplayItem.format(
                runtime.platform().createItem(NamedPlatformMaterial.RED_DYE),
                runtime.lang(I18N.CHUNK_EDITOR_UNCLAIM)
        );
        this.quitItem = DisplayItem.format(
                runtime.platform().createItem(NamedPlatformMaterial.BARRIER),
                runtime.lang(I18N.CHUNK_EDITOR_QUIT)
        );
    }

    @ApiStatus.Internal
    public void enable() {
        this.runtime.platform().events().register(this.worker);
    }

    @ApiStatus.Internal
    public void disable() {
        this.runtime.platform().events().unregister(this.worker);
    }

    public @Nullable Claim getEditing(@NotNull PlatformPlayer ply) {
        return this.cache.get(ply.uuid());
    }

    public boolean enter(@NotNull PlatformPlayer ply, @NotNull Claim claim) {
        if (this.getEditing(ply) != null) return false;
        UUID uuid = ply.uuid();
        PlatformInventory inv = ply.getInventory();
        PlatformPersistentDataContainer pdc = ply.pdc();
        pdc.set(
                KEY_EDITING,
                PlatformPersistentDataType.STRING,
                claim.token()
        );
        pdc.set(
                KEY_INVENTORY,
                PlatformPersistentDataType.BYTE_ARRAY,
                InventorySerializer.serialize(inv)
        );
        this.cache.put(uuid, claim);
        inv.clear();
        inv.setItem(SLOT_CLAIM, this.claimItem);
        inv.setItem(SLOT_UNCLAIM, this.unclaimItem);
        inv.setItem(SLOT_QUIT, this.quitItem);
        return true;
    }

    public boolean exit(@NotNull PlatformPlayer ply) {
        UUID uuid = ply.uuid();
        if (!this.cache.containsKey(uuid)) return false;
        this.clear(ply);
        this.cache.remove(uuid);
        return true;
    }

    private void clear(@NotNull PlatformPlayer ply) {
        PlatformInventory inv = ply.getInventory();
        PlatformPersistentDataContainer pdc = ply.pdc();
        try {
            InventorySerializer.deserialize(
                    inv,
                    pdc.getElse(
                            KEY_INVENTORY,
                            PlatformPersistentDataType.BYTE_ARRAY,
                            new byte[0]
                    )
            );
        } catch (Exception ignored) {
            inv.clear();
        }
        pdc.remove(KEY_EDITING, PlatformPersistentDataType.STRING);
        pdc.remove(KEY_INVENTORY, PlatformPersistentDataType.BYTE_ARRAY);
    }

    private void highlightChunk(@NotNull PlatformPlayer ply, @NotNull Claim claim, @NotNull PlatformChunk chunk) {
        String ref;
        ColorTag color;
        if (claim.containsChunk(chunk)) {
            ref = this.runtime.lang(I18N.CHUNK_EDITOR_INFO_CLAIMED);
            color = ColorTag.GREEN;
        } else {
            Claim cur = this.runtime.claims().getByChunk(chunk);
            if (cur == null) {
                ref = I18N.CHUNK_EDITOR_INFO_OPEN.format(this.runtime);
                color = ColorTag.GRAY;
            } else if (cur.owner().uuid().equals(ply.uuid())) {
                ref = I18N.CHUNK_EDITOR_INFO_OWNED.format(this.runtime);
                color = ColorTag.YELLOW;
            } else {
                ref = I18N.CHUNK_EDITOR_INFO_TAKEN.with(cur.owner().displayName()).format(this.runtime);
                color = ColorTag.RED;
            }
        }

        ply.sendMessage(I18N.CHUNK_EDITOR_INFO
                .with(chunk.x(), chunk.z())
                .format(this.runtime)
        );
        ply.sendMessage(color.format(ref));
        ply.playSound(NamedPlatformSound.EXP);

        int rgb = color.rgb();
        int x = chunk.x() << 4;
        int z = chunk.z() << 4;
        double y1 = ply.location().y() - 1d;
        double y2 = y1 + 4d;

        for (int i=0; i < 6; i++) {
            double y = y1 + ((y2 - y1) * (i / 5d));
            this.beam(
                    ply,
                    rgb,
                    x, y, z,
                    x + 16, y, z
            );
            this.beam(
                    ply,
                    rgb,
                    x, y, z,
                    x, y, z + 16
            );
            this.beam(
                    ply,
                    rgb,
                    x + 16, y, z,
                    x + 16, y, z + 16
            );
            this.beam(
                    ply,
                    rgb,
                    x, y, z + 16,
                    x + 16, y, z + 16
            );
        }

        for (int i=0; i < 2; i++) {
            for (int j=0; j <= 16; j++) {
                int bx = x;
                int bz = z;
                if (i == 0) {
                    bx += j;
                } else {
                    bz += j;
                }
                this.beam(
                        ply,
                        rgb,
                        bx, y1, bz,
                        bx, y2, bz
                );
                if (i == 0) {
                    bz += 16;
                } else {
                    bx += 16;
                }
                this.beam(
                        ply,
                        rgb,
                        bx, y1, bz,
                        bx, y2, bz
                );
            }
        }
    }

    private void beam(
            @NotNull PlatformPlayer ply,
            int rgb,
            double x1,
            double y1,
            double z1,
            double x2,
            double y2,
            double z2
    ) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        double mag = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double factor = 0.2d / mag;
        dx *= factor;
        dy *= factor;
        dz *= factor;

        double travelled = 0d;
        while (travelled <= mag) {
            ply.sendRedstoneParticle(rgb, x1, y1, z1);
            x1 += dx;
            y1 += dy;
            z1 += dz;
            travelled += 0.2d;
        }
    }

    //

    @SuppressWarnings("unused")
    protected record Worker(
            @NotNull ClaimEditor parent
    ) implements PlatformListener {

        private @Nullable Claim editing(@NotNull PlatformPlayer ply) {
            return this.parent.getEditing(ply);
        }

        //

        @PlatformEventHandler
        public void onDrop(@NotNull PlatformPlayerDropItemEvent event) {
            PlatformPlayer ply = event.player();
            if (this.editing(ply) != null)
                event.setCancelled(true);
        }

        @PlatformEventHandler
        public void onPickup(@NotNull PlatformEntityPickupItemEvent event) {
            PlatformEntity ent = event.entity();
            if (!(ent instanceof PlatformPlayer ply)) return;
            if (this.editing(ply) != null)
                event.setCancelled(true);
        }

        @PlatformEventHandler
        public void onClick(@NotNull PlatformInventoryClickEvent event) {
            this.onClickDrag(event);
        }

        @PlatformEventHandler
        public void onDrag(@NotNull PlatformInventoryDragEvent event) {
            this.onClickDrag(event);
        }

        private void onClickDrag(@NotNull PlatformInventoryInteractEvent event) {
            PlatformPlayer ply = event.player();
            if (ply == null) return;
            if (this.editing(ply) != null) {
                event.setCancelled(true);
            }
        }

        @PlatformEventHandler
        public void onInteract(@NotNull PlatformPlayerInteractEvent event) {
            if (event.isPhysical()) return;
            PlatformPlayer ply = event.player();

            Claim claim = this.editing(ply);
            if (claim == null) return;
            event.setCancelled(true);

            ModifyChunksClaimTransaction t;
            switch (ply.getHeldItemSlot()) {
                case SLOT_CLAIM:
                    t = claim.modifyChunks(ply)
                            .addChunk(ply.location().chunk());
                    break;
                case SLOT_UNCLAIM:
                    t = claim.modifyChunks(ply)
                            .allowDeletion(false)
                            .removeChunk(ply.location().chunk());
                    break;
                case SLOT_QUIT:
                    this.parent.exit(ply);
                    return;
                default:
                    return;
            }

            t.commit();
        }

        @PlatformEventHandler
        public void onJoin(@NotNull PlatformPlayerJoinEvent event) {
            PlatformPlayer ply = event.player();
            PlatformPersistentDataContainer pdc = ply.pdc();
            String editing = pdc.getElse(KEY_EDITING, PlatformPersistentDataType.STRING, null);
            if (editing == null) return;

            Claim claim = null;
            for (Claim c : this.parent.runtime.claims().getAll()) {
                if (c.matchesToken(editing)) {
                    claim = c;
                    break;
                }
            }
            if (claim == null) {
                this.parent.clear(ply);
                return;
            }
            this.parent.cache.put(ply.uuid(), claim);
        }

        @PlatformEventHandler
        public void onLeave(@NotNull PlatformPlayerQuitEvent event) {
            PlatformPlayer ply = event.player();
            if (this.parent.runtime.rootConfig().editor().stopOnLeave()) {
                this.parent.exit(ply);
            } else {
                this.parent.cache.remove(ply.uuid());
            }
        }

        @PlatformEventHandler(category = PlatformEventCategory.LAZY)
        public void onDeath(@NotNull PlatformEntityDeathEvent event) {
            PlatformEntity ent = event.entity();
            if (!(ent instanceof PlatformPlayer ply)) return;
            if (!this.parent.exit(ply)) return;
            if (ply.location().world().keepInventory()) return;
            List<PlatformItem> drops = event.drops();
            drops.clear();
            for (PlatformItem item : ply.getInventory().getContents()) {
                if (item == null) continue;
                drops.add(item);
            }
        }

        @PlatformEventHandler
        public void onDamage(@NotNull PlatformEntityDamagedEvent event) {
            PlatformEntity ent = event.entity();
            if (!(ent instanceof PlatformPlayer ply)) return;
            if (this.editing(ply) == null) return;
            event.setDamage(event.getDamage() * 0.1d);
        }

        @PlatformEventHandler(category = PlatformEventCategory.MONITOR)
        public void onMove(@NotNull PlatformPlayerMoveEvent event) {
            PlatformPlayer ply = event.player();
            Claim editing = this.editing(ply);
            if (editing == null) return;

            PlatformChunk a = event.getFrom().chunk();
            PlatformChunk b = event.getTo().chunk();
            if (a.x() == b.x() && a.z() == b.z()) return;

            this.parent.highlightChunk(ply, editing, b);
        }

    }

}
