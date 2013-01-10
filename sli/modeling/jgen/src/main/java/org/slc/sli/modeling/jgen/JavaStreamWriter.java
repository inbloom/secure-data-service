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

/**
 * Model Java stream writer.
 */
public interface JavaStreamWriter extends Flushable, Closeable {

    JavaStreamWriter beginBlock() throws IOException;

    void beginCatch(JavaType type, String variableName) throws IOException;

    void beginClass(String name) throws IOException;

    void beginClass(String name, List<String> implementations) throws IOException;

    void beginClass(String name, List<String> implementations, String extension) throws IOException;

    void beginClass(String name, String extension) throws IOException;

    void beginEnum(String name) throws IOException;

    void beginEnum(String name, List<String> implementations) throws IOException;

    void beginInterface(String name) throws IOException;

    JavaStreamWriter beginStmt() throws IOException;

    JavaStreamWriter castAs(JavaType type) throws IOException;

    JavaStreamWriter comma() throws IOException;

    JavaStreamWriter dblQte() throws IOException;

    JavaStreamWriter elementName(String name) throws IOException;

    void endBlock() throws IOException;

    void endCatch() throws IOException;

    void endClass() throws IOException;

    void endEnum() throws IOException;

    void endStmt() throws IOException;

    JavaStreamWriter parenL() throws IOException;

    JavaStreamWriter parenR() throws IOException;

    JavaStreamWriter space() throws IOException;

    JavaStreamWriter write(JavaSnippet snippet) throws IOException;

    JavaStreamWriter write(String text) throws IOException;

    void writeAccessor(String name, String typeName) throws IOException;

    void writeArgs(List<String> args) throws IOException;

    void writeArgs(String... args) throws IOException;

    void writeAssignment(JavaParam lhs, JavaSnippetExpr rhs) throws IOException;

    void writeAttribute(JavaParam param) throws IOException;

    void writeAttribute(String name, String typeName) throws IOException;

    void writeComment(String comment) throws IOException;

    void writeEnumLiteral(String name, String text) throws IOException;

    void writeImport(String name) throws IOException;

    void writeInitializer(String name, List<JavaFeature> features) throws IOException;

    void writeOverride() throws IOException;

    void writePackage(String name) throws IOException;

    void writeParams(JavaParam... params) throws IOException;

    void writeParams(List<JavaParam> params) throws IOException;

    void writeThrows(JavaType... exceptions) throws IOException;

    JavaStreamWriter writeType(JavaType type) throws IOException;
}
