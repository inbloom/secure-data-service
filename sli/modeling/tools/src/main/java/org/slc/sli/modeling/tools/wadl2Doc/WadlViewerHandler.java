package org.slc.sli.modeling.tools.wadl2Doc;

import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

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

public final class WadlViewerHandler implements WadlHandler {

    public static final String computeURI(final Resource resource, final Resources resources, final Application app,
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

    @Override
    public void beginApplication(Application application) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beginResource(final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors) {
        final String uri = computeURI(resource, resources, app, ancestors);
        System.out.println(uri);
    }

    @Override
    public void endApplication(Application application) {
        // TODO Auto-generated method stub

    }

    @Override
    public void endResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors) {
        // Ignore
    }

    @Override
    public void method(Method method, Resource resource, Resources resources, Application application,
            Stack<Resource> ancestors) {
        @SuppressWarnings("unused")
        // Perhaps modify this method to generate a different naming scheme?
        final String id = WadlHelper.computeId(method, resource, resources, application, ancestors);

        final Request request = method.getRequest();
        for (final Param param : request.getParams()) {

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
    }
}
