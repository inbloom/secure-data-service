package org.slc.sli.modeling.sdkgen;

import java.io.IOException;
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
public final class Level2ClientInterfaceWriter implements WadlHandler {

    private final String packageName;
    private final String className;
    private final JavaStreamWriter jsw;

    public Level2ClientInterfaceWriter(final String packageName, final String className, final JavaStreamWriter jsw) {
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
        this.jsw = jsw;
    }

    @Override
    public void beginApplication(final Application application) {
        try {
            jsw.writePackage(packageName);
            jsw.writeImport("java.io.IOException");
            jsw.writeImport("java.util.List");
            jsw.writeImport("java.util.Map");
            jsw.writeImport("org.slc.sli.api.client.Entity");
            jsw.beginInterface(className);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void method(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) {
        try {
            if (Method.NAME_HTTP_GET.equals(method.getName())) {
                jsw.writeComment(method.getId());
                jsw.beginStmt();
                try {
                    jsw.write("List<Entity> " + method.getId());
                    jsw.write("(");
                    final List<Param> templateParams = RestHelper.computeRequestTemplateParams(resource, ancestors);
                    final List<JavaParam> params = Level2ClientJavaHelper.computeJavaRequestParams(templateParams);
                    jsw.writeParams(params);
                    jsw.write(") throws IOException, SLIDataStoreException");
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
                                @SuppressWarnings("unused")
                                final QName elementName = representation.getElement();
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
