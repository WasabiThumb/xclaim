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
    public @UnknownNullability String build() {
        return this.getString("build");
    }

    @Override
    public @UnknownNullability String breakBlocks() {
        return this.getString("break");
    }

    @Override
    public @UnknownNullability String enter() {
        return this.getString("enter");
    }

    @Override
    public @UnknownNullability String interact() {
        return this.getString("interact");
    }

    @Override
    public @UnknownNullability String chestOpen() {
        return this.getString("chest-open");
    }

    @Override
    public @UnknownNullability String entPlace() {
        return this.getString("ent-place");
    }

    @Override
    public @UnknownNullability String vehiclePlace() {
        return this.getString("vehicle-place");
    }

    @Override
    public @UnknownNullability String fireUse() {
        return this.getString("fire-use");
    }

    @Override
    public @UnknownNullability String entDamageFriendly() {
        return this.getString("entity-damage-friendly");
    }

    @Override
    public @UnknownNullability String entDamageHostile() {
        return this.getString("entity-damage-hostile");
    }

    @Override
    public @UnknownNullability String entDamageVehicle() {
        return this.getString("entity-damage-vehicle");
    }

    @Override
    public @UnknownNullability String entDamageNL() {
        return this.getString("entity-damage-nl");
    }

    @Override
    public @UnknownNullability String entDamageMisc() {
        return this.getString("entity-damage-misc");
    }

    @Override
    public @UnknownNullability String explode() {
        return this.getString("explode");
    }

    @Override
    public @UnknownNullability String itemDrop() {
        return this.getString("item-drop");
    }

    @Override
    public @UnknownNullability String manage() {
        return this.getString("manage");
    }

    @Override
    public @UnknownNullability String delete() {
        return this.getString("delete");
    }
}
