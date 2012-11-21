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

import org.apache.commons.io.IOUtils;
import org.slc.sli.modeling.jgen.JavaCollectionKind;
import org.slc.sli.modeling.jgen.JavaFeature;
import org.slc.sli.modeling.jgen.JavaGenConfig;
import org.slc.sli.modeling.jgen.JavaOutputFactory;
import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.jgen.JavaTypeHelper;
import org.slc.sli.modeling.jgen.JavaTypeKind;
import org.slc.sli.modeling.jgen.JavadocHelper;
import org.slc.sli.modeling.jgen.snippets.Block;
import org.slc.sli.modeling.jgen.snippets.IfThenElse;
import org.slc.sli.modeling.jgen.snippets.MethodCallExpr;
import org.slc.sli.modeling.jgen.snippets.NewInstanceExpr;
import org.slc.sli.modeling.jgen.snippets.NotEqual;
import org.slc.sli.modeling.jgen.snippets.ParenExpr;
import org.slc.sli.modeling.jgen.snippets.ReturnStmt;
import org.slc.sli.modeling.jgen.snippets.VarNameExpr;
import org.slc.sli.modeling.jgen.snippets.Word;
import org.slc.sli.modeling.sdkgen.snippets.CoerceToPojoTypeSnippet;
import org.slc.sli.modeling.sdkgen.snippets.ReturnNewClassTypeSnippet;
import org.slc.sli.modeling.sdkgen.snippets.SetterSnippet;
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
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Level3ClientPojoGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(Level3ClientPojoGenerator.class);
    
    private static final JavaType TYPE_UNDERLYING = JavaType.mapType(JavaType.JT_STRING, JavaType.JT_OBJECT);
    private static final JavaParam FIELD_UNDERLYING = new JavaParam("data", TYPE_UNDERLYING, true);
    
    public static final void doModel(final ModelIndex model, final File dir, final String targetPkgName,
            final JavaGenConfig config) throws FileNotFoundException {
        for (final ClassType classType : model.getClassTypes().values()) {
            if (classType.isClassType()) {
                final String fileName = classType.getName().concat(".java");
                final File file = new File(dir, fileName);
                final List<String> importNames = new ArrayList<String>();
                importNames.add("java.math.*");
                importNames.add("java.util.*");
                importNames.add("org.slc.sli.shtick.CoerceToJson");
                importNames.add("org.slc.sli.shtick.Coercions");
                importNames.add("org.slc.sli.shtick.Mappable");
                writeClassType(targetPkgName, importNames, classType, model, file, config);
            }
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
                IOUtils.closeQuietly(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
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
            throw new SdkGenRuntimeException(e);
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
                IOUtils.closeQuietly(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }
    
    private static final void writeEnumType(final String targetPkgName, final EnumType enumType,
            final ModelIndex model, final OutputStream outstream, final JavaGenConfig config) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8", config);
            try {
                jsw.writePackage(targetPkgName);
                jsw.writeImport("org.slc.sli.shtick.StringEnum");
                JavadocHelper.writeJavadoc(enumType, model, jsw);
                final List<String> implementations = new ArrayList<String>(1);
                implementations.add("StringEnum");
                jsw.beginEnum(enumType.getName(), implementations);
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
                    
                    jsw.write("@Override");
                    jsw.space();
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
            throw new SdkGenRuntimeException(e);
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
                IOUtils.closeQuietly(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }
    
    private static void writeClassTypeValueOfFunction(final JavaType classType, final JavaStreamWriter jsw)
            throws IOException {
        jsw.write("public").space().write("static").space().writeType(classType).space().write("valueOf");
        jsw.parenL();
        jsw.writeParams(FIELD_UNDERLYING);
        jsw.parenR();
        jsw.beginBlock();
        try {
            final JavaSnippet testSnippet = new NotEqual(new VarNameExpr(FIELD_UNDERLYING.getName()), Word.NULL);
            final JavaSnippet thenSnippet = new ReturnNewClassTypeSnippet(classType, FIELD_UNDERLYING);
            final JavaSnippet elseSnippet = new ReturnStmt(Word.NULL);
            final JavaSnippet ite = new IfThenElse(testSnippet, thenSnippet, elseSnippet);
            ite.write(jsw);
        } finally {
            jsw.endBlock();
        }
    }
    
    private static void writeClassTypeGetIdMethod(final JavaStreamWriter jsw) throws IOException {
        jsw.write("public").space().writeType(JavaType.JT_STRING).space().write("getId").parenL().parenR();
        new Block(new ReturnStmt(new CoerceToPojoTypeSnippet(FIELD_UNDERLYING, "id", JavaType.JT_STRING))).write(jsw);
    }
    
    private static void writeClassTypeToMapMethod(final JavaStreamWriter jsw) throws IOException {
        jsw.write("@Override");
        jsw.space();
        jsw.write("public").space().writeType(TYPE_UNDERLYING).space().write("toMap").parenL().parenR();
        new Block(new ReturnStmt(new VarNameExpr(FIELD_UNDERLYING.getName()))).write(jsw);
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
                final JavaType javaClassType = JavaType.simpleType(classType.getName(), JavaType.JT_OBJECT);
                JavadocHelper.writeJavadoc(classType, model, jsw);
                final List<String> implementations = new ArrayList<String>(1);
                implementations.add("Mappable");
                final List<Generalization> generalizations = model.getGeneralizationBase(classType.getId());
                if (generalizations != null) {
                    if (generalizations.size() == 1) {
                        jsw.beginClass(classType.getName(), implementations,
                                model.getType(generalizations.get(0).getParent()).getName());
                    } else if (generalizations.size() == 0) {
                        jsw.beginClass(classType.getName(), implementations);
                    } else {
                        throw new AssertionError("Multiple inheritance for entity : " + classType.getName());
                    }
                }

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
                    
                    writeDefaultConstructor(classType, jsw);
                    
                    writeClassTypeValueOfFunction(javaClassType, jsw);
                    
                    writeClassTypeGetIdMethod(jsw);
                    
                    writeClassTypeToMapMethod(jsw);

                    for (final JavaFeature feature : getJavaFeatures(classType, model)) {
                        if (feature.isAttribute()) {
                            final String name = feature.getName(config);
                            final JavaType type = feature.getAttributeType(config);
                            JavadocHelper.writeJavadoc(feature.getFeature(), model, jsw);
                            writeGetter(type, name, jsw);
                            writeSetter(type, name, jsw);
                        } else if (feature.isNavigable()) {
                            final String name;
                            if (feature.getFeature() instanceof AssociationEnd) {
                                name = ((AssociationEnd) feature.getFeature()).getAssociatedAttributeName();
                            } else {
                                name = feature.getName(config);
                            }
                            final JavaType type = feature.getNavigableType();
                            JavadocHelper.writeJavadoc(feature.getFeature(), model, jsw);
                            writeGetter(type, name, jsw);
                            writeSetter(type, name, jsw);
                        }
                    }
                } finally {
                    jsw.endClass();
                }
            } finally {
                jsw.flush();
            }
        } catch (final IOException e) {
            throw new SdkGenRuntimeException(e);
        }
    }
    
    private static void writeDefaultConstructor(final ClassType classType, final JavaStreamWriter jsw)
            throws IOException {
        // Default initializer
        jsw.write("public");
        jsw.space();
        jsw.write(classType.getName()).parenL().parenR().beginBlock();
        jsw.write("this").parenL().write(Word.NEW).space().write("HashMap<String,Object>").parenL().parenR().parenR()
                .endStmt();
        jsw.endBlock();
    }
    
    private static void writeSetter(final JavaType type, final String name, final JavaStreamWriter jsw)
            throws IOException {
        new SetterSnippet(type.getBaseType(), name).write(jsw);
    }
    
    private static void writeGetter(final JavaType type, final String name, final JavaStreamWriter jsw)
            throws IOException {
        final JavaType baseType = type.getBaseType();
        jsw.write("public");
        jsw.space();
        jsw.writeType(baseType);
        jsw.space();
        jsw.write("get");
        jsw.write(titleCase(name));
        jsw.parenL();
        jsw.parenR();
        jsw.beginBlock();
        if (JavaType.JT_STRING.equals(baseType)) {
            new ReturnStmt(new CoerceToPojoTypeSnippet(FIELD_UNDERLYING, name, baseType)).write(jsw);
        } else if (JavaType.JT_BOOLEAN.equals(baseType)) {
            new ReturnStmt(new CoerceToPojoTypeSnippet(FIELD_UNDERLYING, name, baseType)).write(jsw);
        } else if (JavaType.JT_DOUBLE.equals(baseType)) {
            new ReturnStmt(new CoerceToPojoTypeSnippet(FIELD_UNDERLYING, name, baseType)).write(jsw);
        } else if (JavaType.JT_INTEGER.equals(baseType)) {
            new ReturnStmt(new CoerceToPojoTypeSnippet(FIELD_UNDERLYING, name, baseType)).write(jsw);
        } else if (JavaType.JT_BIG_INTEGER.equals(baseType)) {
            new ReturnStmt(new CoerceToPojoTypeSnippet(FIELD_UNDERLYING, name, baseType)).write(jsw);
        } else if (baseType.getTypeKind() == JavaTypeKind.ENUM) {
            if (type.getCollectionKind() == JavaCollectionKind.LIST) {
                JavaType javaType = new JavaType(baseType.getSimpleName(), baseType.getCollectionKind(),
                        baseType.getTypeKind(), baseType.getBase());
                final JavaType returnType = JavaType.collectionType(JavaCollectionKind.LIST, javaType);
                final String rList = "rList";
                final String elem = "elem";
                jsw.writeAssignment(new JavaParam(rList, returnType, true),
                        new NewInstanceExpr(JavaType.collectionType(JavaCollectionKind.ARRAY_LIST, javaType)));

                jsw.write("for").parenL().writeParams(new JavaParam(elem, JavaType.JT_OBJECT, true));
                jsw.write(":");
                jsw.write(new CoerceToPojoTypeSnippet(FIELD_UNDERLYING, name, javaType)).parenR();
                jsw.beginBlock();

                jsw.write(new MethodCallExpr(
                        new VarNameExpr(rList), "add",
                        new MethodCallExpr(new VarNameExpr(baseType.getSimpleName()),
                                "valueOfName",
                                new VarNameExpr("Coercions.toString(elem)")))).endStmt();

                jsw.endBlock();

                jsw.write(new ReturnStmt(new VarNameExpr(rList)));
            } else {
                jsw.beginStmt();
                jsw.write("return ").write(baseType.getSimpleName()).write(".valueOfName");
                jsw.parenL();
                try {
                    new CoerceToPojoTypeSnippet(FIELD_UNDERLYING, name, JavaType.JT_STRING).write(jsw);
                } finally {
                    jsw.parenR();
                }
                jsw.endStmt();
            }
        } else if (baseType.getTypeKind() == JavaTypeKind.COMPLEX || baseType.getTypeKind() == JavaTypeKind.SIMPLE) {
            if (type.getCollectionKind() == JavaCollectionKind.LIST) {
                JavaType javaType = new JavaType(baseType.getSimpleName(), baseType.getCollectionKind(),
                        baseType.getTypeKind(), baseType.getBase());
                final JavaType returnType = JavaType.collectionType(JavaCollectionKind.LIST, javaType);
                final String rList = "rList";
                final String elem = "elem";
                jsw.writeAssignment(new JavaParam(rList, returnType, true),
                        new NewInstanceExpr(JavaType.collectionType(JavaCollectionKind.ARRAY_LIST, javaType)));
                
                jsw.write("for").parenL().writeParams(new JavaParam(elem, JavaType.JT_OBJECT, true));
                jsw.write(":");
                jsw.write(new CoerceToPojoTypeSnippet(FIELD_UNDERLYING, name, javaType)).parenR();
                jsw.beginBlock();

                if (baseType.getTypeKind() == JavaTypeKind.COMPLEX) {
                    jsw.write(
                            new MethodCallExpr(new VarNameExpr(rList), "add", new NewInstanceExpr(javaType.primeType(),
                                    new VarNameExpr("Coercions.toMap(elem)")))).endStmt();
                } else {
                    jsw.write(
                            new MethodCallExpr(new VarNameExpr(rList), "add",
                                    new VarNameExpr("Coercions.toString(elem)"))).endStmt();
                }
                jsw.endBlock();
                
                jsw.write(new ReturnStmt(new VarNameExpr(rList)));
            } else {
                jsw.beginStmt();
                try {
                    jsw.write("return");
                    jsw.space();
                    jsw.writeType(baseType);
                    jsw.write(".valueOf");
                    new ParenExpr(new CoerceToPojoTypeSnippet(FIELD_UNDERLYING, name, TYPE_UNDERLYING)).write(jsw);
                } finally {
                    jsw.endStmt();
                }
            }
        } else {
            if (type.getCollectionKind() == JavaCollectionKind.LIST) {
                new ReturnStmt(Word.NULL).write(jsw);
            } else {
                jsw.beginStmt();
                try {
                    jsw.write("return");
                    jsw.space();
                    jsw.write("new");
                    jsw.space();
                    jsw.writeType(baseType);
                    new ParenExpr(new CoerceToPojoTypeSnippet(FIELD_UNDERLYING, name, baseType.getBase())).write(jsw);
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
