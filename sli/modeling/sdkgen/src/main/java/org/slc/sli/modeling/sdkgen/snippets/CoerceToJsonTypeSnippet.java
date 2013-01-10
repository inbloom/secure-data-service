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
import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.jgen.JavaTypeKind;

/**
 * Convert a type to JSON
 */
public final class CoerceToJsonTypeSnippet implements JavaSnippet {

    private static final JavaType MAP_STRING_TO_OBJECT = JavaType.mapType(JavaType.JT_STRING, JavaType.JT_OBJECT);

    private final String name;
    private final JavaType type;

    public CoerceToJsonTypeSnippet(final String name, final JavaType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        final JavaCollectionKind collectionKind = type.getCollectionKind();
        switch (collectionKind) {
        case NONE: {
            final JavaTypeKind typeKind = type.getTypeKind();
            switch (typeKind) {
            case SIMPLE: {
                if (JavaType.JT_BIG_INTEGER.equals(type)) {
                    jsw.write(name).write(".toString").parenL().parenR();
                } else if (JavaType.JT_BOOLEAN.equals(type)) {
                    jsw.write(name);
                } else if (JavaType.JT_DOUBLE.equals(type)) {
                    jsw.write(name);
                } else if (JavaType.JT_STRING.equals(type)) {
                    jsw.write(name);
                } else if (JavaType.JT_INTEGER.equals(type)) {
                    jsw.write(name).write(".toString").parenL().parenR();
                } else if (MAP_STRING_TO_OBJECT.equals(type)) {
                    throw new AssertionError(type);
                } else {
                    jsw.write(name).write(".getValue").parenL().parenR();
                }
                break;
            }
            case ENUM: {
                jsw.write(name).write(".getName").parenL().parenR();
                break;
            }
            case COMPLEX: {
                jsw.write(name).write(".toMap").parenL().parenR();
                break;
            }
            default: {
                throw new AssertionError(typeKind);
            }
            }
            break;
        }
        case LIST: {
            final JavaType primeType = type.primeType();
            final JavaTypeKind typeKind = primeType.getTypeKind();
            switch (typeKind) {
            case SIMPLE: {
                jsw.write(name);
                break;
            }
            case COMPLEX: {
                jsw.write("CoerceToJson.toListOfMap").parenL().write(name).parenR();
                break;
            }
            case ENUM: {
                jsw.write(name);
                break;
            }
            default: {
                throw new AssertionError(type);
            }
            }
            break;
        }
        default: {
            throw new AssertionError(collectionKind);
        }
        }
    }
}
