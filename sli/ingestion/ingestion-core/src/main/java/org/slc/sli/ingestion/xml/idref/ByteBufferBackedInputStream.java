package org.slc.sli.ingestion.xml.idref;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author okrook
 *
 */
public class ByteBufferBackedInputStream extends InputStream {

    ByteBuffer buf;

    public ByteBufferBackedInputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    @Override
    public synchronized int read() throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }

        return buf.get() & 0xFF;
    }

    @Override
    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, buf.remaining());
        buf.get(bytes, off, len);
        return len;
    }
}
