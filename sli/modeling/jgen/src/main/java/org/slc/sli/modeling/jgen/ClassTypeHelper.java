package org.slc.sli.modeling.jgen;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.index.ModelIndex;

public class ClassTypeHelper {

    public static final void writeClassType(final String packageName, final List<String> importNames,
            final ClassType classType, final ModelIndex model, final File file, final JavaGenConfig config) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeClassType(packageName, importNames, classType, model, outstream, config);
            } finally {
                try {
                    outstream.close();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final void writeClassType(final String packageName, final List<String> importNames,
            final ClassType classType, final ModelIndex model, final OutputStream outstream, final JavaGenConfig config) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8", config);
            try {
                jsw.writePackage(packageName);
                for (final String importName : importNames) {
                    jsw.writeImport(importName);
                }
                JavadocHelper.writeJavadoc(classType, model, jsw);
                jsw.beginClass(classType.getName(), null);
                try {
                    // Write the attributes and association ends.
                    for (final JavaFeature feature : getFeatures(classType, model)) {
                        if (feature.isAttribute()) {
                            final String name = feature.getName(config);
                            jsw.writeAttribute(name, feature.getAttributeTypeName(config));
                        } else if (feature.isNavigable()) {
                            final String name = feature.getName(config);
                            jsw.writeAttribute(name, feature.getNavigableTypeName());
                        }
                    }

                    jsw.writeInitializer(classType.getName(), getFeatures(classType, model));

                    for (final JavaFeature feature : getFeatures(classType, model)) {
                        if (feature.isAttribute()) {
                            final String name = feature.getName(config);
                            JavadocHelper.writeJavadoc(feature.getFeature(), model, jsw);
                            jsw.writeAccessor(name, feature.getAttributeTypeName(config));
                        } else if (feature.isNavigable()) {
                            final String name = feature.getName(config);
                            JavadocHelper.writeJavadoc(feature.getFeature(), model, jsw);
                            jsw.writeAccessor(name, feature.getNavigableTypeName());
                        }
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

    public static final List<JavaFeature> getFeatures(final ClassType classType, final ModelIndex model) {
        final List<JavaFeature> features = new LinkedList<JavaFeature>();
        for (final Attribute attribute : classType.getAttributes()) {
            features.add(new JavaFeature(attribute, model));
        }
        for (final AssociationEnd associationEnd : model.getAssociationEnds(classType.getId())) {
            features.add(new JavaFeature(associationEnd, model));
        }
        return Collections.unmodifiableList(features);
    }

}
