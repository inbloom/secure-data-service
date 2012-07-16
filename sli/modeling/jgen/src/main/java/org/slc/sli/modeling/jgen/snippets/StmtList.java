package org.slc.sli.modeling.jgen.snippets;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author jstokes
 */
public class StmtList implements JavaSnippet {

    private final List<JavaSnippet> stmts;

    public StmtList(final JavaSnippet... stmts) {
        if (stmts == null) throw new NullPointerException("stmts");
        this.stmts = Arrays.asList(stmts);
    }
    @Override
    public void write(JavaStreamWriter jsw) throws IOException {
        jsw.beginStmt();
        for (JavaSnippet stmt : stmts) {
            jsw.write(stmt);
        }
        jsw.endStmt();
    }
}
