package io.github.wasabithumb.xclaim.platform.event;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.event.adapter.AdapterInstance;
import io.github.wasabithumb.xclaim.platform.event.impl.*;
import io.github.wasabithumb.xclaim.util.collections.ProxyList;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SpongePlatformEventManager extends PlatformEventManager {

    private final SpongePlatform platform;
    private final Map<Class<? extends Event>, List<AdapterInstance<?>>> adapters;
    private final Map<PlatformListener, Map<ListenerKey, List<Method>>> listeners;
    private final ReadWriteLock listenersLock;

    public SpongePlatformEventManager(@NotNull SpongePlatform platform) {
        this.platform = platform;
        this.adapters = new HashMap<>();
        this.listeners = new HashMap<>();
        this.listenersLock = new ReentrantReadWriteLock();
        this.registerAllAdapters();
        Sponge.eventManager().registerListeners(platform.plugin(), this);
    }

    private void registerAllAdapters() {
        this.registerAdapters(
                SpongePlatformBlockBreakEvent.class,
                SpongePlatformBlockFlowEvent.class,
                SpongePlatformBlockPlaceEvent.class,
                SpongePlatformChatEvent.class,
                SpongePlatformEntityDamagedEvent.class,
                SpongePlatformEntityDeathEvent.class,
                SpongePlatformEntityPickupItemEvent.class,
                SpongePlatformEntityPlaceEvent.class,
                SpongePlatformExplosionEvent.class,
                SpongePlatformHangingBreakEvent.class,
                SpongePlatformInventoryClickEvent.class,
                SpongePlatformInventoryCloseEvent.class,
                SpongePlatformInventoryDragEvent.class,
                SpongePlatformItemFrameChangeEvent.class,
                SpongePlatformPlayerDropItemEvent.class,
                SpongePlatformPlayerInteractEntityEvent.class,
                SpongePlatformPlayerInteractEvent.class,
                SpongePlatformPlayerJoinEvent.class,
                SpongePlatformPlayerMoveEvent.class,
                SpongePlatformPlayerQuitEvent.class,
                SpongePlatformPlayerTeleportEvent.class
        );
    }

    @ApiStatus.Internal
    public void shutdown() {
        Sponge.eventManager().unregisterListeners(this);
    }

    //

    @Listener(order = Order.DEFAULT)
    private void receiveNormal(@NotNull Event spongeEvent) {
        this.receive(spongeEvent, PlatformEventCategory.NORMAL);
    }

    @Listener(order = Order.POST)
    private void receiveMonitor(@NotNull Event spongeEvent) {
        this.receive(spongeEvent, PlatformEventCategory.MONITOR);
    }

    @Listener(order = Order.EARLY)
    private void receiveLazy(@NotNull Event spongeEvent) {
        this.receive(spongeEvent, PlatformEventCategory.LAZY);
    }

    private void receive(@NotNull Event spongeEvent, @NotNull PlatformEventCategory category) {
        Queue<PlatformEvent> platformEvents = this.adapt(spongeEvent);
        PlatformEvent next;
        while ((next = platformEvents.poll()) != null) {
            this.dispatch(next, category);
        }
    }

    private void dispatch(@NotNull PlatformEvent event, @NotNull PlatformEventCategory category) {
        ListenerKey key = new ListenerKey(
                PlatformEventType.of(event.getClass()),
                category
        );
        List<DispatchInfo> infos = new ArrayList<>();

        this.listenersLock.readLock().lock();
        try {
            for (Map.Entry<PlatformListener, Map<ListenerKey, List<Method>>> entry : this.listeners.entrySet()) {
                PlatformListener listener = entry.getKey();
                List<Method> methods = entry.getValue().get(key);
                if (methods == null) continue;
                infos.addAll(new ProxyList<>(
                        methods,
                        (Method m) -> new DispatchInfo(listener, m)
                ));
            }
        } finally {
            this.listenersLock.readLock().unlock();
        }

        for (DispatchInfo info : infos) {
            try {
                info.method.invoke(info.listener, event);
            } catch (ReflectiveOperationException | SecurityException e) {
                this.platform.logger().log(Level.WARN, "Error in event listener", e);
            }
        }
    }

    //

    @SafeVarargs
    private void registerAdapters(@NotNull Class<? extends SpongePlatformEvent<?>> @NotNull ... impls) {
        for (Class<? extends SpongePlatformEvent<?>> impl : impls) {
            for (AdapterInstance<?> adapter : AdapterInstance.declared(impl)) {
                List<AdapterInstance<?>> instances = this.adapters.computeIfAbsent(
                        adapter.spongeClass(),
                        (Class<?> ignored) -> new LinkedList<>()
                );
                instances.add(adapter);
            }
        }
    }

    private @NotNull Queue<PlatformEvent> adapt(@NotNull Event event) {
        Queue<PlatformEvent> ret = new LinkedList<>();
        Queue<Class<? extends Event>> types = new LinkedList<>();
        types.add(event.getClass());

        Class<? extends Event> next;
        while ((next = types.poll()) != null) {
            this.adapt0(event, next, ret);

            Class<?> superType = next.getSuperclass();
            if (superType != null && Event.class.isAssignableFrom(superType)) {
                types.add(superType.asSubclass(Event.class));
            }

            for (Class<?> superInterface : next.getInterfaces()) {
                if (Event.class.isAssignableFrom(superInterface)) {
                    types.add(superInterface.asSubclass(Event.class));
                }
            }
        }

        return ret;
    }

    private void adapt0(@NotNull Event event, @NotNull Class<? extends Event> cls, @NotNull Queue<PlatformEvent> queue) {
        List<AdapterInstance<?>> adapters = this.adapters.get(cls);
        if (adapters == null) return;

        for (AdapterInstance<?> adapter : adapters)
            this.adapt00(event, adapter, queue);
    }

    private <T extends Event> void adapt00(
            @NotNull Event event,
            @NotNull AdapterInstance<T> adapter,
            @NotNull Queue<PlatformEvent> queue
    ) {
        adapter.adapt(this.platform, adapter.spongeClass().cast(event), queue);
    }

    //

    @Override
    public void register(@NotNull PlatformListener listener) {
        List<Entry> entries = this.processEntries(listener);
        this.listenersLock.writeLock().lock();
        try {
            for (Entry entry : entries)
                this.register0(entry);
        } finally {
            this.listenersLock.writeLock().unlock();
        }
    }

    private void register0(@NotNull Entry entry) {
        Map<ListenerKey, List<Method>> map = this.listeners.computeIfAbsent(
                entry.listener(),
                (PlatformListener ignored) -> new HashMap<>()
        );
        List<Method> methods = map.computeIfAbsent(
                new ListenerKey(entry.type(), entry.category()),
                (ListenerKey ignored) -> new ArrayList<>()
        );
        methods.add(entry.method());
    }

    @Override
    public void unregister(@NotNull PlatformListener listener) {
        this.listenersLock.writeLock().lock();
        try {
            this.listeners.remove(listener);
        } finally {
            this.listenersLock.writeLock().unlock();
        }
    }

    @Override
    protected void reportIssue(@NotNull PlatformListener listener, @NotNull Throwable issue) {
        final String msg = "Listener " + listener.getClass() + " could not be registered";
        this.platform.plugin().logger().log(Level.WARN, msg, issue);
    }

    //

    private record ListenerKey(
            PlatformEventType type,
            PlatformEventCategory category
    ) { }

    private record DispatchInfo(
            PlatformListener listener,
            Method method
    ) { }

}
