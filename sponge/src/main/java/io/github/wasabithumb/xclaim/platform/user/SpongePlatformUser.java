package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.entity.SpongePlatformPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.UUID;

public class SpongePlatformUser implements PlatformUser {

    protected final SpongePlatform platform;
    protected final User handle;
    private transient ServerPlayer online = null;

    public SpongePlatformUser(@NotNull SpongePlatform platform, @NotNull User handle) {
        this.platform = platform;
        this.handle = handle;
    }

    //

    @Override
    public @NotNull User handle() {
        return this.handle;
    }

    protected synchronized @Nullable ServerPlayer asOnline() {
        ServerPlayer sp = this.online;
        if (sp == null || !sp.isOnline()) {
            sp = this.handle.player().orElse(null);
            this.online = sp;
        }
        return sp;
    }

    //

    @Override
    public @NotNull UUID uuid() {
        return this.handle.uniqueId();
    }

    @Override
    public @NotNull String displayName() {
        String name = this.handle.name();
        if (name == null) name = this.handle.uniqueId().toString();
        return name;
    }

    @Override
    public boolean isOffline() {
        return !this.handle.isOnline();
    }

    @Override
    public boolean isPlayer() {
        return this.asOnline() != null;
    }

    @Override
    public @NotNull PlatformPlayer asPlayer() {
        ServerPlayer sp = this.asOnline();
        if (sp == null) throw new IllegalStateException("User is not an online player");
        return new SpongePlatformPlayer(this.platform, sp);
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.handle.hasPermission(permission);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.handle.player()
                .ifPresent(serverPlayer -> serverPlayer.sendMessage(this.platform.mm().deserialize(message)));
    }

}
