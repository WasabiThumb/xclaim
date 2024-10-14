package codes.wasabi.xclaim.util.io;

import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CloseListenerInputStream extends FilterInputStream {

    private final Closeable attached;
    public CloseListenerInputStream(InputStream in, Closeable attached) {
        super(in);
        this.attached = attached;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            this.attached.close();
        }
    }

}
