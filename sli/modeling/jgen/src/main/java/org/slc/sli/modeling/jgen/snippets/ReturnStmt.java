package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class ReturnStmt implements JavaSnippet {

    private final JavaSnippet what;

    public ReturnStmt(final JavaSnippet what) {
        if (what == null) {
            throw new NullPointerException("what");
        }
        this.what = what;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.beginStmt().write("return").space().write(what).endStmt();
    }
}
