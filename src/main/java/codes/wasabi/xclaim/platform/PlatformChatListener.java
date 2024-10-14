package codes.wasabi.xclaim.platform;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Consumer;

public interface PlatformChatListener extends Listener {

    void onChat(Consumer<Data> cb);

    void unregister();

    //

    /**
     * @deprecated Use {@link Data}
     */
    @ApiStatus.NonExtendable
    @Deprecated
    class PlatformChatListenerData {
        private final Player ply;
        private final String message;
        private final Runnable cancel;

        public PlatformChatListenerData(Player ply, String message, Runnable cancel) {
            this.ply = ply;
            this.message = message;
            this.cancel = cancel;
        }

        public final Player ply() {
            return ply;
        }

        public final String message() {
            return message;
        }

        public final Runnable cancel() {
            return cancel;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ply.getUniqueId(), message, cancel);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof PlatformChatListenerData) {
                if (equals((PlatformChatListenerData) obj)) return true;
            }
            return super.equals(obj);
        }

        public boolean equals(PlatformChatListenerData other) {
            if (!Objects.equals(ply, other.ply)) return false;
            if (!Objects.equals(message, other.message)) return false;
            return Objects.equals(cancel, other.cancel);
        }

        @Override
        public String toString() {
            return "PlatformChatListener.Data[ply=" + ply + ",message=" + message + ",cancel=" + cancel + "]";
        }
    }

    @ApiStatus.NonExtendable
    class Data extends PlatformChatListenerData {

        public Data(Player ply, String message, Runnable cancel) {
            super(ply, message, cancel);
        }

        public void doCancel() {
            this.cancel().run();
        }

    }

}
