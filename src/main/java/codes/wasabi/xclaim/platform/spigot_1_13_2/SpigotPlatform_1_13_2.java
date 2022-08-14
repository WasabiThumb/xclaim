package codes.wasabi.xclaim.platform.spigot_1_13_2;

import codes.wasabi.xclaim.platform.PlatformEntityPlaceListener;
import codes.wasabi.xclaim.platform.spigot_1_13.SpigotPlatform_1_13;
import org.jetbrains.annotations.Nullable;

public class SpigotPlatform_1_13_2 extends SpigotPlatform_1_13 {

    @Override
    public boolean hasPlaceListener() {
        return true;
    }

    @Override
    public @Nullable PlatformEntityPlaceListener getPlaceListener() {
        return new SpigotPlatformEntityPlaceListener_1_13_2();
    }

}
