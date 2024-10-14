package codes.wasabi.xclaim.util.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TeeInputStream extends InputStream {

    protected final InputStream in;
    protected final OutputStream out;
    public TeeInputStream(InputStream in, OutputStream os) {
        this.in = in;
        this.out = os;
    }

    @Override
    public int read() throws IOException {
        int r = this.in.read();
        if (r == -1) {
            this.out.flush();
        } else {
            this.out.write(r);
        }
        return r;
    }

    @Override
    public int read(byte @NotNull [] b, int off, int len) throws IOException {
        int count = this.in.read(b, off, len);
        if (count == -1) {
            this.out.flush();
        } else {
            this.out.write(b, off, count);
        }
        return count;
    }

    @Override
    public int available() throws IOException {
        return this.in.available();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void close() throws IOException {
        try {
            this.out.close();
        } finally {
            this.in.close();
        }
    }

}
