package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class EqualEqual implements JavaSnippet {

    private final JavaSnippet lhsSnippet;
    private final JavaSnippet rhsSnippet;

    public EqualEqual(final JavaSnippet lhsSnippet, final JavaSnippet rhsSnippet) {
        if (lhsSnippet == null) {
            throw new NullPointerException("lhsSnippet");
        }
        if (rhsSnippet == null) {
            throw new NullPointerException("rhsSnippet");
        }
        this.lhsSnippet = lhsSnippet;
        this.rhsSnippet = rhsSnippet;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.write(lhsSnippet).write(" == ").write(rhsSnippet);
    }
}
