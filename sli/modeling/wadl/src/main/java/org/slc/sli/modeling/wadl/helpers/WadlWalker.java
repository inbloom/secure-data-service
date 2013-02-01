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

package org.slc.sli.modeling.wadl.helpers;

import java.util.Stack;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;

/**
 * Iterates through the resources of a wadl, calling the appropriate handler methods.
 * 
 * @author dholmes
 *
 */
public final class WadlWalker {

    private final WadlHandler handler;

    public WadlWalker(final WadlHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler");
        }
        this.handler = handler;
    }

    public void walk(final Application application) {
        if (application == null) {
            throw new IllegalArgumentException("application");
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
