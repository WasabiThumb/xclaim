package codes.wasabi.xclaim.platform;

public abstract class PlatformNamespacedKey {

    public abstract String getNamespace();

    public abstract String getKey();

    @Override
    public String toString() {
        return getNamespace() + ":" + getKey();
    }

}
