package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class Block implements JavaSnippet {

    private final List<JavaSnippet> stmts;

    public Block(final JavaSnippet... stmts) {
        if (stmts == null) {
            throw new NullPointerException("stmts");
        }
        this.stmts = Arrays.asList(stmts);
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.beginBlock();
        for (JavaSnippet stmt : stmts) {
            jsw.write(stmt);
        }
        jsw.endBlock();
    }

}
