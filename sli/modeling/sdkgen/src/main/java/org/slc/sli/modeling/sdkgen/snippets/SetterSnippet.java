package org.slc.sli.modeling.sdkgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.jgen.snippets.Block;
import org.slc.sli.modeling.jgen.snippets.IfThenElse;
import org.slc.sli.modeling.jgen.snippets.VarNameExpr;
import org.slc.sli.modeling.jgen.snippets.NotEqual;
import org.slc.sli.modeling.jgen.snippets.Word;

public final class SetterSnippet implements JavaSnippet {

    /**
     * The type of the property.
     */
    private final JavaType type;
    /**
     * The name of the property in the map.
     */
    private final String name;

    public SetterSnippet(final JavaType type, final String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {

        final JavaParam param = new JavaParam(name, type, true);
        final VarNameExpr value = new VarNameExpr(name);

        jsw.write("public");
        jsw.space();
        jsw.writeType(JavaType.JT_VOID);
        jsw.space();
        jsw.write("set");
        jsw.write(titleCase(name));
        jsw.parenL();
        jsw.writeParams(param);
        jsw.parenR();
        final JavaSnippet testSnippet = new NotEqual(value, Word.NULL);
        final JavaSnippet thenSnippet = new PutMapEntry(name, new CoerceToJsonTypeSnippet(name, type));
        final JavaSnippet elseSnippet = new RemoveMapEntry(name);
        new Block(new IfThenElse(testSnippet, thenSnippet, elseSnippet)).write(jsw);
    }

    private static final String titleCase(final String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
    }
}
