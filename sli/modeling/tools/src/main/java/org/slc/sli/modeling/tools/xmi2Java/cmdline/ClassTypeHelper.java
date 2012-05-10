package org.slc.sli.modeling.tools.xmi2Java.cmdline;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.Mapper;

public class ClassTypeHelper {

    public static final void writeClassType(final String packageName, final List<String> importNames,
            final ClassType classType, final Mapper model, final File file) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeClassType(packageName, importNames, classType, model, outstream);
            } finally {
                CloseableHelper.closeQuiet(outstream);
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final void writeClassType(final String packageName, final List<String> importNames,
            final ClassType classType, final Mapper model, final OutputStream outstream) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8");
            try {
                jsw.writePackage(packageName);
                for (final String importName : importNames) {
                    jsw.writeImport(importName);
                }
                JavadocHelper.writeJavadoc(classType, model, jsw);
                jsw.beginClass(classType.getName(), null);
                try {
                    for (final Attribute attribute : classType.getAttributes()) {
                        final String name = attribute.getName();
                        final Type type = model.getType(attribute.getType());
                        jsw.writeAttribute(name, TypeHelper.getImplementationTypeName(type.getName()));
                    }
                    jsw.writeInitializer(classType.getName(), getFeatures(classType, model));
                    for (final Attribute attribute : classType.getAttributes()) {
                        final String name = attribute.getName();
                        final Type type = model.getType(attribute.getType());
                        JavadocHelper.writeJavadoc(attribute, model, jsw);
                        jsw.writeAccessor(name, TypeHelper.getImplementationTypeName(type.getName()));
                    }
                } finally {
                    jsw.endClass();
                }
            } finally {
                jsw.flush();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final List<JavaFeature> getFeatures(final ClassType classType, final Mapper model) {
        final List<JavaFeature> features = new LinkedList<JavaFeature>();
        for (final Attribute attribute : classType.getAttributes()) {
            final String name = attribute.getName();
            final Type type = model.getType(attribute.getType());
            features.add(new JavaFeature(name, TypeHelper.getImplementationTypeName(type.getName())));
        }
        return Collections.unmodifiableList(features);
    }

}
