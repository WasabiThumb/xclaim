package codes.wasabi.xclaim.protection;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.protection.impl.worldguard.WorldGuardProtectionService;
import codes.wasabi.xclaim.util.service.ServiceFactory;
import codes.wasabi.xclaim.util.service.ServiceInitException;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public abstract class ProtectionService {

    private static boolean initialized = false;
    private static boolean available = false;
    private static ProtectionService service = null;

    public static @Nullable ProtectionService get() {
        if (initialized) return service;
        try {
            ServiceFactory<ProtectionService> factory = new ServiceFactory<>(
                    WorldGuardProtectionService.class
            );
            service = factory.createElseNull(XClaim.mainConfig.getBoolean("worldguard-integration.debug", false));
            available = service != null;
        } finally {
            initialized = true;
        }
        return service;
    }

    public static @NotNull ProtectionService getNonNull() {
        return Objects.requireNonNull(get());
    }

    public static boolean isAvailable() {
        if (!initialized) get();
        return available;
    }

    public ProtectionService() throws ServiceInitException {
    }

    public abstract Collection<ProtectionRegion> getRegionsAt(Chunk chunk);

}
