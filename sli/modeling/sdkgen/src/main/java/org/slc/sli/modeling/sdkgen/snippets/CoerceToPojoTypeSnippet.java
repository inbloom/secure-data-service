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

import org.slc.sli.modeling.jgen.JavaCollectionKind;
import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;

/**
 * Convert JSON to JavaType
 */
public final class CoerceToPojoTypeSnippet implements JavaSnippet {

    private static final JavaType MAP_STRING_TO_OBJECT = JavaType.mapType(JavaType.JT_STRING, JavaType.JT_OBJECT);

    private final JavaParam field;
    private final String name;
    private final JavaType type;

    public CoerceToPojoTypeSnippet(final JavaParam field, final String name, final JavaType type) {
        this.field = field;
        this.name = name;
        this.type = type;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.write("Coercions.");
        if (JavaType.JT_BIG_INTEGER.equals(type)) {
            jsw.write("toBigInteger");
        } else if (JavaType.JT_BOOLEAN.equals(type)) {
            jsw.write("toBoolean");
        } else if (JavaType.JT_DOUBLE.equals(type)) {
            jsw.write("toDouble");
        } else if (JavaType.JT_STRING.equals(type)) {
            jsw.write("toString");
        } else if (JavaType.JT_INTEGER.equals(type)) {
            jsw.write("toInteger");
        } else if (MAP_STRING_TO_OBJECT.equals(type)) {
            jsw.write("toMap");
        } else if (isList(type)) {
            jsw.write("toList");
        } else {
            throw new AssertionError(type);
        }
        jsw.parenL();
        try {
            jsw.write(field.getName()).write(".get");
            jsw.parenL().dblQte().write(name).dblQte().parenR();
        } finally {
            jsw.parenR();
        }
    }

    private boolean isList(JavaType type) {
        return type.getCollectionKind().equals(JavaCollectionKind.LIST)
                || type.getCollectionKind().equals(JavaCollectionKind.ARRAY_LIST);
    }
}
