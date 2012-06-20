package org.slc.sli.modeling.sdkgen;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.ws.commons.schema.XmlSchema;

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

public final class Level3ClientInterfaceWriter extends Level3ClientWriter {

    private final String packageName;
    private final String className;
    private final File wadlFile;
    /**
     * As we encounter schemas in the grammars, we add them here.
     */
    final List<XmlSchema> schemas = new LinkedList<XmlSchema>();

    public Level3ClientInterfaceWriter(final String packageName, final String className, final File wadlFile,
            final JavaStreamWriter jsw) {
        super(jsw);
        if (packageName == null) {
            throw new NullPointerException("packageName");
        }
        if (className == null) {
            throw new NullPointerException("className");
        }
        if (wadlFile == null) {
            throw new NullPointerException("wadlFile");
        }
        this.packageName = packageName;
        this.className = className;
        this.wadlFile = wadlFile;
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
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeGET(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException {

        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        jsw.writeComment(method.getId());
        jsw.beginStmt();
        try {
            final JavaType responseType = getResponseJavaType(method, grammars);
            jsw.writeType(responseType).space().write(method.getId());
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
    protected void writePOST(Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) throws IOException {

        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        jsw.writeComment(method.getId());
        jsw.beginStmt();
        try {
            // The return type is a string because we return the identifier of the posted resource.
            jsw.writeType(JavaType.JT_STRING).space().write(method.getId());
            jsw.parenL();
            final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
            final JavaType requestType = getRequestJavaType(method, grammars);
            final JavaParam requestParam = new JavaParam("entity", requestType, true);
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

        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        jsw.writeComment(method.getId());
        jsw.beginStmt();
        try {
            jsw.writeType(JavaType.JT_VOID).space().write(method.getId());
            jsw.parenL();
            final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
            final JavaType requestType = getRequestJavaType(method, grammars);
            final JavaParam requestParam = new JavaParam("entity", requestType, true);
            final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN, wparams, requestParam);
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
        // Ignore.
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
        // Ignore.
    }
}
