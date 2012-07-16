package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class Block implements JavaSnippet {

    private final JavaSnippet stmts;

    public Block(final JavaSnippet stmts) {
        if (stmts == null) {
            throw new NullPointerException("stmts");
        }
        this.stmts = stmts;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.beginBlock().write(stmts).endBlock();
    }

}
