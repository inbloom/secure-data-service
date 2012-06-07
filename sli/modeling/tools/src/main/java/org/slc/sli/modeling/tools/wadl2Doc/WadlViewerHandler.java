package org.slc.sli.modeling.tools.wadl2Doc;

import java.util.List;
import java.util.Stack;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.wadl.helpers.WadlHandler;
import org.slc.sli.modeling.wadl.helpers.WadlHelper;

public final class WadlViewerHandler implements WadlHandler {

    @Override
    public void beginResource(final Resource resource, final Resources resources, final Application app,
            final Stack<Resource> ancestors) {
        final String uri = computeURI(resource, resources, app, ancestors);
        System.out.println(uri);
    }

    @Override
    public void endResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors) {
        // Ignore
    }

    @Override
    public void beginMethod(Method method, Resource resource, Resources resources, Application app,
            Stack<Resource> ancestors) {
        // Ignore
    }

    @Override
    public void endMethod(Method method, Resource resource, Resources resources, Application app,
            Stack<Resource> ancestors) {
        // Ignore
    }

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
}
