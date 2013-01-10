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

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;

/**
 * Returns a new class
 */
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
