package org.slc.sli.modeling.wadl.helpers;

import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;

public final class WadlWalker {

    private final WadlHandler handler;

    public WadlWalker(final WadlHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        this.handler = handler;
    }

    public void walk(final Application application) {
        if (application == null) {
            throw new NullPointerException("application");
        }
        final Resources resources = application.getResources();
        final Stack<Resource> ancestors = new Stack<Resource>();
        for (final Resource resource : resources.getResources()) {
            walkResource(resource, resources, application, ancestors);
        }
    }

    private void walkResource(final Resource resource, final Resources resources, final Application application,
            final Stack<Resource> ancestors) {
        handler.beginResource(resource, resources, application, ancestors);
        try {
            for (final Method method : resource.getMethods()) {
                walkMethod(method, resource, resources, application, ancestors);
            }
            ancestors.push(resource);
            try {
                for (final Resource childResource : resource.getResources()) {
                    walkResource(childResource, resources, application, ancestors);
                }
            } finally {
                ancestors.pop();
            }
        } finally {
            handler.endResource(resource, resources, application, ancestors);
        }
    }

    private void walkMethod(final Method method, final Resource resource, final Resources resources,
            final Application application, final Stack<Resource> ancestors) {
        handler.beginMethod(method, resource, resources, application, ancestors);
        try {
            @SuppressWarnings("unused")
            // Perhaps modify this method to generate a different naming scheme?
            final String id = WadlHelper.computeId(method, resource, resources, application, ancestors);

            @SuppressWarnings("unused")
            final Request request = method.getRequest();

            final List<Response> responses = method.getResponses();
            for (final Response response : responses) {
                final List<Representation> representations = response.getRepresentations();
                for (final Representation representation : representations) {
                    @SuppressWarnings("unused")
                    final QName elementName = representation.getElement();
                }
            }
        } finally {
            handler.endMethod(method, resource, resources, application, ancestors);
        }
    }
}
