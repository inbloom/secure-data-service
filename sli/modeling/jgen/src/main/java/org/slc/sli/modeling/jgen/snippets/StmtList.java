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
        if (stmts == null) {
            throw new IllegalArgumentException("stmts");
        }
        this.stmts = Arrays.asList(stmts);
    }
    @Override
    public void write(JavaStreamWriter jsw) throws IOException {
        for (JavaSnippet stmt : stmts) {
            jsw.write(stmt);
        }
    }
}
