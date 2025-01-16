package io.github.wasabithumb.xclaim.config.struct.sub;

import io.github.wasabithumb.xclaim.config.struct.Config;
import io.github.wasabithumb.xclaim.gui.dialog.GuiDialogType;
import io.github.wasabithumb.xclaim.gui.layout.GuiBasis;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Locale;

public interface GuiConfig extends Config {

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

    @UnknownNullability String dialogRaw();

    default @UnknownNullability GuiDialogType dialog() {
        String raw = this.dialogRaw();
        if (raw == null) return null;
        try {
            return GuiDialogType.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return GuiDialogType.ACTION_BAR;
        }
    }

}
