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

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author jstokes
 */
public class MockJavaStreamWriter implements JavaStreamWriter {
    private JavaStreamWriter jsw;
    private ByteArrayOutputStream out;


    public MockJavaStreamWriter() throws UnsupportedEncodingException {
        this.out = new ByteArrayOutputStream();
        this.jsw = new StandardJavaStreamWriter(out, "UTF-8", new JavaGenConfig(true));
    }

    @Override
    public JavaStreamWriter beginBlock() throws IOException {
        return jsw.beginBlock();
    }

    @Override
    public void beginCatch(JavaType type, String variableName) throws IOException {
        jsw.beginCatch(type, variableName);
    }

    @Override
    public void beginClass(String name) throws IOException {
        jsw.beginClass(name);
    }

    @Override
    public void beginClass(String name, List<String> implementations) throws IOException {
        jsw.beginClass(name, implementations);
    }

    @Override
    public void beginClass(String name, List<String> implementations, String extension) throws IOException {
        jsw.beginClass(name, implementations, extension);
    }

    @Override
    public void beginClass(String name, String extension) throws IOException {
        jsw.beginClass(name, extension);
    }

    @Override
    public void beginEnum(String name) throws IOException {
        jsw.beginEnum(name);
    }

    @Override
    public void beginEnum(String name, List<String> implementations) throws IOException {
        jsw.beginEnum(name, implementations);
    }

    @Override
    public void beginInterface(String name) throws IOException {
        jsw.beginInterface(name);
    }

    @Override
    public JavaStreamWriter beginStmt() throws IOException {
        return jsw.beginStmt();
    }

    @Override
    public JavaStreamWriter castAs(JavaType type) throws IOException {
        return jsw.castAs(type);
    }

    @Override
    public JavaStreamWriter comma() throws IOException {
        return jsw.comma();
    }

    @Override
    public JavaStreamWriter dblQte() throws IOException {
        return jsw.dblQte();
    }

    @Override
    public JavaStreamWriter elementName(String name) throws IOException {
        return jsw.elementName(name);
    }

    @Override
    public void endBlock() throws IOException {
        jsw.endBlock();
    }

    @Override
    public void endCatch() throws IOException {
        jsw.endCatch();
    }

    @Override
    public void endClass() throws IOException {
        jsw.endClass();
    }

    @Override
    public void endEnum() throws IOException {
        jsw.endEnum();
    }

    @Override
    public void endStmt() throws IOException {
        jsw.endStmt();
    }

    @Override
    public JavaStreamWriter parenL() throws IOException {
        return jsw.parenL();
    }

    @Override
    public JavaStreamWriter parenR() throws IOException {
        return jsw.parenR();
    }

    @Override
    public JavaStreamWriter space() throws IOException {
        return jsw.space();
    }

    @Override
    public JavaStreamWriter write(JavaSnippet snippet) throws IOException {
        return jsw.write(snippet);
    }

    @Override
    public JavaStreamWriter write(String text) throws IOException {
        return jsw.write(text);
    }

    @Override
    public void writeAccessor(String name, String typeName) throws IOException {
        jsw.writeAccessor(name, typeName);
    }

    @Override
    public void writeArgs(List<String> args) throws IOException {
        jsw.writeArgs(args);
    }

    @Override
    public void writeArgs(String... args) throws IOException {
        jsw.writeArgs(args);
    }

    @Override
    public void writeAssignment(JavaParam lhs, JavaSnippetExpr rhs) throws IOException {
        jsw.writeAssignment(lhs, rhs);
    }

    @Override
    public void writeAttribute(JavaParam param) throws IOException {
        jsw.writeAttribute(param);
    }

    @Override
    public void writeAttribute(String name, String typeName) throws IOException {
        jsw.writeAttribute(name, typeName);
    }

    @Override
    public void writeComment(String comment) throws IOException {
        jsw.writeComment(comment);
    }

    @Override
    public void writeEnumLiteral(String name, String text) throws IOException {
        jsw.writeEnumLiteral(name, text);
    }

    @Override
    public void writeImport(String name) throws IOException {
        jsw.writeImport(name);
    }

    @Override
    public void writeInitializer(String name, List<JavaFeature> features) throws IOException {
        jsw.writeInitializer(name, features);
    }

    @Override
    public void writeOverride() throws IOException {
        jsw.writeOverride();
    }

    @Override
    public void writePackage(String name) throws IOException {
        jsw.writePackage(name);
    }

    @Override
    public void writeParams(JavaParam... params) throws IOException {
        jsw.writeParams(params);
    }

    @Override
    public void writeParams(List<JavaParam> params) throws IOException {
        jsw.writeParams(params);
    }

    @Override
    public void writeThrows(JavaType... exceptions) throws IOException {
        jsw.writeThrows(exceptions);
    }

    @Override
    public JavaStreamWriter writeType(JavaType type) throws IOException {
        return jsw.writeType(type);
    }

    @Override
    public void flush() throws IOException {
        jsw.flush();
    }

    @Override
    public void close() throws IOException {
        jsw.close();
    }

    public String read() throws IOException {
        jsw.flush();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        return IOUtils.toString(in);
    }
}
