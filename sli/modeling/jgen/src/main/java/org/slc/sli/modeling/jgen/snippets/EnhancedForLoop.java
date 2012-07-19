package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaSnippetExpr;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class EnhancedForLoop implements JavaSnippet {

    private final JavaParam param;
    private final JavaSnippetExpr rhs;
    private final JavaSnippet body;

    public EnhancedForLoop(final JavaParam param, final JavaSnippetExpr rhs, final JavaSnippet body) {
        if (param == null) {
            throw new NullPointerException("param");
        }
        if (rhs == null) {
            throw new NullPointerException("rhs");
        }
        if (body == null) {
            throw new NullPointerException("body");
        }
        this.param = param;
        this.rhs = rhs;
        this.body = body;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        if (jsw == null) {
            throw new NullPointerException("jsw");
        }
        jsw.write("for");
        jsw.space();
        jsw.parenL();
        try {
            jsw.write("final").space().writeType(param.getType()).space().write(param.getName()).space().write(":")
                    .space().write(rhs);
        } finally {
            jsw.parenR();
        }
        jsw.beginBlock();
        try {
            jsw.write(body);
        } finally {
            jsw.endBlock();
        }
    }
}
