package org.slc.sli.modeling.sdkgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class RemoveMapEntry implements JavaSnippet {

    private final String name;

    public RemoveMapEntry(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.beginStmt();
        jsw.write("data.remove").parenL().dblQte().write(name).dblQte().parenR();
        jsw.endStmt();
    }
}
