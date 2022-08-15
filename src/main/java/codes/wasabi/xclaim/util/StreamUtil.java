package codes.wasabi.xclaim.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class StreamUtil {

    public static byte[] readAllBytes(InputStream is, int bufferSize) throws IOException {
        byte[] ret = new byte[0];
        byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = is.read(buffer)) > 0) {
            int curLength = ret.length;
            byte[] concat = new byte[curLength + read];
            System.arraycopy(ret, 0, concat, 0, curLength);
            System.arraycopy(buffer, 0, concat, curLength, read);
            ret = concat;
        }
        return ret;
    }

    public static byte[] readAllBytes(InputStream is) throws IOException {
        return readAllBytes(is, 8192);
    }

    public static byte[] readNBytes(InputStream is, int count) throws IOException {
        byte[] ret = new byte[count];
        int off = 0;
        int len = count;
        while (len > 0) {
            int r = is.read(ret, off, len);
            if (r < 0) throw new IOException("Unexpected end of stream");
            off += r;
            len -= r;
        }
        return ret;
    }

    public static void writeBytes(ByteArrayOutputStream bos, byte[] bytes) {
        try {
            bos.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
