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
import org.slc.sli.modeling.jgen.JavaTypeKind;
import org.slc.sli.modeling.jgen.snippets.Assignment;
import org.slc.sli.modeling.jgen.snippets.Block;
import org.slc.sli.modeling.jgen.snippets.EnhancedForLoop;
import org.slc.sli.modeling.jgen.snippets.IfThenElse;
import org.slc.sli.modeling.jgen.snippets.MethodCallExpr;
import org.slc.sli.modeling.jgen.snippets.NewInstanceExpr;
import org.slc.sli.modeling.jgen.snippets.Stmt;
import org.slc.sli.modeling.jgen.snippets.StmtList;
import org.slc.sli.modeling.jgen.snippets.VarNameExpr;
import org.slc.sli.modeling.jgen.snippets.NotEqual;
import org.slc.sli.modeling.jgen.snippets.Word;

/**
 * Model a Java setter
 */
public final class SetterSnippet implements JavaSnippet {

    /**
     * The type of the property.
     */
    private final JavaType type;
    /**
     * The name of the property in the map.
     */
    private final String name;

    public SetterSnippet(final JavaType type, final String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {

        final JavaParam param = new JavaParam(name, type, true);
        final VarNameExpr value = new VarNameExpr(name);

        jsw.write("public");
        jsw.space();
        jsw.writeType(JavaType.JT_VOID);
        jsw.space();
        jsw.write("set");
        jsw.write(titleCase(name));
        jsw.parenL();
        jsw.writeParams(param);
        jsw.parenR();
        final JavaSnippet testSnippet = new NotEqual(value, Word.NULL);
        final JavaSnippet thenSnippet;
        final JavaSnippet elseSnippet = new RemoveMapEntry(name);
        if (type.getTypeKind().equals(JavaTypeKind.ENUM) && type.getCollectionKind().equals(JavaCollectionKind.LIST)) {
            final String enumNames = "enumNames";
            final JavaSnippet assnmt = new Assignment(
                    new JavaParam(enumNames, JavaType.collectionType(JavaCollectionKind.LIST, JavaType.JT_STRING), true),
                    new NewInstanceExpr(JavaType.collectionType(JavaCollectionKind.ARRAY_LIST, JavaType.JT_STRING)));
            thenSnippet = new StmtList(
                    new EnhancedForLoop(
                        new JavaParam("item", type.primeType(), true),
                        new VarNameExpr(param.getName()),
                        new Stmt(new MethodCallExpr(new VarNameExpr(enumNames), "add",
                                new MethodCallExpr(new VarNameExpr("item"), "getName")))),
                    new PutMapEntry(name, new VarNameExpr(enumNames)));
            new Block(assnmt, new IfThenElse(testSnippet, thenSnippet, elseSnippet)).write(jsw);
        } else {
            thenSnippet = new PutMapEntry(name, new CoerceToJsonTypeSnippet(name, type));
            new Block(new IfThenElse(testSnippet, thenSnippet, elseSnippet)).write(jsw);
        }
    }

    private static final String titleCase(final String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
    }
}
