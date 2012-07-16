package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippetExpr;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class StringLiteralExpr implements JavaSnippetExpr {

    private final String value;

    public StringLiteralExpr(final String value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value = value;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        if (jsw == null) {
            throw new NullPointerException("jsw");
        }
        jsw.dblQte();
        try {
            // FIXME: This should be escaped.
            jsw.write(value);
        } finally {
            jsw.dblQte();
        }
    }

}
