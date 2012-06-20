package org.slc.sli.modeling.sdkgen;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaElement;

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenGrammars;
import org.slc.sli.modeling.wadl.helpers.WadlHandler;

public abstract class Level3ClientWriter implements WadlHandler {
    /**
     * The simple name of the exception that indicates the HTTP Status Code.
     */
    protected static final JavaType STATUS_CODE_EXCEPTION = JavaType.simpleType("StatusCodeException",
            JavaType.JT_EXCEPTION);
    /**
     * The simple name of the exception that indicates the HTTP Status Code.
     */
    protected static final JavaType IO_EXCEPTION = JavaType.simpleType(IOException.class.getSimpleName(),
            JavaType.JT_EXCEPTION);
    /**
     * The simple name of the domain neutral entity class.
     */
    protected static final JavaType GENERIC_ENTITY = JavaType.simpleType("Entity", JavaType.JT_OBJECT);

    protected static final JavaParam PARAM_TOKEN = new JavaParam("token", JavaType.JT_STRING, true);
    // protected static final JavaParam PARAM_ENTITY = new JavaParam("entity", GENERIC_ENTITY,
    // true);
    protected static final JavaParam PARAM_ENTITY_ID = new JavaParam("entityId", JavaType.JT_STRING, true);

    protected final JavaStreamWriter jsw;

    public Level3ClientWriter(final JavaStreamWriter jsw) {
        if (jsw == null) {
            throw new NullPointerException("jsw");
        }
        this.jsw = jsw;
    }

    protected JavaType getResponseJavaType(final Method method, final SdkGenGrammars grammars) {
        final List<Response> responses = method.getResponses();
        for (final Response response : responses) {
            try {
                final List<Representation> representations = response.getRepresentations();
                for (final Representation representation : representations) {
                    representation.getMediaType();
                    final QName elementName = representation.getElement();
                    final XmlSchemaElement element = grammars.getElement(elementName);
                    if (element != null) {
                        final Stack<QName> elementNames = new Stack<QName>();
                        return Level3ClientJavaHelper.showElement(element, elementNames);
                    } else {
                        // FIXME: We need to resolve these issues...
                        // System.out.println(elementName +
                        // " cannot be resolved as a schema element name.");
                    }
                }
            } finally {
            }
        }
        return JavaType.JT_OBJECT;
    }

    protected JavaType getRequestJavaType(final Method method, final SdkGenGrammars grammars) {
        final Request request = method.getRequest();
        if (request != null) {
            try {
                final List<Representation> representations = request.getRepresentations();
                for (final Representation representation : representations) {
                    representation.getMediaType();
                    final QName elementName = representation.getElement();
                    final XmlSchemaElement element = grammars.getElement(elementName);
                    if (element != null) {
                        final Stack<QName> elementNames = new Stack<QName>();
                        return Level3ClientJavaHelper.showElement(element, elementNames);
                    } else {
                        // FIXME: We need to resolve these issues...
                        // System.out.println(elementName +
                        // " cannot be resolved as a schema element name.");
                    }
                }
            } finally {
            }
        }
        return JavaType.JT_OBJECT;
    }

    @Override
    public final void method(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) {
        try {
            if (Method.NAME_HTTP_GET.equals(method.getName())) {
                writeGET(method, resource, resources, application, ancestors);
            } else if (Method.NAME_HTTP_POST.equals(method.getName())) {
                writePOST(method, resource, resources, application, ancestors);
            } else if (Method.NAME_HTTP_PUT.equals(method.getName())) {
                writePUT(method, resource, resources, application, ancestors);
            } else if (Method.NAME_HTTP_DELETE.equals(method.getName())) {
                writeDELETE(method, resource, resources, application, ancestors);
            } else {
                throw new AssertionError(method);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void writeGET(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException;

    protected abstract void writePOST(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException;

    protected abstract void writePUT(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException;

    protected abstract void writeDELETE(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) throws IOException;

}
