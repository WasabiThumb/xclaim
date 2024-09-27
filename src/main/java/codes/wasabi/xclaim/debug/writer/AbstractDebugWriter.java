package codes.wasabi.xclaim.debug.writer;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

abstract class AbstractDebugWriter implements DebugWriter {

    private TextColor color = NamedTextColor.WHITE;

    @Override
    public void color(@NotNull TextColor color) {
        this.color = color;
    }

    @Override
    public void println(@NotNull CharSequence text) {
        this.println(text, this.color);
    }

    protected abstract void println(@NotNull CharSequence text, @NotNull TextColor color);

    @Override
    public void raise(@NotNull Throwable t) {
        String trace;

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             OutputStreamWriter writer1 = new OutputStreamWriter(bos, StandardCharsets.UTF_8);
             PrintWriter writer2 = new PrintWriter(writer1)) {

            t.printStackTrace(writer2);
            writer2.flush();
            trace = new String(bos.toByteArray(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            String msg = t.getMessage();
            if (msg == null) msg = "Unknown error";
            this.println("* " + msg, NamedTextColor.DARK_RED);
            return;
        }

        for (String line : trace.split("\n")) {
            this.println("* " + line, NamedTextColor.RED);
        }
    }

}
