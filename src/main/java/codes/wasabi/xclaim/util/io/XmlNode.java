package codes.wasabi.xclaim.util.io;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class XmlNode {

    private final String tagName;
    private final Map<String, String> attributes;
    private final List<XmlNode> children;
    public XmlNode(String tagName, Map<String, String> attributes, List<XmlNode> children) {
        this.tagName = tagName;
        this.attributes = Collections.unmodifiableMap(attributes);
        this.children = Collections.unmodifiableList(children);
    }

    public @NotNull String tagName() {
        return this.tagName;
    }

    public @NotNull Map<String, String> attributes() {
        return this.attributes;
    }

    public @NotNull List<XmlNode> children() {
        return this.children;
    }

}
