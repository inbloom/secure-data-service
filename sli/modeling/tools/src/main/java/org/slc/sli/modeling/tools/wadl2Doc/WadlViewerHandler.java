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

package org.slc.sli.modeling.tools.wadl2Doc;

import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.wadl.helpers.WadlHandler;
import org.slc.sli.modeling.wadl.helpers.WadlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Stack;

/**
 * Methods for verifying a WADL
 */
public final class WadlViewerHandler implements WadlHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WadlViewerHandler.class);

    public static final String computeURI(final Resource resource, final Resources resources, final Application app,
                                          final Stack<Resource> ancestors) {
        final List<String> steps = WadlHelper.toSteps(resource, ancestors);

        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final String step : steps) {
            if (!WadlHelper.isVersion(step)) {
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
        // no op

    }

    @Override
    public void beginResource(final Resource resource, final Resources resources, final Application app,
                              final Stack<Resource> ancestors) {
        final String uri = computeURI(resource, resources, app, ancestors);
        LOG.info(uri);
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
        @SuppressWarnings("unused")
        // Perhaps modify this method to generate a different naming scheme?
        final String id = WadlHelper.computeId(method, resource, resources, application, ancestors);

        @SuppressWarnings("unused")
        final Request request = method.getRequest();
//        for (@SuppressWarnings("unused")
//        final Param param : request.getParams()) {
//
//        }

        final List<Response> responses = method.getResponses();
        for (final Response response : responses) {

            final List<Representation> representations = response.getRepresentations();
            for (final Representation representation : representations) {
                @SuppressWarnings("unused")
                final QName elementName = representation.getElementName();
            }
        }
    }
}
