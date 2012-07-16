package org.slc.sli.modeling.jgen;

import java.io.IOException;

/**
 * Provides support for composition of snippets of code.
 */
public interface JavaSnippet {

    void write(final JavaStreamWriter jsw) throws IOException;

}
