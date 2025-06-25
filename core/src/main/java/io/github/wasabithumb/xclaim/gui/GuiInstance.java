package io.github.wasabithumb.xclaim.gui;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.gui.action.GuiAction;
import io.github.wasabithumb.xclaim.gui.action.GuiActionType;
import io.github.wasabithumb.xclaim.gui.action.impl.PromptGuiAction;
import io.github.wasabithumb.xclaim.gui.action.impl.TransferGuiAction;
import io.github.wasabithumb.xclaim.gui.dialog.GuiDialog;
import io.github.wasabithumb.xclaim.gui.layout.GuiLayout;
import io.github.wasabithumb.xclaim.gui.layout.GuiSlot;
import io.github.wasabithumb.xclaim.gui.layout.map.GuiSlotMap;
import io.github.wasabithumb.xclaim.gui.spec.GuiSpec;
import io.github.wasabithumb.xclaim.i18n.I18N;
import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.platform.data.sound.PlatformSound;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformCustomInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformItem;
import io.github.wasabithumb.xclaim.platform.scheduler.PlatformScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiInstance {

    static @NotNull GuiInstance open(
            @NotNull GuiManager manager,
            @NotNull PlatformPlayer player,
            @NotNull GuiSpec spec
    ) {
        final GuiInstance ret = new GuiInstance(manager, player);
        ret.setSpecInternal(spec, 6);
        ret.inventory = manager.runtime().platform().createInventory(
                9 * ret.layout.getHeight(),
                manager.runtime().lang(I18N.GUI_NAME),
                ret
        );
        ret.populate();
        player.openInventory(ret.inventory);
        return ret;
    }

    //

    private final GuiManager manager;
    private final PlatformPlayer player;
    private GuiSpec spec;
    private GuiLayout layout;
    private GuiSlotMap slotMap;
    private PlatformCustomInventory<GuiInstance> inventory;
    private GuiDialog dialog = null;

    GuiInstance(@NotNull GuiManager manager, @NotNull PlatformPlayer player) {
        this.manager = manager;
        this.player = player;
    }

    public synchronized void setSpec(@NotNull GuiSpec spec) {
        if (spec != this.spec) this.setSpecInternal(spec, this.layout.getHeight());
        this.populate();
    }

    protected synchronized void setSpecInternal(@NotNull GuiSpec spec, int heightLimit) {
        final GuiLayout layout = this.manager.layouts().get(spec.layout());
        if (layout == null) {
            throw new IllegalArgumentException("Layout \"" + spec.layout() + "\" required by spec " +
                    spec.getClass().getName() + " not found");
        }
        if (layout.getHeight() > heightLimit) {
            throw new IllegalArgumentException("Layout \"" + spec.layout() + "\" required by spec " +
                    spec.getClass().getName() + " is too tall (limit: " + heightLimit +
                    ", got: " + layout.getHeight() + ")");
        }
        this.spec = spec;
        this.layout = layout;
        this.slotMap = GuiSlotMap.create(layout);
    }

    protected synchronized void populate() {
        this.inventory.clear();
        this.spec.populate(this);
    }

    public @NotNull GuiManager manager() {
        return this.manager;
    }

    public @NotNull XClaim runtime() {
        return this.manager.runtime();
    }

    public @NotNull Platform platform() {
        return this.manager.runtime().platform();
    }

    public @NotNull PlatformInventory inventory() {
        return this.inventory;
    }

    public @NotNull PlatformPlayer player() {
        return this.player;
    }

    public void playSound(@NotNull PlatformSound sound) {
        this.player.playSound(sound);
    }

    public synchronized @Nullable GuiSlot getSlot(int index) {
        return this.layout.getSlot(index);
    }

    public void set(@Nullable GuiSlot slot, int index, @Nullable PlatformItem item) {
        if (slot == null) return;

        final int lw = this.layout.getWidth();
        final int sw = slot.width();
        int y = Math.floorDiv(index, sw);
        int x = index - (y * sw);

        this.inventory.setItem(((y + slot.y()) * lw) + x + slot.x(), item);
    }

    public void set(int slotIndex, int index, @Nullable PlatformItem item) {
        this.set(this.getSlot(slotIndex), index, item);
    }

    public void set(@Nullable GuiSlot slot, @Nullable PlatformItem item) {
        this.set(slot, 0, item);
    }

    public void set(int slotIndex, @Nullable PlatformItem item) {
        this.set(this.getSlot(slotIndex), 0, item);
    }

    // Actions

    public void close() {
        this.destroyPrompt();
        this.manager.untrack(this);
        this.player.closeInventory();
    }

    public void prompt(@NotNull String message) {
        this.manager.addChatTicket(this.player, this);
        this.player.closeInventory();
        this.dialog = GuiDialog.show(this.manager().runtime(), this.player, message);
    }

    protected void destroyPrompt() {
        if (this.dialog != null) {
            this.dialog.close();
        }
        this.dialog = null;
    }

    public void executeAction(@NotNull GuiAction action) {
        switch (action.type()) {
            case EXIT:
                this.close();
                break;
            case REPOPULATE:
                this.populate();
                break;
            case PROMPT:
                this.prompt(((PromptGuiAction) action).message());
                break;
            case TRANSFER:
                this.setSpec(((TransferGuiAction) action).target());
                break;
        }
    }

    // Events

    public void click(int slot) {
        final int y = Math.floorDiv(slot, 9);
        final int x = slot - (y * 9);

        int idx = this.slotMap.get(x, y);
        if (idx == -1) return;

        GuiSlot ob = this.layout.getSlot(idx);
        if (ob == null) return;

        final GuiAction act = this.spec.onClick(this, ob, ob.calculateLocalIndex(x, y));
        this.platform().scheduler()
                .newTask()
                .targetEntity(this.player)
                .executor(() -> this.executeAction(act))
                .build();
    }

    public void respond(@NotNull String message) {
        this.destroyPrompt();
        if (this.spec.asyncResponse()) {
            PlatformScheduler scheduler = this.platform().scheduler();
            scheduler.newTask()
                    .async()
                    .executor(() -> {
                        final GuiAction act = this.spec.onResponse(this, message);
                        scheduler.newTask()
                                .targetEntity(this.player)
                                .executor(() -> this.respond0(act))
                                .build();
                    })
                    .build();
        } else {
            this.respond0(this.spec.onResponse(this, message));
        }
    }

    private void respond0(@NotNull GuiAction act) {
        if (act.type() == GuiActionType.EXIT) {
            this.manager.untrack(this);
            return;
        }
        this.executeAction(act);
        this.player.openInventory(this.inventory);
    }

}
