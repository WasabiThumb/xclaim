package io.github.wasabithumb.xclaim.platform.event;

import io.github.wasabithumb.xclaim.platform.BukkitPlatform;
import io.github.wasabithumb.xclaim.platform.Platform;
import io.github.wasabithumb.xclaim.platform.event.impl.*;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;

public abstract class BukkitPlatformEventManager extends PlatformEventManager {

    private final BukkitPlatform platform;
    private Map<PlatformEventType, BukkitPlatformEventAdapter> adapters;
    private boolean adaptersOpen;
    private final Map<PlatformListener, Registration> registrations;

    BukkitPlatformEventManager(@NotNull BukkitPlatform platform) {
        this.platform = platform;
        this.adapters = new EnumMap<>(PlatformEventType.class);
        this.adaptersOpen = true;
        this.registerAllImpls();
        this.adaptersOpen = false;
        this.adapters = Collections.unmodifiableMap(this.adapters);
        this.registrations = new HashMap<>();
    }

    protected @NotNull BukkitPlatform platform() {
        return this.platform;
    }

    protected void registerAllImpls() {
        this.registerImpls(
                BukkitPlatformBlockBreakEvent.class,
                BukkitPlatformBlockMultiPlaceEvent.class,
                BukkitPlatformBlockPlaceEvent.class,
                BukkitPlatformBlockFlowEvent.class,
                BukkitPlatformEntityPlaceEvent.class,
                BukkitPlatformExplosionEvent.class,
                BukkitPlatformEntityDamagedEvent.class,
                BukkitPlatformEntityPickupItemEvent.class,
                BukkitPlatformEntityDeathEvent.class,
                BukkitPlatformHangingBreakEvent.class,
                BukkitPlatformPlayerInteractEvent.class,
                BukkitPlatformPlayerInteractEntityEvent.class,
                BukkitPlatformPlayerDropItemEvent.class,
                BukkitPlatformPlayerMoveEvent.class,
                BukkitPlatformPlayerTeleportEvent.class,
                BukkitPlatformPlayerJoinEvent.class,
                BukkitPlatformPlayerQuitEvent.class,
                BukkitPlatformInventoryClickEvent.class,
                BukkitPlatformInventoryDragEvent.class,
                BukkitPlatformInventoryCloseEvent.class
        );
    }

    @SafeVarargs
    protected final void registerImpls(@NotNull Class<? extends BukkitPlatformEvent<?>> @NotNull ... impls) {
        for (Class<? extends BukkitPlatformEvent<?>> impl : impls)
            this.registerImpl(impl);
    }

    protected final void registerImpl(@NotNull Class<? extends BukkitPlatformEvent<?>> eventClass) {
        this.registerAdapter(this.createAdapter(eventClass));
    }

    private void registerAdapter(@NotNull BukkitPlatformEventAdapter adapter) {
        if (!this.adaptersOpen) throw new IllegalStateException("Adapters registry is closed");
        this.adapters.put(adapter.type(), adapter);
    }

    private @Nullable BukkitPlatformEventAdapter getAdapter(@NotNull PlatformEventType type) {
        return this.adapters.get(type);
    }

    //

    @Override
    public void register(@NotNull PlatformListener listener) {
        final List<Entry> entries = this.processEntries(listener);
        this.register0(listener, entries);
    }

    @Override
    public void unregister(@NotNull PlatformListener listener) {
        this.unregister0(listener);
    }

    @Override
    protected void reportIssue(@NotNull PlatformListener listener, @NotNull Throwable issue) {
        this.platform.plugin().getLogger()
                .log(Level.WARNING, "Listener " + listener.getClass() + " could not be registered", issue);
    }

    protected @NotNull EventPriority mapCategory(@NotNull PlatformEventCategory category) {
        return switch (category) {
            case LAZY -> EventPriority.LOWEST;
            case NORMAL -> EventPriority.NORMAL;
            case MONITOR -> EventPriority.MONITOR;
        };
    }

    //

    private void register0(@NotNull PlatformListener listener, @NotNull List<Entry> entries) {
        final Registration r = this.registrations.computeIfAbsent(listener, Registration::new);
        for (Entry entry : entries) this.register00(r, entry);
    }

    private void register00(@NotNull Registration r, @NotNull Entry entry) {
        BukkitPlatformEventAdapter adapter = this.getAdapter(entry.type());
        if (adapter == null) return;

        RegisteredEntry registered = r.add(entry, this.platform, adapter);
        if (registered == null) return;

        Bukkit.getPluginManager().registerEvent(
                adapter.bukkitClass(),
                r,
                this.mapCategory(entry.category()),
                registered,
                this.platform.plugin()
        );
    }

    private void unregister0(@NotNull PlatformListener listener) {
        final Registration r = this.registrations.remove(listener);
        if (r == null) return;
        this.unregister00(r);
    }

    private void unregister00(@NotNull Registration r) {
        HandlerList.unregisterAll(r);
        r.entries.clear();
    }

    @Contract("_ -> new")
    private @NotNull BukkitPlatformEventAdapter createAdapter(@NotNull Class<? extends BukkitPlatformEvent<?>> eventClass) {
        Class<? extends PlatformEvent> platformClass = null;
        for (Class<?> iface : eventClass.getInterfaces()) {
            if (PlatformEvent.class.isAssignableFrom(iface)) {
                platformClass = iface.asSubclass(PlatformEvent.class);
                break;
            }
        }
        if (platformClass == null) {
            throw new AssertionError("Class " + eventClass.getName()
                    + " does not directly implement a PlatformEvent");
        }

        Type[] typeArguments;
        if (eventClass.getGenericSuperclass() instanceof ParameterizedType superType) {
            typeArguments = superType.getActualTypeArguments();
        } else {
            typeArguments = new Type[0];
        }
        if (typeArguments.length != 1)
            throw new AssertionError("Failed to identify corresponding Bukkit event for " + eventClass.getName());

        Type unqualifiedBukkitType = typeArguments[0];
        if (!(unqualifiedBukkitType instanceof Class<?> unqualifiedBukkitClass))
            throw new AssertionError();
        Class<? extends Event> bukkitClass = unqualifiedBukkitClass.asSubclass(Event.class);

        Constructor<?> primaryConstructor = null;
        for (Constructor<?> con : eventClass.getDeclaredConstructors()) {
            Class<?>[] params = con.getParameterTypes();
            if (params.length != 2) continue;
            if (!Platform.class.isAssignableFrom(params[0])) continue;
            if (!bukkitClass.equals(params[1])) continue;
            primaryConstructor = con;
        }
        if (primaryConstructor == null) {
            throw new AssertionError("Failed to identify primary constructor for " + eventClass.getName()
                    + " (" + bukkitClass + ")");
        }

        return new BukkitPlatformEventAdapter.Reflect(
                PlatformEventType.of(platformClass),
                bukkitClass,
                platformClass,
                primaryConstructor
        );
    }

    //

    private static final class Registration implements Listener {

        final Set<RegisteredEntry> entries = new HashSet<>();
        Registration(@NotNull PlatformListener ignored) { }

        @Nullable RegisteredEntry add(
                @NotNull Entry entry,
                @NotNull BukkitPlatform platform,
                @NotNull BukkitPlatformEventAdapter adapter
        ) {
            final RegisteredEntry registered = new RegisteredEntry(entry, platform, adapter);
            if (!this.entries.add(registered)) return null;
            return registered;
        }

    }

    private static final class RegisteredEntry implements EventExecutor {

        private final Object target;
        private final Method method;
        private final BukkitPlatform platform;
        private final BukkitPlatformEventAdapter adapter;

        RegisteredEntry(
                @NotNull Entry entry,
                @NotNull BukkitPlatform platform,
                @NotNull BukkitPlatformEventAdapter adapter
        ) {
            this.target = entry.listener();
            this.method = entry.method();
            this.platform = platform;
            this.adapter = adapter;
        }

        @Override
        public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
            if (!this.adapter.bukkitClass().isInstance(event)) return;
            try {
                this.method.invoke(
                        this.target,
                        this.adapter.adapt(this.platform, event)
                );
            } catch (Throwable t) {
                throw new EventException(t);
            }
        }

        @Override
        public int hashCode() {
            return this.method.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj instanceof RegisteredEntry other) {
                return this.method.equals(other.method);
            }
            return super.equals(obj);
        }

    }

}
