package org.slc.sli.modeling.sdkgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippetExpr;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.snippets.VarNameExpr;

public final class ListToMapExpr implements JavaSnippetExpr {

    private final VarNameExpr id;

    public ListToMapExpr(final VarNameExpr id) {
        if (id == null) {
            throw new NullPointerException("id");
        }
        this.id = id;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        if (jsw == null) {
            throw new NullPointerException("jsw");
        }
        jsw.write("CoerceToJson");
        jsw.write(id);
        jsw.write(".");
        jsw.write("toListOfMap");
        jsw.parenL();
        jsw.parenR();
    }
}
