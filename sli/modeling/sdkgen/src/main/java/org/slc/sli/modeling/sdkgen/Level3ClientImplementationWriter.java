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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.ws.commons.schema.XmlSchema;

import org.slc.sli.modeling.jgen.JavaCollectionKind;
import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaSnippetExpr;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.jgen.snippets.EnhancedForLoop;
import org.slc.sli.modeling.jgen.snippets.MethodCallExpr;
import org.slc.sli.modeling.jgen.snippets.NewInstanceExpr;
import org.slc.sli.modeling.jgen.snippets.ReturnStmt;
import org.slc.sli.modeling.jgen.snippets.Stmt;
import org.slc.sli.modeling.jgen.snippets.VarNameExpr;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Include;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.helpers.RestHelper;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenGrammars;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenResolver;
import org.slc.sli.modeling.sdkgen.grammars.xsd.SdkGenGrammarsWrapper;
import org.slc.sli.modeling.sdkgen.snippets.NewEntityFromListExpr;
import org.slc.sli.modeling.sdkgen.snippets.NewEntityFromMapExpr;
import org.slc.sli.modeling.sdkgen.snippets.NewEntityFromMappableExpr;
import org.slc.sli.modeling.xsd.XsdReader;

public final class Level3ClientImplementationWriter extends Level3ClientWriter {

    private static final JavaType JT_LEVEL_TWO_CLIENT = JavaType.simpleType("Level2Client", JavaType.JT_OBJECT);
    private static final JavaParam FIELD_CLIENT = new JavaParam("innerClient", JT_LEVEL_TWO_CLIENT, true);
    private static final VarNameExpr VARNAME_INNER_CLIENT = new VarNameExpr(FIELD_CLIENT.getName());

    private static final List<String> getArgs(final List<JavaParam> params) {
        final List<String> args = new ArrayList<String>(params.size());
        for (final JavaParam param : params) {
            args.add(param.getName());
        }
        return args;
    }

    private final String packageName;
    private final String className;
    private final List<String> interfaces;

    /**
     * As we encounter schemas in the grammars, we add them here.
     */
    final List<XmlSchema> schemas = new LinkedList<XmlSchema>();

    public Level3ClientImplementationWriter(final String packageName, final String className,
            final List<String> interfaces, final File wadlFile, final JavaStreamWriter jsw) {
        super(jsw, wadlFile);
        if (packageName == null) {
            throw new IllegalArgumentException("packageName");
        }
        if (className == null) {
            throw new IllegalArgumentException("className");
        }
        this.packageName = packageName;
        this.className = className;
        this.interfaces = Collections.unmodifiableList(new ArrayList<String>(interfaces));
    }

    @Override
    public void beginApplication(final Application application) {
        try {
            jsw.writePackage(packageName);
            jsw.writeImport("java.io.IOException");
            jsw.writeImport("java.net.URISyntaxException");
            jsw.writeImport("java.net.URI");
            jsw.writeImport("java.util.ArrayList");
            jsw.writeImport("java.util.List");
            jsw.writeImport("java.util.Map");
            jsw.writeImport("org.slc.sli.shtick.pojo.*");
            jsw.writeImport("org.apache.commons.lang3.StringUtils");
            jsw.beginClass(className, interfaces);
            // Attributes
            jsw.writeAttribute(FIELD_CLIENT);
            // Write Initializer
            writeCanonicalInitializer(application);
            writeConvenienceInitializer(application);
            for (final Include include : application.getGrammars().getIncludes()) {
                final File schemaFile = new File(wadlFile.getParentFile(), include.getHref());
                schemas.add(XsdReader.readSchema(schemaFile, new SdkGenResolver()));
            }
        } catch (final IOException e) {
            throw new SdkGenRuntimeException(e);
        }
    }

    @Override
    public void beginResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors) {
        // Ignore
    }

    @Override
    public void endApplication(final Application application) {
        try {
            jsw.endClass();
        } catch (final IOException e) {
            throw new SdkGenRuntimeException(e);
        }
    }

    @Override
    public void endResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors) {
        // Ignore
    }

    private void writeCanonicalInitializer(final Application application) {
        final JavaParam PARAM_CLIENT = new JavaParam("client", JT_LEVEL_TWO_CLIENT, true);
        try {
            jsw.write("public " + className);
            jsw.parenL();
            jsw.writeParams(PARAM_CLIENT);
            jsw.parenR();
            jsw.beginBlock();
            jsw.beginStmt();
            jsw.write("this.").write(VARNAME_INNER_CLIENT).write("=").write(PARAM_CLIENT.getName());
            jsw.endStmt();
            jsw.endBlock();
        } catch (final IOException e) {
            throw new SdkGenRuntimeException(e);
        }
    }

    private void writeConvenienceInitializer(final Application application) {
        final JavaParam PARAM_BASE_URI = new JavaParam("baseUri", JavaType.JT_STRING, true);
        try {
            jsw.write("public").space().write(className);
            jsw.parenL();
            jsw.writeParams(PARAM_BASE_URI);
            jsw.parenR();
            jsw.beginBlock();
            jsw.beginStmt();
            jsw.write("this");
            jsw.parenL();
            jsw.write("new StandardLevel2Client(baseUri)");
            jsw.parenR();
            jsw.endStmt();
            jsw.endBlock();
        } catch (final IOException e) {
            throw new SdkGenRuntimeException(e);
        }
    }

    @Override
    protected void writeDELETE(Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) throws IOException {
        writeMethodDocumentation(method);
        jsw.writeOverride();
        jsw.write("public").space().writeType(JavaType.JT_VOID).space().write(method.getId());
        jsw.parenL();
        final List<JavaParam> jparams = new LinkedList<JavaParam>();
        jparams.add(PARAM_TOKEN);
        jparams.add(PARAM_ENTITY_ID);
        jsw.writeParams(jparams);
        jsw.parenR();
        jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        jsw.beginBlock();
        try {
            jsw.beginStmt();
            jsw.write(VARNAME_INNER_CLIENT).write(".").write(method.getId());
            jsw.parenL();
            jsw.writeArgs(getArgs(jparams));
            jsw.parenR();
            jsw.endStmt();
        } finally {
            jsw.endBlock();
        }
    }

    @Override
    protected void writePATCH(Method method, Resource resource, Resources resources, Application application, Stack<Resource> ancestors) throws IOException {
        final boolean quietMode = true;
        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        final String methodName = method.getId();

        writeMethodDocumentation(method);

        jsw.writeOverride();
        jsw.write("public").space().writeType(JavaType.JT_VOID).space().write(methodName);
        jsw.parenL();
        final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
        final JavaParam requestParam = getRequestJavaParam(method, grammars, quietMode);
        final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN, wparams, requestParam);
        jsw.writeParams(jparams);
        jsw.parenR();
        jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        jsw.beginBlock();
        try {
            final JavaParam entityParam = new JavaParam("entity", JT_ENTITY, true);
            final JavaSnippetExpr rhs = computeRequestParamMapping(entityParam.getType(), requestParam);
            jsw.writeAssignment(entityParam, rhs);

            jsw.beginStmt();
            jsw.write(VARNAME_INNER_CLIENT).write(".").write(methodName);
            jsw.parenL();
            final List<JavaParam> callParameters = getRequestCallParameters(method, resource, ancestors, grammars,
                    quietMode);
            jsw.writeArgs(getArgs(callParameters));
            jsw.parenR();
            jsw.endStmt();
        } finally {
            jsw.endBlock();
        }
    }

    @Override
    protected void writeGET(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException {

        final boolean quietMode = true;
        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        final String methodName = method.getId();

        writeMethodDocumentation(method);

        jsw.writeOverride();
        final JavaType responseType = LevelNClientJavaHelper.getResponseJavaType(method, grammars, quietMode);
        jsw.write("public").space().writeType(responseType).space().write(methodName);
        jsw.parenL();
        final List<Param> templateParams = RestHelper.computeRequestTemplateParams(resource, ancestors);
        final List<JavaParam> jparams = Level2ClientJavaHelper.computeJavaGETParams(templateParams);
        jsw.writeParams(jparams);
        jsw.parenR();
        jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        jsw.beginBlock();
        try {
            final List<JavaSnippetExpr> args = computeArgs(jparams);
            final MethodCallExpr callInner = new MethodCallExpr(VARNAME_INNER_CLIENT, methodName, args);
            if (LevelNClientJavaHelper.isMapStringToObject(responseType)) {
                jsw.write(new ReturnStmt(callInner));
            } else if (LevelNClientJavaHelper.isSingleton(responseType)) {
                final JavaParam entity = new JavaParam("entity", JT_ENTITY, true);
                jsw.writeAssignment(entity, callInner);
                jsw.write(new ReturnStmt(new NewInstanceExpr(responseType.primeType(), new MethodCallExpr(
                                new VarNameExpr(entity.getName()), "getData"))));
            } else {
                final JavaParam entityList = new JavaParam("entityList", JT_LIST_OF_ENTITY, true);
                jsw.writeAssignment(entityList, callInner);

                final JavaParam responseList = new JavaParam("responseList", responseType, true);
                final JavaType responseArrayListType = JavaType.collectionType(JavaCollectionKind.ARRAY_LIST,
                        responseList.getType());
                jsw.writeAssignment(responseList, new NewInstanceExpr(responseArrayListType));

                final JavaParam entity = new JavaParam("entity", JT_ENTITY, true);

                if (LevelNClientJavaHelper.isEntityList(responseType)) {
                    jsw.write(new EnhancedForLoop(entity, new VarNameExpr(entityList.getName()), new Stmt(
                            new MethodCallExpr(new VarNameExpr(responseList.getName()), "add",
                                    new VarNameExpr(entity.getName())))));
                } else {
                    jsw.write(new EnhancedForLoop(entity, new VarNameExpr(entityList.getName()), new Stmt(
                            new MethodCallExpr(new VarNameExpr(responseList.getName()), "add", new NewInstanceExpr(
                                    responseType.primeType(), new MethodCallExpr(new VarNameExpr(entity.getName()),
                                    "getData"))))));
                }
                jsw.write(new ReturnStmt(new VarNameExpr(responseList.getName())));
            }
        } finally {
            jsw.endBlock();
        }
    }

    private static List<JavaSnippetExpr> computeArgs(final List<JavaParam> jparams) {
        final List<JavaSnippetExpr> args = new ArrayList<JavaSnippetExpr>(jparams.size());
        for (final JavaParam jparam : jparams) {
            args.add(new VarNameExpr(jparam.getName()));
        }
        return args;
    }

    @Override
    protected void writePOST(Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) throws IOException {

        final boolean quietMode = true;
        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        writeMethodDocumentation(method);

        jsw.writeOverride();
        jsw.write("public").space().writeType(JavaType.JT_STRING).space().write(method.getId());
        jsw.parenL();
        final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
        final JavaParam requestParam = getRequestJavaParam(method, grammars, quietMode);
        final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN, wparams, requestParam);
        jsw.writeParams(jparams);
        jsw.parenR();
        jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        jsw.beginBlock();
        try {
            final JavaParam entityParam = new JavaParam("entity", JT_ENTITY, true);
            final JavaSnippetExpr rhs = computeRequestParamMapping(entityParam.getType(), requestParam);
            jsw.writeAssignment(entityParam, rhs);

            jsw.beginStmt();
            jsw.write("return").space().write(VARNAME_INNER_CLIENT).write(".").write(method.getId());
            jsw.parenL();
            final List<JavaParam> callParameters = getRequestCallParameters(method, resource, ancestors, grammars,
                    quietMode);
            jsw.writeArgs(getArgs(callParameters));
            jsw.parenR();
            jsw.endStmt();
        } finally {
            jsw.endBlock();
        }
    }

    private static final JavaSnippetExpr computeRequestParamMapping(final JavaType targetType,
            final JavaParam sourceParam) {
        final JavaType sourceType = sourceParam.getType();
        switch (sourceType.getCollectionKind()) {
        case NONE: {
            return new NewEntityFromMappableExpr(targetType, sourceParam.getName());
        }
        case MAP: {
            return new NewEntityFromMapExpr(targetType, sourceParam.getName());
        }
        case LIST: {
            return new NewEntityFromListExpr(targetType, sourceParam.getName());
        }
        default: {
            throw new AssertionError(sourceType.getCollectionKind());
        }
        }
    }

    private static List<JavaParam> getRequestCallParameters(final Method method, final Resource resource,
            final Stack<Resource> ancestors, final SdkGenGrammars grammars, final boolean quietMode) {
        final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
        final JavaType requestType = getRequestJavaType(method, grammars, quietMode);
        final JavaParam requestParam = new JavaParam("entity", requestType, true);
        final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN, wparams, requestParam);
        return jparams;
    }

    @Override
    protected void writePUT(final Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) throws IOException {

        final boolean quietMode = true;
        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        final String methodName = method.getId();

        writeMethodDocumentation(method);

        jsw.writeOverride();
        jsw.write("public").space().writeType(JavaType.JT_VOID).space().write(methodName);
        jsw.parenL();
        final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
        final JavaParam requestParam = getRequestJavaParam(method, grammars, quietMode);
        final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN, wparams, requestParam);
        jsw.writeParams(jparams);
        jsw.parenR();
        jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        jsw.beginBlock();
        try {
            final JavaParam entityParam = new JavaParam("entity", JT_ENTITY, true);
            final JavaSnippetExpr rhs = computeRequestParamMapping(entityParam.getType(), requestParam);
            jsw.writeAssignment(entityParam, rhs);

            jsw.beginStmt();
            jsw.write(VARNAME_INNER_CLIENT).write(".").write(methodName);
            jsw.parenL();
            final List<JavaParam> callParameters = getRequestCallParameters(method, resource, ancestors, grammars,
                    quietMode);
            jsw.writeArgs(getArgs(callParameters));
            jsw.parenR();
            jsw.endStmt();
        } finally {
            jsw.endBlock();
        }
    }
}
