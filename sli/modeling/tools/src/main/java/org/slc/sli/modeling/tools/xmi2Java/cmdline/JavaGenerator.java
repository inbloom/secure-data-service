package org.slc.sli.modeling.tools.xmi2Java.cmdline;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

public final class JavaGenerator {

    public static void main(final String[] args) {
        try {
            final JavaGenConfig config = new JavaGenConfigBuilder().build();
            doModel("SLI.xmi",
                    "/Users/dholmes/Development/SLI/sli/sli/modeling/tools/src/main/java/org/slc/sli/modeling/ninja",
                    "org.slc.sli.modeling.ninja", config);
            // doModel("xmi-mapping.xmi",
            // "/Users/dholmes/Development/SLI/sli/sli/modeling/tools/src/main/java/org/slc/sli/modeling/tools/xmicomp/cmdline",
            // "org.slc.sli.modeling.tools.xmicomp.cmdline", config);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException(e);
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
            ClassTypeHelper.writeClassType(targetPkgName, importNames, classType, model, file, config);
        }
        for (final EnumType enumType : model.getEnumTypes()) {
            final String fileName = enumType.getName().concat(".java");
            final File file = new File(dir, fileName);
            writeEnumType(targetPkgName, enumType, model, file, config);
        }
        for (final DataType dataType : model.getDataTypes().values()) {
            final String fileName = dataType.getName().concat(".java");
            final File file = new File(dir, fileName);
            writeDataType(targetPkgName, dataType, model, file, config);
        }
    }

    private static final void writeDataType(final String targetPkgName, final DataType dataType,
            final ModelIndex model, final File file, final JavaGenConfig config) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeDataType(targetPkgName, dataType, model, outstream, config);
            } finally {
                CloseableHelper.closeQuiet(outstream);
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
                jsw.beginClass(dataType.getName(), null);
                try {
                    final String dataTypeBaseName = TypeHelper.getAttributePrimeTypeName(getDataTypeBase(dataType,
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
                CloseableHelper.closeQuiet(outstream);
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
                            jsw.writeComma();
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
}
