/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slc.sli.modeling.jgen.JavaSnippetExpr;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

/**
 * Model Java method call.
 */
public final class MethodCallExpr implements JavaSnippetExpr {

    private final JavaSnippetExpr callee;
    private final String methodName;
    private final List<JavaSnippetExpr> args;

    public MethodCallExpr(final JavaSnippetExpr callee, final String methodName, final JavaSnippetExpr... args) {
        this(callee, methodName, Arrays.asList(args));
    }

    public MethodCallExpr(final JavaSnippetExpr callee, final String methodName, final List<JavaSnippetExpr> args) {
        if (callee == null) {
            throw new IllegalArgumentException("callee");
        }
        if (methodName == null) {
            throw new IllegalArgumentException("methodName");
        }
        this.callee = callee;
        this.methodName = methodName;
        this.args = Collections.unmodifiableList(new ArrayList<JavaSnippetExpr>(args));
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        if (jsw == null) {
            throw new IllegalArgumentException("jsw");
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
