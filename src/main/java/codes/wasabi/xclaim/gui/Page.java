package codes.wasabi.xclaim.gui;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class Page {

    private final GUIHandler parent;
    protected boolean suspended = false;
    private String awaitPrompt;
    private Consumer<String> awaitCallback;
    private boolean awaiting = false;
    public Page(@NotNull GUIHandler parent) {
        this.parent = parent;
    }

    public final @NotNull GUIHandler getParent() {
        return parent;
    }

    public abstract void onEnter();
    public void onExit() {
        if (awaiting) {
            awaitCallback = ((String s) -> {});
        }
        awaiting = false;
    }

    public void onClick(int slot) {

    }

    public boolean onMessage(@NotNull String message) {
        if (awaiting) {
            awaitCallback.accept(message);
            awaiting = false;
            return true;
        }
        return false;
    }

    public void onTick() {
        if (awaiting) {
            Audience audience = Platform.getAdventure().player(getTarget());
            audience.sendActionBar(Component.text(awaitPrompt).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        }
    }

    protected final void prompt(@NotNull String prompt, @NotNull Consumer<String> callback) {
        boolean ticking = parent.getShouldTick();
        parent.setShouldTick(true);
        suspend();
        Player target = getTarget();
        target.playSound(target.getLocation(), Platform.get().getExpSound(), 1f, 1f);
        awaitPrompt = prompt;
        awaitCallback = ((String s) -> {
            awaiting = false;
            parent.setShouldTick(ticking);
            Bukkit.getScheduler().runTask(XClaim.instance, () -> {
                unsuspend();
                callback.accept(s);
            });
        });
        awaiting = true;
    }

    protected final @NotNull CompletableFuture<String> prompt(@NotNull String prompt) {
        CompletableFuture<String> future = new CompletableFuture<>();
        prompt(prompt, future::complete);
        return future;
    }

    protected final void switchPage(@Nullable Page newPage) {
        parent.switchPage(newPage);
    }

    protected final @NotNull Player getTarget() {
        return parent.getTarget();
    }

    protected final int getPageIndex(int x, int y) {
        return y * 9 + x;
    }

    protected final int[] getCoordinates(int index) {
        int x = index % 9;
        int y = (index - x) / 9;
        return new int[]{ x, y };
    }

    protected final void setItem(int index, @Nullable ItemStack is) {
        parent.getInventory().setItem(index, is);
    }

    protected final void setItem(int x, int y, @Nullable ItemStack is) {
        parent.getInventory().setItem(getPageIndex(x, y), is);
    }

    protected final @Nullable ItemStack getItem(int index) {
        return parent.getInventory().getItem(index);
    }

    protected final @Nullable ItemStack getItem(int x, int y) {
        return parent.getInventory().getItem(getPageIndex(x, y));
    }

    protected final void clear() {
        parent.getInventory().clear();
    }

    protected final void suspend(boolean suspend) {
        if (suspended == suspend) return;
        if (suspend) {
            suspended = true;
            Inventory iv = parent.getInventory();
            Platform.get().closeInventory(iv);
        } else {
            parent.getTarget().openInventory(parent.getInventory());
            suspended = false;
        }
    }

    protected final void suspend() {
        suspend(true);
    }

    protected final void unsuspend() {
        suspend(false);
    }

}
