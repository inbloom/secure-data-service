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
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;
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
import org.slc.sli.modeling.xsd.XsdReader;

/**
 * Write SDK Client.
 */
public final class Level3ClientInterfaceWriter extends Level3ClientWriter {
    
    private final String packageName;
    private final String className;
    
    public Level3ClientInterfaceWriter(final String packageName, final String className, final File wadlFile,
            final JavaStreamWriter jsw) {
        super(jsw, wadlFile);
        if (packageName == null) {
            throw new IllegalArgumentException("packageName");
        }
        if (className == null) {
            throw new IllegalArgumentException("className");
        }
        this.packageName = packageName;
        this.className = className;
    }
    
    @Override
    public void beginApplication(final Application application) {
        try {
            jsw.writePackage(packageName);
            jsw.writeImport("java.io.IOException");
            jsw.writeImport("java.util.List");
            jsw.writeImport("java.util.Map");
            jsw.writeImport("org.slc.sli.shtick.pojo.*");
            jsw.beginInterface(className);
            for (final Include include : application.getGrammars().getIncludes()) {
                final File schemaFile = new File(wadlFile.getParentFile(), include.getHref());
                schemas.add(XsdReader.readSchema(schemaFile, new SdkGenResolver()));
            }
        } catch (final IOException e) {
            throw new SdkGenRuntimeException(e);
        }
    }
    
    @Override
    protected void writeGET(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException {
        
        final boolean quietMode = true;
        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);
        
        final String methodName = method.getId();
        
        writeMethodDocumentation(method);
        
        jsw.beginStmt();
        try {
            final JavaType responseType = LevelNClientJavaHelper.getResponseJavaType(method, grammars, quietMode);
            jsw.writeType(responseType).space().write(methodName);
            jsw.parenL();
            final List<Param> templateParams = RestHelper.computeRequestTemplateParams(resource, ancestors);
            final List<JavaParam> params = Level2ClientJavaHelper.computeJavaGETParams(templateParams);
            jsw.writeParams(params);
            jsw.parenR();
            jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
            
        } finally {
            jsw.endStmt();
        }
    }
    
    @Override
    protected void writePOST(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException {
        
        final boolean quietMode = true;
        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);
        
        final String methodName = method.getId();
        
        writeMethodDocumentation(method);
        
        jsw.beginStmt();
        try {
            // The return type is a string because we return the identifier of the posted resource.
            jsw.writeType(JavaType.JT_STRING).space().write(methodName);
            jsw.parenL();
            final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
            final JavaParam requestParam = getRequestJavaParam(method, grammars, quietMode);
            final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN, wparams, requestParam);
            jsw.writeParams(jparams);
            jsw.parenR();
            jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        } finally {
            jsw.endStmt();
        }
    }
    
    @Override
    protected void writePUT(Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) throws IOException {
        
        final boolean quietMode = true;
        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);
        
        final String methodName = method.getId();
        
        writeMethodDocumentation(method);
        
        jsw.beginStmt();
        try {
            jsw.writeType(JavaType.JT_VOID).space().write(methodName);
            jsw.parenL();
            final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
            final JavaParam requestParam = getRequestJavaParam(method, grammars, quietMode);
            final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN, wparams, requestParam);
            //
            // final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource,
            // ancestors);
            // final JavaType requestType = getRequestJavaType(method, grammars);
            // final JavaParam requestParam = new JavaParam("entity", requestType, true);
            // final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN,
            // wparams, requestParam);
            jsw.writeParams(jparams);
            jsw.parenR();
            jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        } finally {
            jsw.endStmt();
        }
    }
    
    @Override
    protected void writeDELETE(Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) throws IOException {
        
        writeMethodDocumentation(method);
        
        jsw.beginStmt();
        try {
            jsw.writeType(JavaType.JT_VOID).space().write(method.getId());
            jsw.parenL();
            final List<JavaParam> params = new LinkedList<JavaParam>();
            params.add(PARAM_TOKEN);
            params.add(PARAM_ENTITY_ID);
            jsw.writeParams(params);
            jsw.parenR();
            jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        } finally {
            jsw.endStmt();
        }
    }

    @Override
    protected void writePATCH(Method method, Resource resource, Resources resources, Application application, Stack<Resource> ancestors) throws IOException {
        final boolean quietMode = true;
        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        final String methodName = method.getId();

        writeMethodDocumentation(method);

        jsw.beginStmt();
        try {
            jsw.writeType(JavaType.JT_VOID).space().write(methodName);
            jsw.parenL();
            final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
            final JavaParam requestParam = getRequestJavaParam(method, grammars, quietMode);
            final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN, wparams, requestParam);

            jsw.writeParams(jparams);
            jsw.parenR();
            jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        } finally {
            jsw.endStmt();
        }
    }

    @Override
    public void beginResource(final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors) {
        // Ignore.
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
    public void endResource(final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors) {
        // Ignore.
    }
    
}
