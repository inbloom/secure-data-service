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

import org.slc.sli.api.resources.generic.GenericResource;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Builds a default response. Used for other operations than GET.
 *
 * @author srupasinghe
 * @author jstokes
 * @author pghosh
 *
 */

@Scope("request")
@Component
public class DefaultResponseBuilder extends ResponseBuilder {

    public Response build(final UriInfo uriInfo, final ResourceTemplate template,
                          final ResourceMethod method, final GenericResource.ResourceLogic logic) {

        //get the resource container
        Resource resource = constructAndCheckResource(uriInfo, template, method);

        return logic.run(resource);
    }
}
