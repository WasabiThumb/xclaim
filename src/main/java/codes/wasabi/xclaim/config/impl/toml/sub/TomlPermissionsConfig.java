package codes.wasabi.xclaim.config.impl.toml.sub;

import codes.wasabi.xclaim.config.impl.toml.TomlConfig;
import codes.wasabi.xclaim.config.struct.sub.PermissionsConfig;
import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class TomlPermissionsConfig extends TomlConfig implements PermissionsConfig {

    public TomlPermissionsConfig(@Nullable Toml table) {
        super(table);
    }

    @Override
    public @UnknownNullability String defaultBuild() {
        return this.getString("default-build");
    }

    @Override
    public @UnknownNullability String defaultBreak() {
        return this.getString("default-break");
    }

    @Override
    public @UnknownNullability String defaultEnter() {
        return this.getString("default-enter");
    }

    @Override
    public @UnknownNullability String defaultInteract() {
        return this.getString("default-interact");
    }

    @Override
    public @UnknownNullability String defaultChestOpen() {
        return this.getString("default-chest-open");
    }

    @Override
    public @UnknownNullability String defaultEntPlace() {
        return this.getString("default-ent-place");
    }

    @Override
    public @UnknownNullability String defaultVehiclePlace() {
        return this.getString("default-vehicle_place");
    }

    @Override
    public @UnknownNullability String defaultFireUse() {
        return this.getString("default-fire-use");
    }

    @Override
    public @UnknownNullability String defaultEntFriendly() {
        return this.getString("default-entity-friendly");
    }

    @Override
    public @UnknownNullability String defaultEntDamageHost() {
        return this.getString("default-entity-damage-hostile");
    }

    @Override
    public @UnknownNullability String defaultEntDamageVehicle() {
        return this.getString("default-entity-damage-vehicle");
    }

    @Override
    public @UnknownNullability String defaultEntDamageNL() {
        return this.getString("deafault-entity-damage-nl");
    }

    @Override
    public @UnknownNullability String defaultEntDamageMisc() {
        return this.getString("default-entity-damage-misc");
    }

    @Override
    public @UnknownNullability String defaultExplode() {
        return this.getString("default-explode");
    }

    @Override
    public @UnknownNullability String defaultItemDrop() {
        return this.getString("default-item-drop");
    }

    @Override
    public @UnknownNullability String defaultManage() {
        return this.getString("default-manage");
    }

    @Override
    public @UnknownNullability String defaultDelete() {
        return this.getString("default-delete");
    }
}
