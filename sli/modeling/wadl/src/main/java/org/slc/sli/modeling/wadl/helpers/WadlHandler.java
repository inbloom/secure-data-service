package org.slc.sli.modeling.wadl.helpers;

import java.util.Stack;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;

public interface WadlHandler {

    void beginResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors);

    void endResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors);

    void beginMethod(Method method, Resource resource, Resources resources, Application app, Stack<Resource> ancestors);

    void endMethod(Method method, Resource resource, Resources resources, Application app, Stack<Resource> ancestors);
}
