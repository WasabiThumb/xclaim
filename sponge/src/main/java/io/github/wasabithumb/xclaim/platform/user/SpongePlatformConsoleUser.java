package io.github.wasabithumb.xclaim.platform.user;

import io.github.wasabithumb.xclaim.platform.SpongePlatform;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

public class SpongePlatformConsoleUser implements PlatformConsoleUser {

    protected final SpongePlatform platform;
    public SpongePlatformConsoleUser(@NotNull SpongePlatform platform) {
        this.platform = platform;
    }

    //

    @Override
    public @NotNull SpongePlatform handle() {
        return this.platform;
    }

    //

    @Override
    public void sendMessage(@NotNull String message) {
        Sponge.systemSubject().sendMessage(this.platform.mm().deserialize(message));
    }

}
