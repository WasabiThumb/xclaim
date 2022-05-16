package codes.wasabi.xclaim.api.dynmap.exception;

public class DynmapException extends IllegalStateException {

    public DynmapException() {
        super();
    }

    public DynmapException(String s) {
        super(s);
    }

    public DynmapException(String message, Throwable cause) {
        super(message, cause);
    }

    public DynmapException(Throwable cause) {
        super(cause);
    }

}
