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
 * Writes the implementation for the Level 2 Client SDK.
 */
public final class Level2ClientInterfaceWriter extends Level3ClientWriter {

    private final String packageName;
    private final String className;

    public Level2ClientInterfaceWriter(final String packageName, final String className, final File wadlFile,
            final JavaStreamWriter jsw) {
        super(jsw, wadlFile);
        if (packageName == null) {
            throw new NullPointerException("packageName");
        }
        if (className == null) {
            throw new NullPointerException("className");
        }
        if (jsw == null) {
            throw new NullPointerException("jsw");
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
            jsw.beginInterface(className);
            for (final Include include : application.getGrammars().getIncludes()) {
                final File schemaFile = new File(wadlFile.getParentFile(), include.getHref());
                schemas.add(XsdReader.readSchema(schemaFile, new SdkGenResolver()));
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeGET(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException {

        final boolean quietMode = true;
        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        final String methodName = method.getId();
        jsw.writeComment(methodName);
        jsw.beginStmt();
        try {
            final JavaType responseType = LevelNClientJavaHelper.getResponseJavaType(method, grammars, quietMode);
            jsw.writeType(LevelNClientJavaHelper.computeGenericType(responseType, quietMode)).space().write(methodName);
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
        jsw.writeComment(method.getId());
        jsw.beginStmt();
        try {
            jsw.writeType(JavaType.JT_STRING).space().write(method.getId());
            jsw.parenL();
            final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
            final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN, wparams, PARAM_ENTITY);
            jsw.writeParams(jparams);
            jsw.parenR();
            jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        } finally {
            jsw.endStmt();
        }
    }

    @Override
    protected void writePUT(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException {
        jsw.writeComment(method.getId());
        jsw.beginStmt();
        try {
            jsw.writeType(JavaType.JT_VOID).space().write(method.getId());
            jsw.parenL();
            final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
            final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN, wparams, PARAM_ENTITY);
            jsw.writeParams(jparams);
            jsw.parenR();
            jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        } finally {
            jsw.endStmt();
        }
    }

    @Override
    protected void writeDELETE(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException {
        jsw.writeComment(method.getId());
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
    public void beginResource(final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors) {
    }

    @Override
    public void endApplication(final Application application) {
        try {
            jsw.endClass();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endResource(final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors) {
    }
}
