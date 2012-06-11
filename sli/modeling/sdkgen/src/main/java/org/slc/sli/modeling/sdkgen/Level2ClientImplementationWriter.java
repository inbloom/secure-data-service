package org.slc.sli.modeling.sdkgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.wadl.helpers.WadlHandler;
import org.slc.sli.modeling.wadl.helpers.WadlHelper;

/**
 * Writes the implementation for the Level 2 Client SDK.
 */
public final class Level2ClientImplementationWriter implements WadlHandler {

    private final String packageName;
    private final String className;
    private final List<String> interfaces;
    private final JavaStreamWriter jsw;

    public Level2ClientImplementationWriter(final String packageName, final String className,
            final List<String> interfaces, final JavaStreamWriter jsw) {
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
        this.interfaces = Collections.unmodifiableList(new ArrayList<String>(interfaces));
        this.jsw = jsw;
    }

    @Override
    public void beginApplication(final Application application) {
        try {
            jsw.writePackage(packageName);
            jsw.writeImport("java.io.IOException");
            jsw.writeImport("java.net.URISyntaxException");
            jsw.writeImport("java.net.URL");
            jsw.writeImport("java.util.ArrayList");
            jsw.writeImport("java.util.Collections");
            jsw.writeImport("java.util.List");
            jsw.writeImport("org.slc.sli.api.client.Entity");
            jsw.beginClass(className, interfaces);
            // Attributes
            jsw.writeAttribute("client", "Level1Client");
            // Write Initializer
            jsw.write("public " + className + "(final Level1Client client)");
            jsw.beginBlock();
            jsw.beginStmt().write("this.client = client").endStmt();
            jsw.endBlock();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void method(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) {
        try {
            if (Method.NAME_HTTP_GET.equals(method.getName())) {
                @SuppressWarnings("unused")
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
                            @SuppressWarnings("unused")
                            final QName elementName = representation.getElement();
                        }
                    } finally {
                    }
                }
                jsw.writeComment(method.getId());
                jsw.writeOverride();
                jsw.beginStmt();
                try {
                    // FIXME: This method call does not have any parameters.
                    jsw.write("public List<Entity> " + method.getId()
                            + "(final String token) throws IOException, SLIDataStoreException");
                    jsw.beginBlock();
                    try {
                        jsw.write("try");
                        jsw.beginBlock();
                        // FIXME: This URI does not have a substitution for the template parameter.
                        final String uri = computeURI(resource, resources, application, ancestors);

                        jsw.beginStmt().write("final String url = ").dblQte().write(uri).dblQte().endStmt();
                        jsw.beginStmt().write("return client.getRequest(token, new URL(url))").endStmt();
                        jsw.endBlock();
                        jsw.write("catch(final URISyntaxException e)");
                        jsw.beginBlock();
                        jsw.beginStmt().write("throw new AssertionError(e)").endStmt();
                        jsw.endBlock();
                    } finally {
                        jsw.endBlock();
                    }
                } finally {
                    jsw.endStmt();
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    // try {
    // return client.getRequest(token, new URL("TODO"));
    // } catch (URISyntaxException e) {
    // throw new AssertionError(e);
    // }

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

    /**
     * Computes the URI for the specified resource and its ancestors.
     */
    private static final String computeURI(final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors) {
        final List<String> steps = WadlHelper.toSteps(resource, ancestors);

        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final String step : steps) {
            if (WadlHelper.isVersion(step)) {
                // Ignore
            } else {
                if (first) {
                    first = false;
                } else {
                    sb.append("/");
                }
                sb.append(step);
            }
        }
        return sb.toString();
    }
}
