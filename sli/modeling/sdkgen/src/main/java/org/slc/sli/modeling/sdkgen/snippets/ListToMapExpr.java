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

package org.slc.sli.modeling.sdkgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippetExpr;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.snippets.VarNameExpr;

/**
 * Convert to Map.
 */
public final class ListToMapExpr implements JavaSnippetExpr {

    private final VarNameExpr id;

    public ListToMapExpr(final VarNameExpr id) {
        if (id == null) {
            throw new IllegalArgumentException("id");
        }
        this.id = id;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        if (jsw == null) {
            throw new IllegalArgumentException("jsw");
        }
        jsw.write("CoerceToJson");
        jsw.write(id);
        jsw.write(".");
        jsw.write("toListOfMap");
        jsw.parenL();
        jsw.parenR();
    }
}
