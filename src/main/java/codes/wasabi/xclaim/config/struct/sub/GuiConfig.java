package codes.wasabi.xclaim.config.struct.sub;

import codes.wasabi.xclaim.config.struct.Config;
import codes.wasabi.xclaim.gui2.layout.GuiBasis;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Locale;

public interface GuiConfig extends Config {

    @UnknownNullability Integer versionRaw();

    default @UnknownNullability Version version() {
        Integer raw = this.versionRaw();
        if (raw == null) return null;
        return (raw == 2) ? Version.V2 : Version.V1;
    }

    @UnknownNullability Integer height();

    @UnknownNullability String basisRaw();

    default @UnknownNullability GuiBasis basis() {
        String raw = this.basisRaw();
        if (raw == null) return null;
        try {
             return GuiBasis.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return GuiBasis.LEFT;
        }
    }

    //

    enum Version {
        V1,
        V2
    }

}
