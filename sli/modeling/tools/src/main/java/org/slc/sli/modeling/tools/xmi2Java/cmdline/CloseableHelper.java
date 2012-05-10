package org.slc.sli.modeling.tools.xmi2Java.cmdline;

import java.io.Closeable;
import java.io.IOException;

public final class CloseableHelper {

    public static final void closeQuiet(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
