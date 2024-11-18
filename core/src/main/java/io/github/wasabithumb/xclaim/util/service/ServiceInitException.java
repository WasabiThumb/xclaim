package io.github.wasabithumb.xclaim.util.service;

public class ServiceInitException extends IllegalStateException {

    public ServiceInitException() {
        super();
    }

    public ServiceInitException(String s) {
        super(s);
    }

    public ServiceInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceInitException(Throwable cause) {
        super(cause);
    }

}
