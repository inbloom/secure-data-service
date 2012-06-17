package org.slc.sli.modeling.sdkgen;

import java.io.IOException;
import java.util.Stack;

import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.wadl.helpers.WadlHandler;

public abstract class Level2ClientWriter implements WadlHandler {
    /**
     * The simple name of the exception that indicates the HTTP Status Code.
     */
    protected static final JavaType STATUS_CODE_EXCEPTION = new JavaType("StatusCodeException");
    /**
     * The simple name of the exception that indicates the HTTP Status Code.
     */
    protected static final JavaType IO_EXCEPTION = new JavaType(IOException.class.getSimpleName());
    /**
     * The simple name of the domain neutral entity class.
     */
    protected static final String GENERIC_ENTITY = "Entity";

    protected static final JavaParam PARAM_TOKEN = new JavaParam("token", "String", true);
    protected static final JavaParam PARAM_ENTITY = new JavaParam("entity", GENERIC_ENTITY, true);
    protected static final JavaParam PARAM_ENTITY_ID = new JavaParam("entityId", "String", true);

    protected final JavaStreamWriter jsw;

    public Level2ClientWriter(final JavaStreamWriter jsw) {
        if (jsw == null) {
            throw new NullPointerException("jsw");
        }
        this.jsw = jsw;
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
