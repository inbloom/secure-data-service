/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
package org.slc.sli.api.resources.generic.response;

import org.slc.sli.api.resources.generic.MethodNotAllowedException;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.service.ResourceAccessLog;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.Set;

/**
 * Base response builder
 *
 * @author srupasinghe
 */

public abstract class ResponseBuilder {

    @Autowired
    protected ResourceHelper resourceHelper;

    @Autowired
    private ResourceAccessLog resourceAccessLog;

    @javax.annotation.Resource(name = "resourceSupportedMethods")
    private Map<String, Set<String>> resourceSupportedMethods;

    protected Resource constructAndCheckResource(final UriInfo uriInfo, final ResourceTemplate template,
                                                 final ResourceMethod method) {
        final String resourcePath = resourceHelper.getResourcePath(uriInfo, template);
        Resource resource = resourceHelper.getResourceName(uriInfo, template);

        Set<String> values = resourceSupportedMethods.get(resourcePath);
        if (!values.contains(method.getMethod())) {
            throw new MethodNotAllowedException(values);
        }

        //log security events
        resourceAccessLog.logAccessToRestrictedEntity(uriInfo, resource, GetResponseBuilder.class.toString());

        return resource;
    }

}
