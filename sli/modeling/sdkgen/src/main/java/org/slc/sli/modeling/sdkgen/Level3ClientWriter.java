package org.slc.sli.modeling.sdkgen;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;

import org.slc.sli.modeling.jgen.JavaCollectionKind;
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
     * The (reserved) name given to the custom property in the SLI database.
     */
    private static final QName CUSTOM_ELEMENT_NAME = new QName("http://www.slcedu.org/api/v1", "custom");
    /**
     * This is the default type used for all JSON objects.
     */
    private static final JavaType JT_MAP_STRING_TO_OBJECT = JavaType.mapType(JavaType.JT_STRING, JavaType.JT_OBJECT);
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
    protected static final JavaType JT_ENTITY = JavaType.simpleType("Entity", JavaType.JT_OBJECT);
    protected static final JavaType JT_LIST_OF_ENTITY = JavaType.collectionType(JavaCollectionKind.LIST, JT_ENTITY);

    protected static final JavaParam PARAM_TOKEN = new JavaParam("token", JavaType.JT_STRING, true);
    protected static final JavaParam PARAM_ENTITY = new JavaParam("entity", JT_ENTITY, true);
    protected static final JavaParam PARAM_ENTITY_ID = new JavaParam("entityId", JavaType.JT_STRING, true);

    protected final JavaStreamWriter jsw;
    protected final File wadlFile;
    /**
     * As we encounter schemas in the grammars, we add them here.
     */
    final List<XmlSchema> schemas = new LinkedList<XmlSchema>();

    public Level3ClientWriter(final JavaStreamWriter jsw, final File wadlFile) {
        if (jsw == null) {
            throw new NullPointerException("jsw");
        }
        if (wadlFile == null) {
            throw new NullPointerException("wadlFile");
        }
        this.jsw = jsw;
        this.wadlFile = wadlFile;
    }

    protected static JavaType getRequestJavaType(final Method method, final SdkGenGrammars grammars,
            final boolean quietMode) {
        final Request request = method.getRequest();
        if (request != null) {
            try {
                final List<Representation> representations = request.getRepresentations();
                for (final Representation representation : representations) {
                    representation.getMediaType();
                    final QName elementName = representation.getElementName();
                    if (elementName != null) {
                        final XmlSchemaElement element = grammars.getElement(elementName);
                        if (element != null) {
                            final Stack<QName> elementNames = new Stack<QName>();
                            return Level3ClientJavaHelper.toJavaTypeFromSchemaElement(element, elementNames, grammars);
                        } else {
                            if (CUSTOM_ELEMENT_NAME.equals(elementName)) {
                                return JT_MAP_STRING_TO_OBJECT;
                            } else {
                                if (quietMode) {
                                    return JavaType.JT_OBJECT;
                                } else {
                                    throw new RuntimeException("Unknown element: " + elementName);
                                }
                            }
                        }
                    } else {
                        if (quietMode) {
                            return JT_MAP_STRING_TO_OBJECT;
                        } else {
                            throw new RuntimeException("Unknown element: " + elementName);
                        }
                    }
                }
            } finally {
            }
        }
        return JavaType.JT_OBJECT;
    }

    protected JavaParam getRequestJavaParam(final Method method, final SdkGenGrammars grammars, final boolean quietMode) {
        final Request request = method.getRequest();
        if (request != null) {
            try {
                final List<Representation> representations = request.getRepresentations();
                for (final Representation representation : representations) {
                    representation.getMediaType();
                    final QName elementName = representation.getElementName();
                    if (elementName != null) {
                        final XmlSchemaElement element = grammars.getElement(elementName);
                        if (element != null) {
                            final Stack<QName> elementNames = new Stack<QName>();
                            final JavaType requestJavaType = Level3ClientJavaHelper.toJavaTypeFromSchemaElement(
                                    element, elementNames, grammars);
                            return new JavaParam(elementName.getLocalPart(), requestJavaType, true);
                        } else {
                            if (CUSTOM_ELEMENT_NAME.equals(elementName)) {
                                return new JavaParam(elementName.getLocalPart(), JT_MAP_STRING_TO_OBJECT, true);
                            } else {
                                if (quietMode) {
                                    return new JavaParam(elementName.getLocalPart(), JavaType.JT_OBJECT, true);
                                } else {
                                    throw new RuntimeException("Unknown element: " + elementName);
                                }
                            }
                        }
                    } else {
                        if (quietMode) {
                            return new JavaParam("unknown", JT_MAP_STRING_TO_OBJECT, true);
                        } else {
                            throw new RuntimeException("Unknown element: " + elementName);
                        }
                    }
                }
            } finally {
            }
        }
        return new JavaParam("obj", JavaType.JT_OBJECT, true);
    }

    protected JavaParam getResponseJavaParam(final Method method, final SdkGenGrammars grammars) {
        final List<Response> responses = method.getResponses();
        for (final Response response : responses) {
            try {
                final List<Representation> representations = response.getRepresentations();
                for (final Representation representation : representations) {
                    representation.getMediaType();
                    final QName elementName = representation.getElementName();
                    final XmlSchemaElement element = grammars.getElement(elementName);
                    if (element != null) {
                        final Stack<QName> elementNames = new Stack<QName>();
                        final JavaType responseJavaType = Level3ClientJavaHelper.toJavaTypeFromSchemaElement(element,
                                elementNames, grammars);
                        return new JavaParam(elementName.getLocalPart(), responseJavaType, true);
                    } else {
                        if (CUSTOM_ELEMENT_NAME.equals(elementName)) {
                            return new JavaParam(elementName.getLocalPart(), JT_MAP_STRING_TO_OBJECT, true);
                        } else {
                            throw new RuntimeException("Unknown element: " + elementName);
                        }
                    }
                }
            } finally {
            }
        }
        return new JavaParam("obj", JavaType.JT_OBJECT, true);
    }

    @Override
    public final void method(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) {
        try {
            final String verb = method.getVerb();
            if (Method.NAME_HTTP_GET.equals(verb)) {
                writeGET(method, resource, resources, application, ancestors);
            } else if (Method.NAME_HTTP_POST.equals(verb)) {
                writePOST(method, resource, resources, application, ancestors);
            } else if (Method.NAME_HTTP_PUT.equals(verb)) {
                writePUT(method, resource, resources, application, ancestors);
            } else if (Method.NAME_HTTP_DELETE.equals(verb)) {
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
