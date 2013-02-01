/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slc.sli.api.resources.generic.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.wadl.helpers.WadlHandler;
import org.slc.sli.modeling.wadl.helpers.WadlHelper;

/**
 * Walks a wadl and returns the resource end points
 *
 * @author srupasinghe
 *
 */
public class ResourceWadlHandler implements WadlHandler {
    private Map<String, Resource> resourceEnds = new HashMap<String, Resource>();


    private void computeURI(final Resource resource, final Resources resources, final Application app,
                                          final Stack<Resource> ancestors) {
        final List<String> steps = WadlHelper.toSteps(resource, ancestors);

        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final String step : steps) {
            if (first) {
                first = false;
            } else {
                sb.append("/");
            }
            sb.append(step);
        }

        resourceEnds.put(sb.toString(), resource);
    }

    @Override
    public void beginApplication(Application application) {
        // no op

    }

    @Override
    public void beginResource(final Resource resource, final Resources resources, final Application app,
                              final Stack<Resource> ancestors) {
        computeURI(resource, resources, app, ancestors);
    }

    @Override
    public void endApplication(Application application) {
        // no op

    }

    @Override
    public void endResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors) {
        // Ignore
    }

    @Override
    public void method(Method method, Resource resource, Resources resources, Application application,
                       Stack<Resource> ancestors) {
        // No Op
    }


    public Map<String, Resource> getResourceEnds() {
        return resourceEnds;
    }
}

