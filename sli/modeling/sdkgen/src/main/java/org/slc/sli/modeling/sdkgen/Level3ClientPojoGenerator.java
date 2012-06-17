package org.slc.sli.modeling.sdkgen;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slc.sli.modeling.jgen.JavaFeature;
import org.slc.sli.modeling.jgen.JavaGenConfig;
import org.slc.sli.modeling.jgen.JavaGenConfigBuilder;
import org.slc.sli.modeling.jgen.JavaOutputFactory;
import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.jgen.JavaTypeHelper;
import org.slc.sli.modeling.jgen.JavadocHelper;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Feature;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.helpers.NamespaceHelper;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

public final class Level3ClientPojoGenerator {

    private static final JavaType TYPE_UNDERLYING = JavaType.mapType(JavaType.JT_STRING, JavaType.JT_OBJECT);
    private static final JavaParam FIELD_UNDERLYING = new JavaParam("data", TYPE_UNDERLYING, true);

    public static void main(final String[] args) {
        try {
            final JavaGenConfig config = new JavaGenConfigBuilder().build();
            doModel("./../../domain/src/main/resources/sliModel/SLI.xmi",
                    "./../../modeling/shtick/src/main/java/org/slc/sli/shtick/pojo", "org.slc.sli.shtick.pojo", config);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static final void doModel(final ModelIndex model, final File dir, final String targetPkgName,
            final JavaGenConfig config) throws FileNotFoundException {
        for (final ClassType classType : model.getClassTypes()) {
            final String fileName = classType.getName().concat(".java");
            final File file = new File(dir, fileName);
            final List<String> importNames = new ArrayList<String>();
            importNames.add("java.math.*");
            importNames.add("java.util.*");
            importNames.add("org.slc.sli.shtick.Entity");
            writeClassType(targetPkgName, importNames, classType, model, file, config);
        }
        for (final EnumType enumType : model.getEnumTypes()) {
            final String fileName = enumType.getName().concat(".java");
            final File file = new File(dir, fileName);
            writeEnumType(targetPkgName, enumType, model, file, config);
        }
        for (final DataType dataType : model.getDataTypes().values()) {
            final String fileName = dataType.getName().concat(".java");
            final File file = new File(dir, fileName);
            if (!NamespaceHelper.getNamespace(dataType, model).equals("http://www.w3.org/2001/XMLSchema")) {
                writeDataType(targetPkgName, dataType, model, file, config);
            }
        }
    }

    private static final void doModel(final String modelFileName, final String targetDirName,
            final String targetPkgName, final JavaGenConfig config) throws FileNotFoundException {
        final ModelIndex model = new DefaultModelIndex(XmiReader.readModel(modelFileName));
        final File dir = new File(targetDirName);
        for (final ClassType classType : model.getClassTypes()) {
            final String fileName = classType.getName().concat(".java");
            final File file = new File(dir, fileName);
            final List<String> importNames = new ArrayList<String>();
            importNames.add("java.math.*");
            importNames.add("java.util.*");
            writeClassType(targetPkgName, importNames, classType, model, file, config);
        }
        for (final EnumType enumType : model.getEnumTypes()) {
            final String fileName = enumType.getName().concat(".java");
            final File file = new File(dir, fileName);
            writeEnumType(targetPkgName, enumType, model, file, config);
        }
        for (final DataType dataType : model.getDataTypes().values()) {
            final String fileName = dataType.getName().concat(".java");
            final File file = new File(dir, fileName);
            if (!NamespaceHelper.getNamespace(dataType, model).equals("http://www.w3.org/2001/XMLSchema")) {
                writeDataType(targetPkgName, dataType, model, file, config);
            }
        }
    }

    private static final void writeDataType(final String targetPkgName, final DataType dataType,
            final ModelIndex model, final File file, final JavaGenConfig config) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeDataType(targetPkgName, dataType, model, outstream, config);
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

    private static final void writeDataType(final String targetPkgName, final DataType dataType,
            final ModelIndex model, final OutputStream outstream, final JavaGenConfig config) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8", config);
            try {
                jsw.writePackage(targetPkgName);
                JavadocHelper.writeJavadoc(dataType, model, jsw);
                jsw.beginClass(dataType.getName());
                try {
                    final String dataTypeBaseName = JavaTypeHelper.getAttributePrimeTypeName(getDataTypeBase(dataType,
                            model));
                    final String baseName = "value";

                    jsw.writeAttribute(baseName, dataTypeBaseName);

                    jsw.write("public ").write(dataType.getName()).write("(").write("final ").write(dataTypeBaseName)
                            .write(" ").write(baseName).write(")");
                    jsw.write("{");
                    jsw.write("this.").write(baseName).write(" = ").write(baseName).endStmt();
                    jsw.write("}");

                    jsw.writeAccessor(baseName, dataTypeBaseName);

                    jsw.write("@Override").write(" ");
                    jsw.write("public boolean equals(final Object obj)");
                    jsw.write("{");
                    jsw.write("  if (obj instanceof ").write(dataType.getName()).write(")");
                    jsw.write("  {");
                    jsw.write("    final ").write(dataType.getName()).write(" other = (").write(dataType.getName())
                            .write(")obj").endStmt();
                    jsw.write("    return ").write(baseName).write(".equals(").write("other.").write(baseName)
                            .write(")").endStmt();
                    jsw.write("  }");
                    jsw.write("  else");
                    jsw.write("  {");
                    jsw.write("    return false").endStmt();
                    jsw.write("  }");
                    jsw.write("}");

                    jsw.write("@Override").write(" ");
                    jsw.write("public int hashCode()");
                    jsw.write("{");
                    jsw.write("return ").write(baseName).write(".hashCode()").endStmt();
                    jsw.write("}");

                    jsw.write("@Override").write(" ");
                    jsw.write("public String toString()");
                    jsw.write("{");
                    jsw.write("return ").write(baseName).write(".toString()").endStmt();
                    jsw.write("}");
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

    private static final String getDataTypeBase(final DataType dataType, final ModelIndex model) {
        final Identifier id = dataType.getId();
        final List<Generalization> bases = model.getGeneralizationBase(id);
        for (final Generalization base : bases) {
            final Type parent = model.getType(base.getParent());
            return parent.getName();
        }
        return "string";
    }

    private static final void writeEnumType(final String targetPkgName, final EnumType enumType,
            final ModelIndex model, final File file, final JavaGenConfig config) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeEnumType(targetPkgName, enumType, model, outstream, config);
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

    private static final void writeEnumType(final String targetPkgName, final EnumType enumType,
            final ModelIndex model, final OutputStream outstream, final JavaGenConfig config) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8", config);
            try {
                jsw.writePackage(targetPkgName);
                JavadocHelper.writeJavadoc(enumType, model, jsw);
                jsw.beginEnum(enumType.getName());
                try {
                    final Collection<EnumLiteral> enumLiterals = ensureUnique(enumType.getLiterals());
                    final int size = enumLiterals.size();
                    int index = 0;
                    for (final EnumLiteral literal : enumLiterals) {
                        final String name = literal.getName();
                        index += 1;
                        jsw.writeComment(name);
                        jsw.writeEnumLiteral(name, name);
                        jsw.write("(").dblQte().write(name).dblQte().write(")");
                        if (index == size) {
                            jsw.endStmt();
                        } else {
                            jsw.comma();
                        }
                    }
                    jsw.writeAttribute("name", "String");
                    jsw.write(enumType.getName()).write("(").write("final String ").write("name").write(")");
                    jsw.write("{");
                    jsw.write("this.name = name").endStmt();
                    jsw.write("}");

                    jsw.write("public String getName()");
                    jsw.write("{");
                    jsw.write("return name").endStmt();
                    jsw.write("}");

                    jsw.write("public static ").write(enumType.getName()).write(" valueOfName(final String name)");
                    jsw.write("{");
                    jsw.write("  for (final ").write(enumType.getName()).write(" value : values())");
                    jsw.write("  {");
                    jsw.write("    if (value.getName().equals(name))");
                    jsw.write("    {");
                    jsw.write("      return value").endStmt();
                    jsw.write("    }");
                    jsw.write("  }");
                    jsw.write("return null").endStmt();
                    jsw.write("}");
                } finally {
                    jsw.endEnum();
                }
            } finally {
                jsw.flush();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Hack to compensate for bugs in the schemas or model.
     */
    private static final Collection<EnumLiteral> ensureUnique(final List<EnumLiteral> enumLiterals) {
        final Map<String, EnumLiteral> map = new HashMap<String, EnumLiteral>();
        for (final EnumLiteral literal : enumLiterals) {
            map.put(literal.getName(), literal);
        }
        return map.values();
    }

    private static final void writeClassType(final String packageName, final List<String> importNames,
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
        final JavaParam PARAM_ENTITY = new JavaParam("data", FIELD_UNDERLYING.getType(), true);

        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8", config);
            try {
                jsw.writePackage(packageName);
                for (final String importName : importNames) {
                    jsw.writeImport(importName);
                }
                JavadocHelper.writeJavadoc(classType, model, jsw);
                jsw.beginClass(classType.getName());
                try {
                    // Fields
                    jsw.writeAttribute(FIELD_UNDERLYING);

                    // Initializer
                    jsw.write("public");
                    jsw.space();
                    jsw.write(classType.getName());
                    jsw.parenL();
                    jsw.writeParams(PARAM_ENTITY);
                    jsw.parenR();
                    jsw.beginBlock();
                    jsw.beginStmt();
                    jsw.write("this.").write(FIELD_UNDERLYING.getName()).write("=").write(PARAM_ENTITY.getName());
                    jsw.endStmt();
                    jsw.endBlock();

                    // FIXME: Not very happy with idea that getId might conflict.
                    jsw.write("public").space().writeType(JavaType.JT_STRING).space().write("getId").parenL().parenR();
                    jsw.beginBlock();
                    jsw.beginStmt();
                    jsw.write("return");
                    jsw.space();
                    jsw.castAs(JavaType.JT_STRING);
                    jsw.write(FIELD_UNDERLYING.getName()).write(".get").parenL().dblQte().write("id").dblQte().parenR();
                    jsw.endStmt();
                    jsw.endBlock();

                    for (final JavaFeature feature : getJavaFeatures(classType, model)) {
                        if (feature.isAttribute()) {
                            final String name = feature.getName(config);
                            final JavaType type = feature.getAttributeType(config);
                            JavadocHelper.writeJavadoc(feature.getFeature(), model, jsw);
                            writeAccessor(type, name, jsw);
                        } else if (feature.isNavigable()) {
                            final String name = feature.getName(config);
                            final JavaType type = feature.getNavigableType();
                            JavadocHelper.writeJavadoc(feature.getFeature(), model, jsw);
                            writeAccessor(type, name, jsw);
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

    private static void writeAccessor(final JavaType type, final String name, final JavaStreamWriter jsw)
            throws IOException {
        jsw.write("public");
        jsw.space();
        jsw.writeType(type);
        jsw.space();
        jsw.write("get");
        jsw.write(titleCase(name));
        jsw.parenL();
        jsw.parenR();
        jsw.beginBlock();
        if (JavaType.JT_STRING.equals(type)) {
            jsw.beginStmt();
            jsw.write("return ").castAs(JavaType.JT_STRING).write(FIELD_UNDERLYING.getName()).write(".get").parenL()
                    .dblQte().write(name).dblQte().parenR();
            jsw.endStmt();
        } else if (JavaType.JT_BOOLEAN.equals(type)) {
            jsw.beginStmt();
            jsw.write("return ").castAs(JavaType.JT_BOOLEAN).write(FIELD_UNDERLYING.getName()).write(".get").parenL()
                    .dblQte().write(name).dblQte().parenR();
            jsw.endStmt();
        } else if (JavaType.JT_DOUBLE.equals(type)) {
            jsw.beginStmt();
            jsw.write("return ").castAs(JavaType.JT_DOUBLE).write(FIELD_UNDERLYING.getName()).write(".get").parenL()
                    .dblQte().write(name).dblQte().parenR();
            jsw.endStmt();
        } else if (JavaType.JT_INTEGER.equals(type)) {
            jsw.beginStmt();
            jsw.write("return ").castAs(JavaType.JT_INTEGER).write(FIELD_UNDERLYING.getName()).write(".get").parenL()
                    .dblQte().write(name).dblQte().parenR();
            jsw.endStmt();
        } else if (type.isEnum()) {
            if (type.isList()) {
                jsw.beginStmt().write("final ").writeType(type).write(" list = new ArrayList<")
                        .write(type.getSimpleName()).write(">()").endStmt();
                jsw.beginStmt().write("return list").endStmt();
            } else {
                jsw.beginStmt();
                jsw.write("return ").write(type.getSimpleName()).write(".valueOfName").parenL()
                        .castAs(JavaType.JT_STRING).write(FIELD_UNDERLYING.getName()).write(".get").parenL().dblQte()
                        .write(name).dblQte().parenR().parenR();
                jsw.endStmt();
            }
        } else if (type.isComplex()) {
            if (type.isList()) {
                System.out.println(type);
                jsw.writeReturn("null");
            } else {
                jsw.beginStmt();
                try {
                    jsw.write("return");
                    jsw.space();
                    jsw.write("new");
                    jsw.space();
                    jsw.writeType(type);
                    jsw.parenL();
                    try {
                        jsw.castAs(TYPE_UNDERLYING);
                        jsw.write(FIELD_UNDERLYING.getName());
                        jsw.write(".get");
                        jsw.parenL();
                        try {
                            jsw.dblQte().write(name).dblQte();
                        } finally {
                            jsw.parenR();
                        }
                    } finally {
                        jsw.parenR();
                    }
                } finally {
                    jsw.endStmt();
                }
            }
        } else {
            if (type.isList()) {
                System.out.println(type);
                jsw.writeReturn("null");
            } else {
                jsw.beginStmt();
                try {
                    jsw.write("return");
                    jsw.space();
                    jsw.write("new");
                    jsw.space();
                    jsw.writeType(type);
                    jsw.parenL();
                    try {
                        jsw.castAs(JavaType.JT_STRING);
                        jsw.write(FIELD_UNDERLYING.getName());
                        jsw.write(".get");
                        jsw.parenL();
                        try {
                            jsw.dblQte().write(name).dblQte();
                        } finally {
                            jsw.parenR();
                        }
                    } finally {
                        jsw.parenR();
                    }
                } finally {
                    jsw.endStmt();
                }
            }
        }
        jsw.endBlock();
    }

    private static final String titleCase(final String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
    }

    public static final List<JavaFeature> getJavaFeatures(final ClassType classType, final ModelIndex model) {
        final List<JavaFeature> features = new LinkedList<JavaFeature>();
        for (final Attribute attribute : classType.getAttributes()) {
            features.add(new JavaFeature(attribute, model));
        }
        for (final AssociationEnd associationEnd : model.getAssociationEnds(classType.getId())) {
            features.add(new JavaFeature(associationEnd, model));
        }
        return Collections.unmodifiableList(features);
    }

    @SuppressWarnings("unused")
    private static final List<Feature> getFeatures(final ClassType classType, final ModelIndex model) {
        final List<Feature> features = new LinkedList<Feature>();
        for (final Attribute attribute : classType.getAttributes()) {
            features.add(attribute);
        }
        for (final AssociationEnd associationEnd : model.getAssociationEnds(classType.getId())) {
            features.add(associationEnd);
        }
        return Collections.unmodifiableList(features);
    }
}
