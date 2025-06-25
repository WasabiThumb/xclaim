package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import io.github.wasabithumb.xclaim.platform.entity.PlatformPlayer;
import io.github.wasabithumb.xclaim.platform.entity.SpongePlatformPlayer;
import io.github.wasabithumb.xclaim.util.MojAPI;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.user.UserManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SpongePlatformUserManager implements PlatformUserManager {

    protected final SpongePlatform platform;
    protected final SpongePlatformConsoleUser console;

    public SpongePlatformUserManager(@NotNull SpongePlatform platform) {
        this.platform = platform;
        this.console = new SpongePlatformConsoleUser(platform);
    }

    //

    protected @NotNull UserManager handle() {
        return this.platform.server().userManager();
    }

    //

    @Override
    public @NotNull SpongePlatformConsoleUser console() {
        return this.console;
    }

    @Override
    public @NotNull List<PlatformPlayer> players() {
        Collection<ServerPlayer> sps = this.platform.server().onlinePlayers();
        List<PlatformPlayer> ret = new ArrayList<>(sps.size());
        for (ServerPlayer sp : sps) {
            ret.add(new SpongePlatformPlayer(this.platform, sp));
        }
        return Collections.unmodifiableList(ret);
    }

    @Override
    public int playerCount() {
        return this.platform.server().onlinePlayers().size();
    }

    @Override
    public @NotNull PlatformUser getUser(@NotNull UUID uuid) {
        if (uuid.equals(PlatformConsoleUser.ID))
            return this.console;

        Optional<ServerPlayer> ply = this.platform.server().player(uuid);
        if (ply.isPresent()) {
            return new SpongePlatformPlayer(this.platform, ply.get());
        }

        if (this.handle().exists(uuid)) {
            Optional<User> user = this.handle()
                    .load(uuid)
                    .getNow(Optional.empty());

            if (user.isPresent()) {
                return new SpongePlatformUser(this.platform, user.get());
            } else {
                this.platform.logger().warn("Unable to lookup user ({}) instantly, may now block", uuid);
            }
        }

        User ret = this.blockOnFuture(this.handle().loadOrCreate(uuid));
        return new SpongePlatformUser(this.platform, ret);
    }

    @Override
    public @Nullable PlatformUser matchUser(@NotNull String name) {
        Optional<ServerPlayer> ply = this.platform.server().player(name);
        if (ply.isPresent()) {
            return new SpongePlatformPlayer(this.platform, ply.get());
        }

        Optional<User> known = this.blockOnFuture(this.handle().load(name));
        if (known.isPresent()) {
            return new SpongePlatformUser(this.platform, known.get());
        }

        Logger l = this.platform.logger();
        UUID uuid = MojAPI.api(l::warn, l::error).getProfile(name);
        if (uuid == null) return null;

        GameProfile gp = GameProfile.of(uuid, name);
        return this.blockOnFuture(this.handle().load(gp))
                .map(user -> (PlatformUser) new SpongePlatformUser(this.platform, user))
                .orElseGet(() -> this.getUser(uuid));
    }

    @Override
    public @Nullable PlatformPlayer getPlayer(@NotNull UUID uuid) {
        return this.platform.server()
                .player(uuid)
                .map((ServerPlayer sp) -> new SpongePlatformPlayer(this.platform, sp))
                .orElse(null);
    }

    //

    private <T> @UnknownNullability T blockOnFuture(@NotNull CompletableFuture<T> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause == null) cause = e;
            if (cause instanceof RuntimeException re) throw re;
            throw new AssertionError("Unexpected exception while awaiting user data", cause);
        }
    }

}
