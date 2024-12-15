package codes.wasabi.xclaim.config.struct.sub;

import codes.wasabi.xclaim.api.enums.TrustLevel;
import codes.wasabi.xclaim.config.struct.Config;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Locale;

public interface PermissionsConfig extends Config {

    @UnknownNullability String defaultBuild();

    @UnknownNullability String defaultBreak();

    @UnknownNullability String defaultEnter();

    @UnknownNullability String defaultInteract();

    @UnknownNullability String defaultChestOpen();

    @UnknownNullability String defaultEntPlace();

    @UnknownNullability String defaultVehiclePlace();

    @UnknownNullability String defaultFireUse();

    @UnknownNullability String defaultEntFriendly();

    @UnknownNullability String defaultEntDamageHost();

    @UnknownNullability String defaultEntDamageVehicle();

    @UnknownNullability String defaultEntDamageNL();

    @UnknownNullability String defaultEntDamageMisc();

    @UnknownNullability String defaultExplode();

    @UnknownNullability String defaultItemDrop();

    @UnknownNullability String defaultManage();

    @UnknownNullability String defaultDelete();
}
