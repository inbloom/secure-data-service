package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaSnippetExpr;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;

public final class NewInstanceExpr implements JavaSnippetExpr {

    private final JavaType type;
    private final JavaSnippetExpr[] args;

    public NewInstanceExpr(final JavaType type, final JavaSnippetExpr... args) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (args == null) {
            throw new NullPointerException("args");
        }
        this.type = type;
        this.args = args;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        if (jsw == null) {
            throw new NullPointerException("jsw");
        }
        jsw.write("new").space().writeType(type).parenL();
        try {
            boolean first = true;
            for (final JavaSnippet arg : args) {
                if (first) {
                    first = false;
                } else {
                    jsw.comma().space();
                }
                jsw.write(arg);
            }
        } finally {
            jsw.parenR();
        }
    }
}
