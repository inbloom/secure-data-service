package org.slc.sli.modeling.sdkgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;

public final class ReturnNewClassTypeSnippet implements JavaSnippet {

    private final JavaType classType;
    private final JavaParam param;

    public ReturnNewClassTypeSnippet(final JavaType classType, final JavaParam param) {
        this.classType = classType;
        this.param = param;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.beginStmt();
        jsw.write("return");
        jsw.space();
        jsw.write("new");
        jsw.space();
        jsw.writeType(classType);
        jsw.parenL();
        jsw.write(param.getName());
        jsw.parenR();
        jsw.endStmt();
    }
}
