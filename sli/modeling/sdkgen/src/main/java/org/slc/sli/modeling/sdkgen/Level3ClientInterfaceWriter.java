package org.slc.sli.modeling.sdkgen;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;

import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Include;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenElement;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenGrammars;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenResolver;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenType;
import org.slc.sli.modeling.sdkgen.grammars.xsd.SdkGenGrammarsWrapper;
import org.slc.sli.modeling.wadl.helpers.WadlHandler;
import org.slc.sli.modeling.wadl.helpers.WadlHelper;
import org.slc.sli.modeling.xsd.XsdReader;

public final class Level3ClientInterfaceWriter implements WadlHandler {

    private final String packageName;
    private final String className;
    private final File wadlFile;
    private final JavaStreamWriter jsw;
    /**
     * As we encounter schemas in the grammars, we add them here.
     */
    final List<XmlSchema> schemas = new LinkedList<XmlSchema>();

    public Level3ClientInterfaceWriter(final String packageName, final String className, final File wadlFile,
            final JavaStreamWriter jsw) {
        if (packageName == null) {
            throw new NullPointerException("packageName");
        }
        if (className == null) {
            throw new NullPointerException("className");
        }
        if (wadlFile == null) {
            throw new NullPointerException("wadlFile");
        }
        if (jsw == null) {
            throw new NullPointerException("jsw");
        }
        this.packageName = packageName;
        this.className = className;
        this.wadlFile = wadlFile;
        this.jsw = jsw;
    }

    @Override
    public void beginApplication(final Application application) {
        try {
            jsw.writePackage(packageName);
            jsw.writeImport("java.io.IOException");
            jsw.writeImport("java.util.List");
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
    public void method(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) {

        // We're going to need to be able to analyze the request and response types.
        final SdkGenGrammars grammars = new SdkGenGrammarsWrapper(schemas);

        try {
            if (Method.NAME_HTTP_GET.equals(method.getName())) {
                jsw.writeComment(method.getId());
                jsw.beginStmt();
                try {
                    // We really can't write this yet until we have seen the response
                    // representations
                    jsw.write("List<SLIEntity> " + method.getId() + "() throws IOException, SLIDataStoreException");
                    @SuppressWarnings("unused")
                    // Perhaps modify this method to generate a different naming scheme?
                    final String id = WadlHelper.computeId(method, resource, resources, application, ancestors);

                    final Request request = method.getRequest();
                    if (request != null) {
                        for (@SuppressWarnings("unused")
                        final Param param : request.getParams()) {

                        }
                    }

                    final List<Response> responses = method.getResponses();
                    for (final Response response : responses) {
                        try {
                            final List<Representation> representations = response.getRepresentations();
                            for (final Representation representation : representations) {
                                representation.getMediaType();
                                final QName elementName = representation.getElement();
                                final SdkGenElement element = grammars.getElement(elementName);
                                if (element != null) {
                                    final SdkGenType type = element.getType();
                                    System.out.println(elementName.getLocalPart() + " : " + type);
                                } else {
                                    // FIXME: We need to resolve these issues...
                                    // System.out.println(elementName +
                                    // " cannot be resolved as a schema element name.");
                                }
                            }
                        } finally {
                        }
                    }
                } finally {
                    jsw.endStmt();
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void beginResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors) {
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

    }
}
