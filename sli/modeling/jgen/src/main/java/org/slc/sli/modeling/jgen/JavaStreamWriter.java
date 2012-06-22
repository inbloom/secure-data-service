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


package org.slc.sli.modeling.jgen;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.List;

public interface JavaStreamWriter extends Flushable, Closeable {

    JavaStreamWriter beginBlock() throws IOException;

    void beginClass(String name, String extendsClass) throws IOException;

    void beginEnum(String name) throws IOException;

    JavaStreamWriter beginStmt() throws IOException;

    JavaStreamWriter dblQte() throws IOException;

    JavaStreamWriter elementName(String name) throws IOException;

    void endBlock() throws IOException;

    void endClass() throws IOException;

    void endEnum() throws IOException;

    void endStmt() throws IOException;

    JavaStreamWriter write(String text) throws IOException;

    void writeAccessor(String name, String typeName) throws IOException;

    void writeAttribute(String name, String typeName) throws IOException;

    void writeComma() throws IOException;

    void writeComment(String comment) throws IOException;

    void writeEnumLiteral(String name, String text) throws IOException;

    void writeImport(String name) throws IOException;

    void writeInitializer(String name, List<JavaFeature> features) throws IOException;

    void writePackage(String name) throws IOException;
}
