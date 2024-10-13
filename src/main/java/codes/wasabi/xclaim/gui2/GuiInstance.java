package codes.wasabi.xclaim.gui2;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.gui2.action.GuiAction;
import codes.wasabi.xclaim.gui2.action.GuiActionType;
import codes.wasabi.xclaim.gui2.action.impl.PromptGuiAction;
import codes.wasabi.xclaim.gui2.action.impl.TransferGuiAction;
import codes.wasabi.xclaim.gui2.dialog.GuiDialog;
import codes.wasabi.xclaim.gui2.layout.GuiLayout;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import codes.wasabi.xclaim.gui2.layout.map.GuiSlotMap;
import codes.wasabi.xclaim.gui2.spec.GuiSpec;
import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiInstance implements InventoryHolder {

    public static @NotNull GuiInstance open(@NotNull GuiManager manager, @NotNull Player player, @NotNull GuiSpec spec) {
        final GuiInstance ret = new GuiInstance(manager, player);
        ret.setSpecInternal(spec, 6);
        ret.inventory = Platform.get().createInventory(
                ret,
                9 * ret.layout.getHeight(),
                XClaim.lang.getComponent("gui-name")
        );
        ret.populate();
        player.openInventory(ret.inventory);
        return ret;
    }

    //

    private final GuiManager manager;
    private final Player player;
    private GuiSpec spec;
    private GuiLayout layout;
    private GuiSlotMap slotMap;
    private Inventory inventory;
    private GuiDialog dialog = null;

    GuiInstance(@NotNull GuiManager manager, @NotNull Player player) {
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

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public @NotNull Inventory inventory() {
        return this.inventory;
    }

    public @NotNull Player player() {
        return this.player;
    }

    public @NotNull Audience audience() {
        return Platform.getAdventure().player(this.player);
    }

    public void playSound(@NotNull Sound sound, float volume, float pitch) {
        this.player.playSound(this.player.getLocation(), sound, volume, pitch);
    }

    public synchronized @Nullable GuiSlot getSlot(int index) {
        return this.layout.getSlot(index);
    }

    public void set(@Nullable GuiSlot slot, int index, @Nullable ItemStack item) {
        if (slot == null) return;

        final int lw = this.layout.getWidth();
        final int sw = slot.width();
        int y = Math.floorDiv(index, sw);
        int x = index - (y * sw);

        this.inventory.setItem(((y + slot.y()) * lw) + x + slot.x(), item);
    }

    public void set(int slotIndex, int index, @Nullable ItemStack item) {
        this.set(this.getSlot(slotIndex), index, item);
    }

    public void set(@Nullable GuiSlot slot, @Nullable ItemStack item) {
        this.set(slot, 0, item);
    }

    public void set(int slotIndex, @Nullable ItemStack item) {
        this.set(this.getSlot(slotIndex), 0, item);
    }

    // Actions

    public void close() {
        this.destroyPrompt();
        this.manager.untrack(this);
        this.inventory.close();
    }

    public void prompt(@NotNull Component message) {
        this.manager.addChatTicket(this.player, this);
        this.inventory.close();
        this.dialog = GuiDialog.show(this.player, message);
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

        GuiAction act = this.spec.onClick(this, ob, ob.calculateLocalIndex(x, y));
        this.executeAction(act);
    }

    public void respond(@NotNull String message) {
        this.destroyPrompt();
        GuiAction act = this.spec.onResponse(this, message);
        if (act.type() == GuiActionType.EXIT) {
            this.manager.untrack(this);
            return;
        }
        this.executeAction(act);
        this.player.openInventory(this.inventory);
    }

}
