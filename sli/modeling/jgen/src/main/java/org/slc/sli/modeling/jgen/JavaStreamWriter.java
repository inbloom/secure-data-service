package org.slc.sli.modeling.jgen;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.List;

public interface JavaStreamWriter extends Flushable, Closeable {

    JavaStreamWriter beginBlock() throws IOException;

    void beginClass(String name) throws IOException;

    void beginClass(String name, List<String> implementations) throws IOException;

    void beginClass(String name, String extension) throws IOException;

    void beginEnum(String name) throws IOException;

    void beginInterface(String name) throws IOException;

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

    void writeOverride() throws IOException;

    void writePackage(String name) throws IOException;
}
