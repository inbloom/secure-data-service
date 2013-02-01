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
package org.slc.sli.api.resources.generic.response;

import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.service.ResourceAccessLog;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;

/**
 * Base response builder
 *
 * @author srupasinghe
 * @author jstokes
 * @author pghosh
 *
 */

public abstract class ResponseBuilder {

    @Autowired
    protected ResourceHelper resourceHelper;

    @Autowired
    private ResourceAccessLog resourceAccessLog;

    protected Resource constructAndCheckResource(final UriInfo uriInfo, final ResourceTemplate template,
                                                 final ResourceMethod method) {

        Resource resource = resourceHelper.getResourceName(uriInfo, template);

        //log security events
        resourceAccessLog.logAccessToRestrictedEntity(uriInfo, resource, GetResponseBuilder.class.toString());

        return resource;
    }

}
