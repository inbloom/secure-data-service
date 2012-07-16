package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slc.sli.modeling.jgen.JavaSnippetExpr;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class MethodCallExpr implements JavaSnippetExpr {

    private final JavaSnippetExpr callee;
    private final String methodName;
    private final List<JavaSnippetExpr> args;

    public MethodCallExpr(final JavaSnippetExpr callee, final String methodName, final JavaSnippetExpr... args) {
        this(callee, methodName, Arrays.asList(args));
    }

    public MethodCallExpr(final JavaSnippetExpr callee, final String methodName, final List<JavaSnippetExpr> args) {
        if (callee == null) {
            throw new NullPointerException("callee");
        }
        if (methodName == null) {
            throw new NullPointerException("methodName");
        }
        this.callee = callee;
        this.methodName = methodName;
        this.args = Collections.unmodifiableList(new ArrayList<JavaSnippetExpr>(args));
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        if (jsw == null) {
            throw new NullPointerException("jsw");
        }
        jsw.write(callee).write(".").write(methodName);
        jsw.parenL();
        try {
            boolean first = true;
            for (final JavaSnippetExpr arg : args) {
                if (first) {
                    first = false;
                } else {
                    jsw.comma();
                }
                jsw.write(arg);
            }
        } finally {
            jsw.parenR();
        }
    }
}
