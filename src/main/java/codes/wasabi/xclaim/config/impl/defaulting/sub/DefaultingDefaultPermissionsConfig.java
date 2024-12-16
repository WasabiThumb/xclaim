package codes.wasabi.xclaim.config.impl.defaulting.sub;

import codes.wasabi.xclaim.config.impl.filter.sub.FilterDefaultPermissionsConfig;
import codes.wasabi.xclaim.config.struct.sub.DefaultPermissionsConfig;
import org.jetbrains.annotations.NotNull;

public final class DefaultingDefaultPermissionsConfig extends FilterDefaultPermissionsConfig {

    public DefaultingDefaultPermissionsConfig(@NotNull DefaultPermissionsConfig backing) {
        super(backing);
    }

    @Override
    public @NotNull String defaultBuild(){
        return this.nullFallback(this.backing().defaultBuild(), "TRUSTED");
    }

    @Override
    public @NotNull String defaultBreak(){
        return this.nullFallback(this.backing().defaultBreak(), "TRUSTED");
    }

    @Override
    public @NotNull String defaultEnter(){
        return this.nullFallback(this.backing().defaultEnter(), "ALL");
    }

    @Override
    public @NotNull String defaultInteract(){
        return this.nullFallback(this.backing().defaultInteract(), "VETERANS");
    }

    @Override
    public @NotNull String defaultChestOpen(){
        return this.nullFallback(this.backing().defaultChestOpen(), "TRUSTED");
    }

    @Override
    public @NotNull String defaultEntPlace(){
        return this.nullFallback(this.backing().defaultEntPlace(), "VETERANS");
    }

    @Override
    public @NotNull String defaultVehiclePlace(){
        return this.nullFallback(this.backing().defaultVehiclePlace(), "VETERANS");
    }

    @Override
    public @NotNull String defaultFireUse(){
        return this.nullFallback(this.backing().defaultFireUse(), "TRUSTED");
    }

    @Override
    public @NotNull String defaultEntFriendly(){
        return this.nullFallback(this.backing().defaultEntFriendly(), "VETERANS");
    }

    @Override
    public @NotNull String defaultEntDamageHost(){
        return this.nullFallback(this.backing().defaultEntDamageHost(), "VETERANS");
    }

    @Override
    public @NotNull String defaultEntDamageVehicle(){
        return this.nullFallback(this.backing().defaultEntDamageVehicle(), "VETERANS");
    }

    @Override
    public @NotNull String defaultEntDamageNL(){
        return this.nullFallback(this.backing().defaultEntDamageNL(), "VETERANS");
    }

    @Override
    public @NotNull String defaultEntDamageMisc(){
        return this.nullFallback(this.backing().defaultEntDamageMisc(), "ALL");
    }

    @Override
    public @NotNull String defaultExplode(){
        return this.nullFallback(this.backing().defaultExplode(), "TRUSTED");
    }

    @Override
    public @NotNull String defaultItemDrop(){
        return this.nullFallback(this.backing().defaultItemDrop(), "ALL");
    }

    @Override
    public @NotNull String defaultManage(){
        return this.nullFallback(this.backing().defaultManage(), "NONE");
    }

    @Override
    public @NotNull String defaultDelete(){
        return this.nullFallback(this.backing().defaultDelete(), "NONE");
    }

}
