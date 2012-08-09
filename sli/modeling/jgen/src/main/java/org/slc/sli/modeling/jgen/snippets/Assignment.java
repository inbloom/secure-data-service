package org.slc.sli.modeling.jgen.snippets;

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaSnippetExpr;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

import java.io.IOException;

/**
 * @author jstokes
 */
public final class Assignment implements JavaSnippet {

    private final JavaParam lhs;
    private final JavaSnippetExpr rhs;

    public Assignment(final JavaParam lhs, final JavaSnippetExpr rhs) {
        if (lhs == null) throw new NullPointerException("lhs");
        if (rhs == null) throw new NullPointerException("rhs");

        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.writeAssignment(this.lhs, this.rhs);
    }
}
