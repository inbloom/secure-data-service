package org.slc.sli.modeling.wadl.helpers;

import java.util.Stack;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;

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
        handler.beginApplication(application);
        try {
            final Resources resources = application.getResources();
            final Stack<Resource> ancestors = new Stack<Resource>();
            for (final Resource resource : resources.getResources()) {
                walkResource(resource, resources, application, ancestors);
            }
        } finally {
            handler.endApplication(application);
        }
    }

    private void walkResource(final Resource resource, final Resources resources, final Application application,
            final Stack<Resource> ancestors) {
        handler.beginResource(resource, resources, application, ancestors);
        try {
            for (final Method method : resource.getMethods()) {
                handler.method(method, resource, resources, application, ancestors);
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
}
