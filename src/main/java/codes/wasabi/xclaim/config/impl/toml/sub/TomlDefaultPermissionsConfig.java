package codes.wasabi.xclaim.config.impl.toml.sub;

import codes.wasabi.xclaim.config.impl.toml.TomlConfig;
import codes.wasabi.xclaim.config.struct.sub.DefaultPermissionsConfig;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class TomlDefaultPermissionsConfig extends TomlConfig implements DefaultPermissionsConfig {

    public TomlDefaultPermissionsConfig(@Nullable Toml table) {
        super(table);
    }

    @Override
    public @UnknownNullability String defaultBuild() {
        return this.getString("build");
    }

    @Override
    public @UnknownNullability String defaultBreak() {
        return this.getString("break");
    }

    @Override
    public @UnknownNullability String defaultEnter() {
        return this.getString("enter");
    }

    @Override
    public @UnknownNullability String defaultInteract() {
        return this.getString("interact");
    }

    @Override
    public @UnknownNullability String defaultChestOpen() {
        return this.getString("chest-open");
    }

    @Override
    public @UnknownNullability String defaultEntPlace() {
        return this.getString("ent-place");
    }

    @Override
    public @UnknownNullability String defaultVehiclePlace() {
        return this.getString("vehicle_place");
    }

    @Override
    public @UnknownNullability String defaultFireUse() {
        return this.getString("fire-use");
    }

    @Override
    public @UnknownNullability String defaultEntFriendly() {
        return this.getString("entity-friendly");
    }

    @Override
    public @UnknownNullability String defaultEntDamageHost() {
        return this.getString("entity-damage-hostile");
    }

    @Override
    public @UnknownNullability String defaultEntDamageVehicle() {
        return this.getString("entity-damage-vehicle");
    }

    @Override
    public @UnknownNullability String defaultEntDamageNL() {
        return this.getString("entity-damage-nl");
    }

    @Override
    public @UnknownNullability String defaultEntDamageMisc() {
        return this.getString("entity-damage-misc");
    }

    @Override
    public @UnknownNullability String defaultExplode() {
        return this.getString("explode");
    }

    @Override
    public @UnknownNullability String defaultItemDrop() {
        return this.getString("item-drop");
    }

    @Override
    public @UnknownNullability String defaultManage() {
        return this.getString("manage");
    }

    @Override
    public @UnknownNullability String defaultDelete() {
        return this.getString("delete");
    }
}
