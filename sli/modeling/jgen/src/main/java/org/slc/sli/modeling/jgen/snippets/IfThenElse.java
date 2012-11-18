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
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public final class IfThenElse implements JavaSnippet {

    private final JavaSnippet testSnippet;
    private final JavaSnippet thenSnippet;
    private final JavaSnippet elseSnippet;

    public IfThenElse(final JavaSnippet testSnippet, final JavaSnippet thenSnippet, final JavaSnippet elseSnippet) {
        if (testSnippet == null) {
            throw new IllegalArgumentException("testSnippet");
        }
        if (thenSnippet == null) {
            throw new IllegalArgumentException("thenSnippet");
        }
        if (elseSnippet == null) {
            throw new IllegalArgumentException("elseSnippet");
        }
        this.testSnippet = testSnippet;
        this.thenSnippet = thenSnippet;
        this.elseSnippet = elseSnippet;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.write("if").space().parenL().write(testSnippet).parenR();
        jsw.beginBlock();
        try {
            thenSnippet.write(jsw);
        } finally {
            jsw.endBlock();
        }
        jsw.write("else");
        jsw.beginBlock();
        try {
            elseSnippet.write(jsw);
        } finally {
            jsw.endBlock();
        }

    }
}
