package codes.wasabi.xclaim.config.struct.sub;

import codes.wasabi.xclaim.config.struct.Config;
import org.jetbrains.annotations.UnknownNullability;

public interface DefaultPermissionsConfig extends Config {

    @UnknownNullability String build();

    @UnknownNullability String breakBlocks();

    @UnknownNullability String enter();

    @UnknownNullability String interact();

    @UnknownNullability String chestOpen();

    @UnknownNullability String entPlace();

    @UnknownNullability String vehiclePlace();

    @UnknownNullability String fireUse();

    @UnknownNullability String entDamageFriendly();

    @UnknownNullability String entDamageHostile();

    @UnknownNullability String entDamageVehicle();

    @UnknownNullability String entDamageNL();

    @UnknownNullability String entDamageMisc();

    @UnknownNullability String explode();

    @UnknownNullability String itemDrop();

    @UnknownNullability String manage();

    @UnknownNullability String delete();

}
