package io.github.wasabithumb.xclaim.gui2;

import io.github.wasabithumb.xclaim.XClaim;
import io.github.wasabithumb.xclaim.gui2.editor.ClaimEditor;
import io.github.wasabithumb.xclaim.gui2.layout.GuiLayouts;
import io.github.wasabithumb.xclaim.gui2.spec.GuiSpecs;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.event.PlatformEventHandler;
import io.github.wasabithumb.xclaim.platform.event.PlatformListener;
import io.github.wasabithumb.xclaim.platform.event.helper.PlatformInventoryEvent;
import io.github.wasabithumb.xclaim.platform.event.impl.*;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformCustomInventory;
import io.github.wasabithumb.xclaim.platform.inventory.PlatformInventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.*;

public class GuiManager implements PlatformListener {

    private final XClaim runtime;
    private final GuiLayouts layouts;
    private final Set<GuiInstance> instances = Collections.synchronizedSet(new HashSet<>());
    private final Map<UUID, WeakReference<GuiInstance>> chatTickets = Collections.synchronizedMap(new HashMap<>());
    private final ClaimEditor editor;

    public GuiManager(@NotNull XClaim runtime) {
        this.runtime = runtime;
        this.layouts = new GuiLayouts();
        this.editor = new ClaimEditor(runtime);
    }

    @Contract(value = "-> !null", pure = true)
    public final XClaim runtime() {
        return this.runtime;
    }

    @Contract(value = "-> !null", pure = true)
    public final GuiLayouts layouts() {
        return this.layouts;
    }

    public @NotNull ClaimEditor editor() {
        return this.editor;
    }

    public void start() {
        this.layouts.startLoading();
        this.runtime.platform().events().register(this);
        this.editor.enable();
    }

    public void stop() {
        this.clear();
        this.runtime.platform().events().unregister(this);
        this.editor.disable();
    }

    public void openGui(@NotNull PlatformPlayer target) {
        final GuiInstance instance = GuiInstance.open(this, target, GuiSpecs.main());
        this.instances.add(instance);
    }

    public void clear() {
        GuiInstance[] toClose;
        int nToClose = 0;
        synchronized (this.instances) {
            final int len = this.instances.size();
            toClose = new GuiInstance[len];
            for (GuiInstance instance : this.instances) {
                toClose[nToClose++] = instance;
            }
            this.instances.clear();
        }
        for (int i=0; i < nToClose; i++)
            toClose[i].close();
        this.chatTickets.clear();
    }

    public void untrack(@NotNull GuiInstance instance) {
        this.instances.remove(instance);
    }

    void addChatTicket(@NotNull PlatformPlayer player, @NotNull GuiInstance instance) {
        this.chatTickets.put(player.uuid(), new WeakReference<>(instance));
    }

    // Listener Helpers

    private @Nullable GuiInstance getInstance(@NotNull PlatformInventory inv) {
        if (inv instanceof PlatformCustomInventory<?> pci) {
            Object custom = pci.data();
            if (custom instanceof GuiInstance gui) return gui;
        }
        return null;
    }

    private @Nullable GuiInstance getInstance(@NotNull PlatformInventoryEvent event) {
        return this.getInstance(event.inventory());
    }

    // Listeners

    @PlatformEventHandler
    public void onClick(@NotNull PlatformInventoryClickEvent event) {
        GuiInstance instance = this.getInstance(event);
        if (instance == null) return;
        event.setCancelled(true);
        instance.click(event.slot());
    }

    @PlatformEventHandler
    public void onDrag(@NotNull PlatformInventoryDragEvent event) {
        if (this.getInstance(event) != null) event.setCancelled(true);
    }

    @PlatformEventHandler
    public void onClose(@NotNull PlatformInventoryCloseEvent event) {
        GuiInstance instance = this.getInstance(event);
        if (instance == null) return;
        if (this.chatTickets.containsKey(instance.player().uuid())) return;
        this.untrack(instance);
    }

    @PlatformEventHandler
    public void onPlayerQuit(@NotNull PlatformPlayerQuitEvent event) {
        final UUID uuid = event.player().uuid();
        this.instances.removeIf((GuiInstance i) -> i.player().uuid().equals(uuid));
        this.chatTickets.remove(uuid);
    }

    @PlatformEventHandler
    public void onChat(@NotNull PlatformChatEvent event) {
        final PlatformPlayer ply = event.player();
        final UUID uuid = ply.uuid();

        final WeakReference<GuiInstance> instanceRef = this.chatTickets.remove(uuid);
        if (instanceRef == null) return;
        final GuiInstance instance = instanceRef.get();
        if (instance == null) return;

        event.setCancelled(true);
        this.runtime.platform().scheduler().synchronize(() -> instance.respond(event.plainMessage()));
    }

}
