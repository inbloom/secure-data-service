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

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaSnippetExpr;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;

/**
 * Model Java new operator.
 */
public final class NewInstanceExpr implements JavaSnippetExpr {

    private final JavaType type;
    private final JavaSnippetExpr[] args;

    public NewInstanceExpr(final JavaType type, final JavaSnippetExpr... args) {
        if (type == null) {
            throw new IllegalArgumentException("type");
        }
        if (args == null) {
            throw new IllegalArgumentException("args");
        }
        this.type = type;
        this.args = args;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        if (jsw == null) {
            throw new IllegalArgumentException("jsw");
        }
        jsw.write("new").space().writeType(type).parenL();
        try {
            boolean first = true;
            for (final JavaSnippet arg : args) {
                if (first) {
                    first = false;
                } else {
                    jsw.comma().space();
                }
                jsw.write(arg);
            }
        } finally {
            jsw.parenR();
        }
    }
}
