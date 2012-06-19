package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public class JavaIdentifier implements JavaSnippet {

    private final String name;

    public JavaIdentifier(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.write(name);
    }
}
