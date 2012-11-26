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

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

/**
 * Add an entry to a map
 */
public final class PutMapEntry implements JavaSnippet {

    private final String name;
    private final JavaSnippet value;

    public PutMapEntry(final String name, final JavaSnippet value) {
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        if (value == null) {
            throw new IllegalArgumentException("value");
        }
        this.name = name;
        this.value = value;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.beginStmt();
        jsw.write("data.put").parenL().dblQte().write(name).dblQte().comma().write(value).parenR();
        jsw.endStmt();
    }

}
