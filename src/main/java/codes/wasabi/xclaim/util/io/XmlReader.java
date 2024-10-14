package codes.wasabi.xclaim.util.io;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * An XML reader that I rolled for the purpose of reading GUI Layouts ({@code resources/layouts/*.xml}).
 * Not for general purpose.
 */
public class XmlReader extends FilterReader {

    protected State state = State.READ_TXT;
    protected final Deque<String> stack = new LinkedList<>();
    private final StringBuilder buf = new StringBuilder();
    private int counter = 0;
    private int peeked = -1;

    public XmlReader(@NotNull Reader in) {
        super(in);
    }

    protected void setState(@NotNull State state) {
        this.state = state;
        this.buf.setLength(0);
    }

    public @NotNull XmlNode readDocument() throws IOException {
        XmlNode document = this.readNodeAssert();
        if (this.readSymbol() != null) this.throwPositioned("Expected end of document");
        return document;
    }

    public @NotNull XmlNode readNodeAssert() throws IOException {
        XmlNode ret = this.readNode();
        if (ret == null) this.throwEOF();
        return ret;
    }

    public @Nullable XmlNode readNode() throws IOException {
        Symbol symbol = this.readSymbol();
        if (symbol == null || symbol.type() != Symbol.Type.TAG_OPENING) this.throwPositioned("Expected opening tag");
        return this.readNode0(symbol.tag());
    }

    private @NotNull XmlNode readNode0(@NotNull String tagName) throws IOException {
        final Map<String, String> attrs = new HashMap<>();
        final List<XmlNode> children = new LinkedList<>();
        Symbol symbol;

        outer:
        while ((symbol = this.readSymbol()) != null) {
            switch (symbol.type()) {
                case TAG_OPENING:
                    children.add(this.readNode0(symbol.tag()));
                    break;
                case TAG_CLOSING:
                    break outer;
                case ATTRIBUTE:
                    AttributeSymbol attr = (AttributeSymbol) symbol;
                    if (attrs.containsKey(attr.key))
                        this.throwPositioned("Tag <" + tagName + "> has duplicate attribute \"" + attr.key + "\"");
                    attrs.put(attr.key, attr.value);
                    break;
                case TEXT:
                    // TODO: Don't handle this unless we actually need it.
                    break;
            }
        }

        return new XmlNode(tagName, attrs, children);
    }

    public @Nullable Symbol readSymbol() throws IOException {
        int read;
        Symbol symbol = null;
        while (true) {
            if (this.peeked != -1) {
                read = this.peeked;
                this.peeked = -1;
            } else {
                read = this.in.read();
                if (read == -1) break;
            }
            symbol = this.readSymbolInternal((char) read);
            this.counter++;
            if (symbol != null) break;
        }
        return symbol;
    }

    protected @Nullable Symbol readSymbolInternal(char c) throws IOException {
        // The left angle bracket is a globally special char, let's check it first
        if (c == '<') {
            if (this.state == State.READ_TXT) {
                final Symbol ret = (this.buf.length() == 0) ? null : Symbol.text(this.buf.toString());
                this.setState(State.READ_TAG);
                return ret;
            } else {
                this.throwUnexpectedSymbol(c);
            }
        }
        switch (this.state) {
            case READ_TXT:
                return this.readSymbolForText(c);
            case READ_TAG:
                return this.readSymbolForTag(c);
            case READ_ATTRS:
                return this.readSymbolForAttrs(c);
            case READ_COMMENT:
                if (c == '>') this.state = State.READ_TXT;
                break;
        }
        return null;
    }

    protected @Nullable Symbol readSymbolForText(char c) throws IOException {
        if (Character.isWhitespace(c)) {
            if (this.buf.length() == 0) return null;
        } else if (this.stack.isEmpty()) {
            this.throwPositioned("Text literal at root of document");
        }
        this.buf.append(c);
        return null;
    }

    protected @Nullable Symbol readSymbolForTag(char c) throws IOException {
        String tagName = null;
        boolean selfClose = false;

        final boolean initEmpty = this.buf.length() == 0;
        if (initEmpty && c == '!') {
            // comment
            this.state = State.READ_COMMENT;
            return null;
        }

        switch (c) {
            case '/':
                if (initEmpty) {
                    this.buf.append(c);
                    break;
                }
                final char next = this.pop();
                if (next != '>') throwUnexpectedSymbol(next);
                selfClose = true;
            case '>':
                tagName = this.buf.toString();
                this.setState(State.READ_TXT);
                break;
            default:
                final boolean startAttrs = (c == ' ');
                if (!startAttrs) this.buf.append(c);
                if (startAttrs || this.peek() == '/') {
                    tagName = this.buf.toString();
                    this.buf.setLength(0);
                }
                if (startAttrs) {
                    this.state = State.READ_ATTRS;
                }
                break;
        }

        if (tagName != null) {
            if (tagName.isEmpty()) this.throwPositioned("Empty tag");
            boolean closing = selfClose;
            if (tagName.charAt(0) == '/') {
                if (selfClose || tagName.length() < 2) this.throwPositioned("Closing tag uses self-closing syntax");
                closing = true;
                tagName = tagName.substring(1);
            }
            if (closing) {
                final String head = this.stack.pollLast();
                if (!tagName.equals(head)) this.throwPositioned("Closing tag </" + tagName + "> without matching open tag");
                return Symbol.tagClosing(tagName);
            } else {
                this.stack.addLast(tagName);
                return Symbol.tagOpening(tagName);
            }
        }
        return null;
    }

    protected @Nullable Symbol readSymbolForAttrs(char c) throws IOException {
        State targetState;

        switch (c) {
            case '>':
                targetState = State.READ_TXT;
                break;
            case '/':
                final String tagName = this.stack.pollLast();
                assert tagName != null;
                final char next = this.pop();
                if (next != '>') this.throwUnexpectedSymbol(next);
                this.setState(State.READ_TXT);
                return Symbol.tagClosing(tagName);
            case ' ':
                targetState = State.READ_ATTRS;
                break;
            default:
                this.buf.append(c);
                if (this.peek() == '/') {
                    targetState = State.READ_ATTRS;
                    break;
                }
                return null;
        }

        String attr = this.buf.toString();
        Symbol ret = null;
        if (!attr.isEmpty()) {
            int whereEq = attr.indexOf('=');
            if (whereEq == -1) {
                ret = Symbol.attribute(attr, "");
            } else if (whereEq == 0) {
                this.throwPositioned("Attribute has no key");
            } else {
                String value = attr.substring(whereEq + 1);
                attr = attr.substring(0, whereEq);

                final int vl = value.length();
                char q;
                if (vl < 2 ||
                        (value.charAt(0) != (q = '"') && value.charAt(0) != (q = '\'')) ||
                        value.charAt(vl - 1) != q
                ) {
                    this.throwPositioned("Illegal attribute value (must be delimited by quotes, no leading whitespace)");
                } else {
                    ret = Symbol.attribute(attr, value.substring(1, vl - 1));
                }
            }
        }

        this.setState(targetState);
        return ret;
    }

    @Override
    public void close() throws IOException {
        try {
            if (this.state == State.READ_TAG || this.state == State.READ_ATTRS) {
                this.throwPositioned("Partial tag");
            }
            if (!this.stack.isEmpty()) {
                this.throwPositioned("Unclosed tag: <" + this.stack.peekLast() + ">");
            }
        } finally {
            this.state = State.READ_TXT;
            this.buf.setLength(0);
            this.stack.clear();
            this.counter = 0;
            this.peeked = -1;
            super.close();
        }
    }

    @Contract("_ -> fail")
    protected void throwUnexpectedSymbol(char symbol) throws IOException {
        this.throwPositioned("Unexpected symbol: " + symbol);
    }

    @Contract("_ -> fail")
    protected void throwPositioned(@NotNull String message) throws IOException {
        throw new IOException(message + " @ char " + this.counter);
    }

    @Contract(" -> fail")
    protected void throwEOF() throws EOFException {
        throw new EOFException("Unexpected end of stream @ char " + this.counter);
    }

    protected char peek() throws IOException {
        if (this.peeked != -1) throw new IllegalStateException("peek() called twice in symbol handling");
        int next = this.in.read();
        if (next == -1) this.throwEOF();
        this.peeked = next;
        return (char) next;
    }

    protected char pop() throws IOException {
        int next = this.in.read();
        if (next == -1) this.throwEOF();
        this.counter++;
        return (char) next;
    }

    // START State
    protected enum State {
        READ_TXT,
        READ_TAG,
        READ_ATTRS,
        READ_COMMENT
    }
    // END State

    // START Symbols
    public interface Symbol {

        static @NotNull TagSymbol tagOpening(@NotNull String tag) {
            return new TagSymbol.Opening(tag);
        }

        static @NotNull TagSymbol tagClosing(@NotNull String tag) {
            return new TagSymbol.Closing(tag);
        }

        static @NotNull AttributeSymbol attribute(@NotNull String key, @Nullable String value) {
            return new AttributeSymbol(key, value);
        }

        static @NotNull TextSymbol text(@Nullable String value) {
            return new TextSymbol(value);
        }

        @NotNull Type type();

        default @NotNull String tag() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        enum Type {
            TAG_OPENING,
            TAG_CLOSING,
            ATTRIBUTE,
            TEXT
        }

    }

    public interface TagSymbol extends Symbol {

        @Override
        @NotNull String tag();

        abstract class Abstract implements TagSymbol {

            private final Type type;
            private final String tag;

            public Abstract(@NotNull String tag, boolean closing) {
                this.tag = tag;
                this.type = closing ? Type.TAG_CLOSING : Type.TAG_OPENING;
            }

            @Override
            public @NotNull Type type() {
                return this.type;
            }

            @Override
            public @NotNull String tag() {
                return this.tag;
            }

        }

        final class Opening extends Abstract {

            public Opening(@NotNull String tag) {
                super(tag, false);
            }

            @Override
            public String toString() {
                return "TagSymbol.Opening[tag=" + this.tag() + "]";
            }

        }

        final class Closing extends Abstract {

             public Closing(@NotNull String tag) {
                 super(tag, true);
             }

            @Override
            public String toString() {
                return "TagSymbol.Closing[tag=" + this.tag() + "]";
            }

        }

    }

    public static final class AttributeSymbol implements Symbol {

        private final String key;
        private final String value;
        public AttributeSymbol(@NotNull String key, @Nullable String value) {
            this.key = key;
            this.value = (value == null) ? "" : value;
        }

        @Override
        public @NotNull Type type() {
            return Type.ATTRIBUTE;
        }

        public @NotNull String key() {
            return this.key;
        }

        public @NotNull String value() {
            return this.value;
        }

        @Override
        public String toString() {
            return "AttributeSymbol[key=" + this.key + ", value=" + this.value + "]";
        }

    }

    public static final class TextSymbol implements Symbol {

        private final String value;
        public TextSymbol(@Nullable String value) {
            this.value = (value == null) ? "" : value;
        }

        @Override
        public @NotNull Type type() {
            return Type.TEXT;
        }

        public @NotNull String value() {
            return this.value;
        }

        @Override
        public String toString() {
            return "TextSymbol[value=" + this.value + "]";
        }

    }
    // END Symbols

}
