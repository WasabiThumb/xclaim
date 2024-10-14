package codes.wasabi.xclaim.gui2.layout.xml;

import codes.wasabi.xclaim.gui2.layout.GuiBasis;
import codes.wasabi.xclaim.gui2.layout.GuiSlot;
import codes.wasabi.xclaim.gui2.layout.memory.MemoryGuiLayout;
import codes.wasabi.xclaim.gui2.layout.memory.MemoryGuiSlot;
import codes.wasabi.xclaim.util.io.XmlNode;
import codes.wasabi.xclaim.util.io.XmlReader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class XmlGuiLayout extends MemoryGuiLayout {

    private static final String T_LAYOUT = "layout";
    private static final String T_AREA = "area";
    private static final String T_SLOT = "slot";
    private static final String T_ROW  = "row";

    private GuiBasis defaultBasis = GuiBasis.LEFT;
    public XmlGuiLayout(int height) {
        super(height);
    }

    public void setDefaultBasis(@NotNull GuiBasis defaultBasis) {
        this.defaultBasis = defaultBasis;
    }

    public void read(@NotNull InputStream in) throws IOException {
        try (InputStreamReader inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
             XmlReader xmlReader = new XmlReader(inReader)
        ) {
            this.read(xmlReader);
        }
    }

    public void read(@NotNull XmlReader in) throws IOException {
        XmlNode node = in.readDocument();
        try {
            this.read(node);
        } catch (IllegalArgumentException e) {
            throw new IOException("Malformed document", e);
        }
    }

    public void read(@NotNull XmlNode document) throws IllegalArgumentException {
        if (!document.tagName().equals(T_LAYOUT)) throw new IllegalArgumentException("Root tag is not a <layout>");
        if (!document.attributes().isEmpty()) {
            throw new IllegalArgumentException("Layout may not have top-level attributes");
        }

        this.readChildren(
                null,
                document,
                new ReadContext(this.defaultBasis, 0, 0, this.getWidth(), this.height)
        );
    }

    private void readChildren(@Nullable GuiSlot parent, @NotNull XmlNode node, @NotNull ReadContext ctx) {
        final List<XmlNode> children = node.children();
        final int childCount = children.size();
        ctx.siblingCount = childCount;
        for (int i=0; i < childCount; i++) {
            ctx.siblingIndex = i;
            this.readInternal(
                    parent,
                    children.get(i),
                    ctx.copy()
            );
        }
    }

    private void readInternal(@Nullable GuiSlot parent, @NotNull XmlNode node, @NotNull ReadContext ctx) {
        switch (node.tagName()) {
            case T_AREA:
                this.readArea(parent, node, ctx);
                return;
            case T_SLOT:
                this.readSlot(parent, node, ctx);
                return;
            case T_ROW:
                this.readRow(parent, node, ctx);
                return;
            case T_LAYOUT:
                throw new IllegalArgumentException("Tag <layout> cannot be parented to another tag");
        }
        throw new IllegalArgumentException("Unknown tag <" + node.tagName() + ">");
    }

    private void readArea(@Nullable GuiSlot parent, @NotNull XmlNode node, @NotNull ReadContext ctx) {
        ctx.readPositionBasis(parent, node);
        this.addSlot(ctx.createSlot(node, this.readId(node)));
        this.assertNoChildren(node);
    }

    private void readSlot(@Nullable GuiSlot parent, @NotNull XmlNode node, @NotNull ReadContext ctx) {
        ctx.readPositionBasis(parent, node);
        ctx.w = ctx.h = 1;
        this.addSlot(ctx.createSlot(node, this.readId(node)));
        this.assertNoChildren(node);
    }

    private void readRow(@Nullable GuiSlot parent, @NotNull XmlNode node, @NotNull ReadContext ctx) {
        ctx.readPositionBasis(parent, node);
        ctx.h = 1;

        int id = this.readId(node);
        GuiSlot slot;
        if (id == -1) {
            slot = ctx.createSlot(node, 0);
        } else {
            slot = ctx.createSlot(node, id);
            this.addSlot(slot);
        }
        this.readChildren(slot, node, ctx);
    }

    private void assertNoChildren(@NotNull XmlNode node) {
        if (!node.children().isEmpty())
            throw new IllegalArgumentException("Tag <" + node.tagName() + "> may not have children");
    }

    private int readId(@NotNull XmlNode node) {
        int index = -1;
        String str = node.attributes().get("id");
        if (str != null) {
            try {
                index = Integer.parseInt(str);
            } catch (NumberFormatException ignored) { }
        }
        return index;
    }

    //

    private static class ReadContext {

        GuiBasis basis;
        int x;
        int y;
        int w;
        int h;
        int siblingIndex;
        int siblingCount;

        ReadContext(GuiBasis basis, int x, int y, int w, int h, int siblingIndex, int siblingCount) {
            this.basis = basis;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.siblingIndex = siblingIndex;
            this.siblingCount = siblingCount;
        }

        ReadContext(GuiBasis basis, int x, int y, int w, int h) {
            this(basis, x, y, w, h, -1, -1);
        }

        public @NotNull GuiSlot createSlot(@NotNull XmlNode node, int index) {
            if (index < 0 || index > 255)
                throw new IllegalArgumentException("Tag <" + node.tagName() + "> has invalid ID: " + index);

            return new MemoryGuiSlot(index, this.x, this.y, this.w, this.h, this.basis);
        }

        public @NotNull ReadContext copy() {
            return new ReadContext(this.basis, this.x, this.y, this.w, this.h, this.siblingIndex, this.siblingCount);
        }

        public void readPositionBasis(@Nullable GuiSlot parent, @NotNull XmlNode node) {
            int nx = -1;
            int ny = this.y;
            int nw = this.w;
            int nh = this.h;
            String tmp;

            tmp = node.attributes().get("basis");
            if (tmp != null) {
                try {
                    this.basis = GuiBasis.valueOf(tmp);
                } catch (IllegalArgumentException ignored) { }
            }

            tmp = node.attributes().get("x");
            if (tmp != null) nx = this.x + this.readMetric(node, "x", tmp, 0, this.w - 1);

            tmp = node.attributes().get("y");
            if (tmp != null) ny = this.y + this.readMetric(node, "y", tmp, 0, this.h - 1);

            tmp = node.attributes().get("w");
            if (tmp != null) nw = this.readMetric(node, "w", tmp, 1, this.w);

            tmp = node.attributes().get("h");
            if (tmp != null) nh = this.readMetric(node, "h", tmp, 1, this.h);

            if (nx == -1) {
                if (parent != null) {
                    nx = parent.basis().organize(this.siblingIndex, this.siblingCount, parent.width());
                } else {
                    nx = this.x;
                }
            }

            this.x = nx;
            this.y = ny;
            this.w = nw;
            this.h = nh;
        }

        private int readMetric(@NotNull XmlNode node, @NotNull String name, @NotNull String value, int min, int max) {
            final int vl = value.length();
            if (vl == 0) this.throwInvalidValue(node, name, value, null);

            int ret = max;
            if (value.charAt(0) == '~') {
                if (vl == 1) {
                    return ret;
                } else {
                    try {
                        ret -= Integer.parseInt(value.substring(1));
                    } catch (NumberFormatException e) {
                        this.throwInvalidValue(node, name, value, e);
                    }
                }
            } else {
                try {
                    ret = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    this.throwInvalidValue(node, name, value, e);
                }
            }

            if (ret < min || ret > max) {
                throw new IllegalArgumentException("Tag <" + node.tagName() + "> has attribute \"" + name + "\" with " +
                        "numeric value " + ret + ", violating expected bounds (" + min + " - " + max + ")");
            }
            return ret;
        }

        @Contract("_, _, _, _ -> fail")
        private void throwInvalidValue(@NotNull XmlNode node, @NotNull String key, @NotNull String value, @Nullable Throwable cause) {
            final String msg = "Tag <" + node.tagName() + "> has attribute \"" + key
                    + "\" with invalid value \"" + value + "\"";
            if (cause == null) {
                throw new IllegalArgumentException(msg);
            } else {
                throw new IllegalArgumentException(msg, cause);
            }
        }

    }

}
