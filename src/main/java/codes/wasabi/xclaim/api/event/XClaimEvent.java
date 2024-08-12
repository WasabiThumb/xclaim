package codes.wasabi.xclaim.api.event;

import codes.wasabi.xclaim.XClaim;
import codes.wasabi.xclaim.platform.Platform;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Superclass & helpers for all XClaim events.
 * @see XClaimCreateClaimEvent
 * @see XClaimDeleteClaimEvent
 * @see XClaimAddChunksToClaimEvent
 * @see XClaimAddChunkToClaimEvent
 * @see XClaimRemoveChunksFromClaimEvent
 * @see XClaimRemoveChunkFromClaimEvent
 * @see XClaimTransferOwnerEvent
 * @see XClaimGrantUserPermissionEvent
 * @see XClaimRevokeUserPermissionEvent
 */
public abstract class XClaimEvent extends Event implements Cancellable {

    @ApiStatus.Internal
    public static boolean dispatch(
            @NotNull XClaimEvent event,
            @NotNull Player player
    ) {
        Bukkit.getPluginManager().callEvent(event);
        if (!event.cancelled) return true;

        final String langFailMessage = event.getTranslatableFailMessage();
        List<String> detail = event.cancelMessages;
        Component failMessage = XClaim.lang.has(langFailMessage) ? XClaim.lang.getComponent(langFailMessage) : null;

        if ((!detail.isEmpty()) && XClaim.lang.has(langFailMessage + "-single")) {
            failMessage = XClaim.lang.getComponent(langFailMessage + "-single", detail.get(0));
        }

        if (detail.size() > 1 && XClaim.lang.has(langFailMessage + "-multi")) {
            failMessage = XClaim.lang.getComponent(langFailMessage + "-multi", String.join(", ", detail));
        }

        final Audience audience = Platform.getAdventure().player(player);
        if (failMessage != null) {
            audience.sendMessage(failMessage);
        }

        return false;
    }

    @ApiStatus.Internal
    public static boolean dispatch(
            @NotNull XClaimEvent event
    ) throws UnsupportedOperationException {
        return dispatch(event, event.getPlayer());
    }

    //

    protected boolean cancelled = false;
    protected List<String> cancelMessages = new ArrayList<>(1);

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * @deprecated Favor {@link #cancel(String)} instead of this
     */
    @ApiStatus.Obsolete
    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public void cancel(@Nullable String cancelMessage) {
        this.cancelMessages.add(cancelMessage);
        this.cancelled = true;
    }

    public @NotNull @UnmodifiableView List<String> getCancelMessages() {
        return Collections.unmodifiableList(this.cancelMessages);
    }

    protected abstract @NotNull String getTranslatableFailMessage();

    protected @NotNull Player getPlayer() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Event has no associated Player");
    }

}
