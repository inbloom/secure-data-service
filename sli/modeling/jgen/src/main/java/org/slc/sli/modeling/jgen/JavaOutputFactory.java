package org.slc.sli.modeling.jgen;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public final class JavaOutputFactory {

    private JavaOutputFactory() {

    }

    private static final JavaOutputFactory INSTANCE = new JavaOutputFactory();

    public static JavaOutputFactory newInstance() {
        return INSTANCE;
    }

    public JavaStreamWriter createJavaStreamWriter(final OutputStream stream, final String encoding,
            final JavaGenConfig config) throws UnsupportedEncodingException {
        return new StandardJavaStreamWriter(stream, encoding, config);
    }
}
