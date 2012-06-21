package org.slc.sli.modeling.sdkgen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

public final class Level3ClientImplementationWriter extends Level3ClientWriter {

    private static final JavaParam FIELD_BASE_URI = new JavaParam("baseUri", JavaType.JT_STRING, true);
    private static final JavaType JT_LEVEL_TWO_CLIENT = JavaType.simpleType("Level2Client", JavaType.JT_OBJECT);
    private static final JavaParam FIELD_CLIENT = new JavaParam("innerClient", JT_LEVEL_TWO_CLIENT, true);

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
    private final File wadlFile;

    /**
     * As we encounter schemas in the grammars, we add them here.
     */
    final List<XmlSchema> schemas = new LinkedList<XmlSchema>();

    public Level3ClientImplementationWriter(final String packageName, final String className,
            final List<String> interfaces, final File wadlFile, final JavaStreamWriter jsw) {
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
        this.interfaces = Collections.unmodifiableList(new ArrayList<String>(interfaces));
        this.wadlFile = wadlFile;
    }

    @Override
    public void beginApplication(final Application application) {
        try {
            jsw.writePackage(packageName);
            jsw.writeImport("java.io.IOException");
            jsw.writeImport("java.net.URISyntaxException");
            jsw.writeImport("java.net.URI");
            jsw.writeImport("java.util.List");
            jsw.writeImport("java.util.Map");
            jsw.writeImport("org.apache.commons.lang3.StringUtils");
            jsw.beginClass(className, interfaces);
            // Attributes
            jsw.writeAttribute(FIELD_BASE_URI);
            jsw.writeAttribute(FIELD_CLIENT);
            // Write Initializer
            writeCanonicalInitializer(application);
            writeConvenienceInitializer(application);
            for (final Include include : application.getGrammars().getIncludes()) {
                final File schemaFile = new File(wadlFile.getParentFile(), include.getHref());
                schemas.add(XsdReader.readSchema(schemaFile, new SdkGenResolver()));
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors) {
        // Ignore
    }

    private void writeCanonicalInitializer(final Application application) {
        final JavaParam PARAM_BASE_URI = new JavaParam("baseUri", JavaType.JT_STRING, true);
        final JavaParam PARAM_CLIENT = new JavaParam("client", JT_LEVEL_TWO_CLIENT, true);
        try {
            jsw.write("public " + className);
            jsw.parenL();
            jsw.writeParams(PARAM_BASE_URI, PARAM_CLIENT);
            jsw.parenR();
            jsw.beginBlock();
            jsw.beginStmt();
            jsw.write("this.").write(FIELD_BASE_URI.getName()).write("=").write(PARAM_BASE_URI.getName());
            jsw.endStmt();
            jsw.beginStmt();
            jsw.write("this.").write(FIELD_CLIENT.getName()).write("=").write(PARAM_CLIENT.getName());
            jsw.endStmt();
            jsw.endBlock();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeConvenienceInitializer(final Application application) {
        final JavaParam PARAM_BASE_URI = new JavaParam("baseUri", JavaType.JT_STRING, true);
        try {
            jsw.write("public " + className);
            jsw.parenL();
            jsw.writeParams(PARAM_BASE_URI);
            jsw.parenR();
            jsw.beginBlock();
            jsw.beginStmt();
            jsw.write("this");
            jsw.parenL();
            jsw.write(PARAM_BASE_URI.getName() + ", new StandardLevel2Client(baseUri)");
            jsw.parenR();
            jsw.endStmt();
            jsw.endBlock();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeDELETE(Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) throws IOException {
        jsw.writeComment(method.getId());
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
            jsw.write(FIELD_CLIENT.getName()).write(".").write(method.getId());
            jsw.parenL();
            jsw.writeArgs(getArgs(jparams));
            jsw.parenR();
            jsw.endStmt();
        } finally {
            jsw.endBlock();
        }
    }

    @Override
    protected void writeGET(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException {

        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        jsw.writeComment(method.getId());
        jsw.writeOverride();
        final JavaType responseType = getResponseJavaType(method, grammars);
        jsw.write("public").space().writeType(responseType).space().write(method.getId());
        jsw.parenL();
        final List<Param> templateParams = RestHelper.computeRequestTemplateParams(resource, ancestors);
        final List<JavaParam> jparams = Level2ClientJavaHelper.computeJavaGETParams(templateParams);
        jsw.writeParams(jparams);
        jsw.parenR();
        jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        jsw.beginBlock();
        try {
            jsw.beginStmt();
            jsw.write("return").space().write(FIELD_CLIENT.getName()).write(".").write(method.getId());
            jsw.parenL();
            jsw.writeArgs(getArgs(jparams));
            jsw.parenR();
            jsw.endStmt();
        } finally {
            jsw.endBlock();
        }
    }

    @Override
    protected void writePOST(Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) throws IOException {

        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        jsw.writeComment(method.getId());
        jsw.writeOverride();
        jsw.write("public").space().writeType(JavaType.JT_STRING).space().write(method.getId());
        jsw.parenL();
        final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
        final JavaType requestType = getRequestJavaType(method, grammars);
        final JavaParam requestParam = new JavaParam("entity", requestType, true);
        final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN, wparams, requestParam);
        jsw.writeParams(jparams);
        jsw.parenR();
        jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        jsw.beginBlock();
        try {
            jsw.beginStmt();
            jsw.write("return").space().write(FIELD_CLIENT.getName()).write(".").write(method.getId());
            jsw.parenL();
            jsw.writeArgs(getArgs(jparams));
            jsw.parenR();
            jsw.endStmt();
        } finally {
            jsw.endBlock();
        }
    }

    @Override
    protected void writePUT(Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) throws IOException {

        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        jsw.writeComment(method.getId());
        jsw.writeOverride();
        jsw.write("public").space().writeType(JavaType.JT_VOID).space().write(method.getId());
        jsw.parenL();
        final List<Param> wparams = RestHelper.computeRequestTemplateParams(resource, ancestors);
        final JavaType requestType = getRequestJavaType(method, grammars);
        final JavaParam requestParam = new JavaParam("entity", requestType, true);
        final List<JavaParam> jparams = LevelNClientJavaHelper.computeParams(PARAM_TOKEN, wparams, requestParam);
        jsw.writeParams(jparams);
        jsw.parenR();
        jsw.writeThrows(IO_EXCEPTION, STATUS_CODE_EXCEPTION);
        jsw.beginBlock();
        try {
            jsw.beginStmt();
            jsw.write(FIELD_CLIENT.getName()).write(".").write(method.getId());
            jsw.parenL();
            jsw.writeArgs(getArgs(jparams));
            jsw.parenR();
            jsw.endStmt();
        } finally {
            jsw.endBlock();
        }
    }
}
