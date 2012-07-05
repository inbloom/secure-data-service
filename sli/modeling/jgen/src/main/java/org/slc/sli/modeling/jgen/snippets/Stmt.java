package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class Stmt implements JavaSnippet {

    private final JavaSnippet statement;

    public Stmt(final JavaSnippet statement) {
        if (statement == null) {
            throw new NullPointerException("statement");
        }
        this.statement = statement;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        if (jsw == null) {
            throw new NullPointerException("jsw");
        }
        jsw.beginStmt();
        try {
            jsw.write(statement);
        } finally {
            jsw.endStmt();
        }
    }
}
