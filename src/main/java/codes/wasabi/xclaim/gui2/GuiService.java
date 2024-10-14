package codes.wasabi.xclaim.gui2;

import codes.wasabi.xclaim.config.struct.sub.GuiConfig;
import codes.wasabi.xclaim.gui.GUIHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Abstraction for either the legacy GUI system ({@link GUIHandler}) or
 * the V2 GUI ({@link GuiManager})
 */
public interface GuiService {

    static @NotNull GuiService create(@NotNull GuiConfig.Version version) {
        switch (version) {
            case V1:
                return new GuiService.Legacy();
            case V2:
                return new GuiManager();
        }
        throw new AssertionError();
    }

    //

    void start();

    void stop();

    void openGui(@NotNull Player target);

    //

    @SuppressWarnings("deprecation")
    final class Legacy implements GuiService {

        @Override
        public void start() { }

        @Override
        public void stop() {
            GUIHandler.closeAll();
        }

        @Override
        public void openGui(@NotNull Player target) {
            new GUIHandler(target);
        }

    }

}
