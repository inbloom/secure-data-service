package org.slc.sli.ingestion.xml.idref;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author okrook
 *
 */
public class NonClosingOutputStream extends OutputStream {

    private OutputStream out;

    public NonClosingOutputStream(OutputStream out) {
        this.out = out;
    }

    /**
     * Does nothing.
     */
    @Override
    public void close() throws IOException {
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

}
