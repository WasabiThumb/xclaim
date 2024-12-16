package codes.wasabi.xclaim.config.impl.defaulting.sub;

import codes.wasabi.xclaim.config.impl.filter.sub.FilterDefaultPermissionsConfig;
import codes.wasabi.xclaim.config.struct.sub.DefaultPermissionsConfig;
import org.jetbrains.annotations.NotNull;

public final class DefaultingDefaultPermissionsConfig extends FilterDefaultPermissionsConfig {

    public DefaultingDefaultPermissionsConfig(@NotNull DefaultPermissionsConfig backing) {
        super(backing);
    }

    @Override
    public @NotNull String build(){
        return this.nullFallback(this.backing().build(), "TRUSTED");
    }

    @Override
    public @NotNull String breakBlocks(){
        return this.nullFallback(this.backing().breakBlocks(), "TRUSTED");
    }

    @Override
    public @NotNull String enter(){
        return this.nullFallback(this.backing().enter(), "ALL");
    }

    @Override
    public @NotNull String interact(){
        return this.nullFallback(this.backing().interact(), "VETERANS");
    }

    @Override
    public @NotNull String chestOpen(){
        return this.nullFallback(this.backing().chestOpen(), "TRUSTED");
    }

    @Override
    public @NotNull String entPlace(){
        return this.nullFallback(this.backing().entPlace(), "VETERANS");
    }

    @Override
    public @NotNull String vehiclePlace(){
        return this.nullFallback(this.backing().vehiclePlace(), "VETERANS");
    }

    @Override
    public @NotNull String fireUse(){
        return this.nullFallback(this.backing().fireUse(), "TRUSTED");
    }

    @Override
    public @NotNull String entDamageFriendly(){
        return this.nullFallback(this.backing().entDamageFriendly(), "VETERANS");
    }

    @Override
    public @NotNull String entDamageHostile(){
        return this.nullFallback(this.backing().entDamageHostile(), "VETERANS");
    }

    @Override
    public @NotNull String entDamageVehicle(){
        return this.nullFallback(this.backing().entDamageVehicle(), "VETERANS");
    }

    @Override
    public @NotNull String entDamageNL(){
        return this.nullFallback(this.backing().entDamageNL(), "VETERANS");
    }

    @Override
    public @NotNull String entDamageMisc(){
        return this.nullFallback(this.backing().entDamageMisc(), "ALL");
    }

    @Override
    public @NotNull String explode(){
        return this.nullFallback(this.backing().explode(), "TRUSTED");
    }

    @Override
    public @NotNull String itemDrop(){
        return this.nullFallback(this.backing().itemDrop(), "ALL");
    }

    @Override
    public @NotNull String manage(){
        return this.nullFallback(this.backing().manage(), "NONE");
    }

    @Override
    public @NotNull String delete(){
        return this.nullFallback(this.backing().delete(), "NONE");
    }

}
