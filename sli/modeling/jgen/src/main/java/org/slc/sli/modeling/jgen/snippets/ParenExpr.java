package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class ParenExpr implements JavaSnippet {

    private final JavaSnippet expr;

    public ParenExpr(final JavaSnippet expr) {
        if (expr == null) {
            throw new NullPointerException("expr");
        }
        this.expr = expr;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.parenL().write(expr).parenR();
    }
}
