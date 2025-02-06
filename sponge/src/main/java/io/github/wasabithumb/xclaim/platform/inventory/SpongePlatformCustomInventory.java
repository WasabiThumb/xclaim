package io.github.wasabithumb.xclaim.platform.inventory;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

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

        ViewableInventory.Builder.EndStep builder = ViewableInventory.builder()
                .type(matchContainerType(size))
                .completeStructure()
                .carrier(carrier)
                .plugin(platform.plugin());

        Inventory inventory;
        try {
            // Seriously don't ask
            inventory = (Inventory) ViewableInventory.Builder.EndStep.class.getMethod("build").invoke(builder);
        } catch (ReflectiveOperationException | SecurityException e) {
            throw new AssertionError(e);
        }

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

    private static @NotNull ContainerType matchContainerType(int size) {
        return switch (Math.ceilDiv(size, 9)) {
            case 0, 1 -> ContainerTypes.GENERIC_9X1.get();
            case 2 -> ContainerTypes.GENERIC_9X2.get();
            case 3 -> ContainerTypes.GENERIC_9X3.get();
            case 4 -> ContainerTypes.GENERIC_9X4.get();
            case 5 -> ContainerTypes.GENERIC_9X5.get();
            case 6 -> ContainerTypes.GENERIC_9X6.get();
            default -> throw new IllegalArgumentException("Unable to match requested size " + size + " to a Container");
        };
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

    public @NotNull ViewableInventory asViewable() {
        if (this.handle instanceof ViewableInventory v) return v;
        return this.handle.asViewable().orElseThrow(IllegalStateException::new);
    }

}
