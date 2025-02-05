package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

public final class SpongePlatformCustomInventory<D> extends SpongePlatformInventory implements PlatformCustomInventory<D> {

    public static <T> SpongePlatformCustomInventory<T> create(
            @NotNull SpongePlatform platform,
            int size,
            @NotNull String name,
            @NotNull T customData
    ) {
        CustomCarrier<T> carrier = new CustomCarrier<>(
                platform.mm().deserialize(name),
                customData
        );
        Inventory inventory = Inventory.builder()
                .grid(9, Math.ceilDiv(size, 9))
                .completeStructure()
                .carrier(carrier)
                .plugin(platform.plugin())
                .build();
        carrier.setInventory(inventory);
        return of(platform, inventory, carrier);
    }

    public static @Nullable SpongePlatformCustomInventory<?> of(
            @NotNull SpongePlatform platform,
            @NotNull Inventory inventory
    ) {
        if (!(inventory instanceof CarriedInventory<?> carried)) return null;
        Carrier genericCarrier = carried.carrier().orElse(null);
        if (!(genericCarrier instanceof CustomCarrier<?> carrier)) return null;
        return of(platform, inventory, carrier);
    }

    private static <T> @NotNull SpongePlatformCustomInventory<T> of(
            @NotNull SpongePlatform platform,
            @NotNull Inventory inventory,
            @NotNull CustomCarrier<T> carrier
    ) {
        return new SpongePlatformCustomInventory<>(platform, inventory, carrier);
    }

    //

    private final CustomCarrier<D> carrier;

    private SpongePlatformCustomInventory(
            @NotNull SpongePlatform platform,
            @NotNull Inventory inventory,
            @NotNull CustomCarrier<D> carrier
    ) {
        super(platform, inventory);
        this.carrier = carrier;
    }

    //

    @Override
    public @NotNull Component title() {
        return this.carrier.title();
    }

    @Override
    public @NotNull D data() {
        return this.carrier.data();
    }

}
