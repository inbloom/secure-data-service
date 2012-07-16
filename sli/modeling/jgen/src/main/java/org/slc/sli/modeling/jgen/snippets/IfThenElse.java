package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class IfThenElse implements JavaSnippet {

    private final JavaSnippet testSnippet;
    private final JavaSnippet thenSnippet;
    private final JavaSnippet elseSnippet;

    public IfThenElse(final JavaSnippet testSnippet, final JavaSnippet thenSnippet, final JavaSnippet elseSnippet) {
        if (testSnippet == null) {
            throw new NullPointerException("testSnippet");
        }
        if (thenSnippet == null) {
            throw new NullPointerException("thenSnippet");
        }
        if (elseSnippet == null) {
            throw new NullPointerException("elseSnippet");
        }
        this.testSnippet = testSnippet;
        this.thenSnippet = thenSnippet;
        this.elseSnippet = elseSnippet;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.write("if").space().parenL().write(testSnippet).parenR();
        jsw.beginBlock();
        try {
            thenSnippet.write(jsw);
        } finally {
            jsw.endBlock();
        }
        jsw.write("else");
        jsw.beginBlock();
        try {
            elseSnippet.write(jsw);
        } finally {
            jsw.endBlock();
        }

    }
}
