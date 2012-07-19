package org.slc.sli.modeling.sdkgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class PutMapEntry implements JavaSnippet {

    private final String name;
    private final JavaSnippet value;

    public PutMapEntry(final String name, final JavaSnippet value) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.name = name;
        this.value = value;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.beginStmt();
        jsw.write("data.put").parenL().dblQte().write(name).dblQte().comma().write(value).parenR();
        jsw.endStmt();
    }

}
