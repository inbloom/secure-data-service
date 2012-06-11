package org.slc.sli.modeling.sdkgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.rest.helpers.RestHelper;
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
                    jsw.write("public ");
                    jsw.write("List<Entity> " + method.getId());
                    jsw.write("(");
                    final List<Param> templateParams = RestHelper.computeRequestTemplateParams(resource, ancestors);
                    final List<JavaParam> params = Level2ClientJavaHelper.computeJavaRequestParams(templateParams);
                    jsw.writeParams(params);
                    jsw.write(") throws IOException, SLIDataStoreException");
                    jsw.beginBlock();
                    try {
                        jsw.write("try");
                        jsw.beginBlock();
                        final String uri = computeURI(resource, resources, application, ancestors);
                        writeURLString(uri, templateParams);
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
     * Writes a statement that declares a string with substitution parameters.
     *
     * @param uri
     *            The URI computed from the WADL with curly braces for template parameters.
     * @param templateParams
     *            The template parameters from left to right.
     */
    private void writeURLString(final String uri, final List<Param> templateParams) throws IOException {
        // FIXME: This URI does not have a substitution for the template parameter.
        final String uriFormatString = computeURIFormatString(uri, templateParams);

        jsw.beginStmt();
        jsw.write("final String url = String.format");
        jsw.parenL();
        jsw.dblQte().write(uriFormatString).dblQte();
        for (final Param templateParam : templateParams) {
            jsw.write(", ");
            jsw.write(templateParam.getName());
        }
        jsw.parenR();

        jsw.endStmt();
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

    private static final String computeURIFormatString(final String uri, final List<Param> templateParams) {
        String result = uri;
        for (final Param templateParam : templateParams) {
            result = result.replace("{" + templateParam.getName() + "}", "%s");
        }
        return result;
    }
}
