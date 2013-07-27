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
package org.slc.sli.api.resources.generic;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.util.SecurityUtil;

/**
 *
 * @author jstokes
 * @author pghosh
 * @author srupasinghe
 *
 */
@Component
@Scope("request")
public class FourPartResource extends GenericResource {

    private static final String OTHER = "other";
    private static final String BASE_KEY = "base";
    private static final String VERSION_KEY = "version";
    private static final String ASSOCIATION_KEY = "association";

    @GET
    public Response get(@Context final UriInfo uriInfo,
                        @PathParam("id") final String id) {
        final FourPartResource thisResource = this;

        return getAllResponseBuilder.build(uriInfo, ResourceTemplate.FOUR_PART, ResourceMethod.GET, new GetResourceLogic() {
            @Override
            public ServiceResponse run(Resource resource) {
                final Resource base = resourceHelper.getBaseName(uriInfo, ResourceTemplate.FOUR_PART);
                final Resource association = resourceHelper.getAssociationName(uriInfo, ResourceTemplate.FOUR_PART);
                ServiceResponse response = resourceService.getEntities(base, id, association, resource, uriInfo.getRequestUri());
                if (SecurityUtil.isDualContext()) {
                    response = resourceHelper.getEntitiesForOtherUserContext(uriInfo, response, thisResource);
                    SecurityUtil.setDualContext(false);
                }

                return response;
//                return resourceService.getEntities(base, id, association, resource, uriInfo.getRequestUri());
            }
        });

    }

    public Resource getBaseName(final URI uri, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uri, template);
        return getResource(BASE_KEY, matchList);
    }

    public Resource getAssociationName(final URI uri, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uri, template);
        return getResource(ASSOCIATION_KEY, matchList);
    }

    private Map<String, String> getMatchList(final URI uri, final ResourceTemplate template) {
        final UriTemplate uriTemplate = new UriTemplate(template.getTemplate());
        String path = uri.getPath();
        if (path.endsWith("/")) {
            path = path.substring(0, (path.length() - 1));
        }
        return uriTemplate.match(path);
    }

    private Resource getResource(final String resourceType, final Map<String, String> matchList) {
        String namespace = matchList.get(VERSION_KEY);
        String type =  matchList.get(resourceType);
        return new Resource(namespace, type);
    }
}
