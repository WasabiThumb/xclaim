package codes.wasabi.xclaim.api.dynmap.exception;

public class DynmapNotFoundException extends DynmapException {

    public DynmapNotFoundException() {
        super();
    }

    public DynmapNotFoundException(String s) {
        super(s);
    }

    public DynmapNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DynmapNotFoundException(Throwable cause) {
        super(cause);
    }

}
