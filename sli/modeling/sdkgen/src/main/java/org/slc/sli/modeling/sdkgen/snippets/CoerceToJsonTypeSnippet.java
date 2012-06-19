package org.slc.sli.modeling.sdkgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;

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
        if (type.isComplex()) {
            if (type.isList()) {
                jsw.write("CoerceToJson.toListOfMap").parenL().write(name).parenR();
            } else {
                jsw.write(name).write(".toMap").parenL().parenR();
            }
        } else if (type.isEnum()) {
            if (type.isList()) {
                jsw.write("null");
            } else {
                jsw.write(name).write(".getName").parenL().parenR();
            }
        } else {
            if (type.isList()) {
                if (JavaType.JT_STRING.equals(type.getBase())) {
                    jsw.write(name);
                } else if (JavaType.JT_OBJECT.equals(type.getBase())) {
                    jsw.write(name);
                } else {
                    throw new AssertionError(type.getBase());
                }
            } else {
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
            }
        }
    }
}
