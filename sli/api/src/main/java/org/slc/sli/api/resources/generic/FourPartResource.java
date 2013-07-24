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
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import org.slc.sli.api.jersey.PostProcessFilter;
import org.slc.sli.api.jersey.PreProcessFilter;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.enums.Right;

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

    @Autowired
    private PreProcessFilter preFilter;

    @Autowired
    private PostProcessFilter postFilter;

    @GET
    public Response get(@Context final UriInfo uriInfo,
                        @PathParam("id") final String id) {

        return getAllResponseBuilder.build(uriInfo, ResourceTemplate.FOUR_PART, ResourceMethod.GET, new GetResourceLogic() {
            @Override
            public ServiceResponse run(Resource resource) {
                // Split request into staff and teacher context requests, if needed.
                List<String> contexts = new LinkedList<String>();
                SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                Collection<GrantedAuthority> rights = principal.getAllRights();
                if (rights.contains(Right.STAFF_CONTEXT) && rights.contains(Right.TEACHER_CONTEXT)) {
                    if (principal.getEntity().getType().equals(EntityNames.STAFF)) {
                        contexts.add(EntityNames.STAFF);
                        contexts.add(EntityNames.TEACHER);
                    } else {  // TEACHER
                        contexts.add(EntityNames.TEACHER);
                        contexts.add(EntityNames.STAFF);
                    }
                } else {
                    contexts.add(OTHER);
                }

                List<EntityBody> entityBodyList = new LinkedList<EntityBody>();
                ContainerRequest request = SecurityUtil.getLastRequest();
                for (String context : contexts) {
                    // Construct context-specific URIs, if needed.
                    SecurityUtil.setContext(context);
                    URI requestUri = uriInfo.getRequestUri();
                    if ((!context.equals(OTHER)) && (!context.equals(principal.getEntity().getType()))) {
                        try {
                            requestUri = new URI(request.getBaseUri().toString() + ((String) request.getProperties().get("original-request")));
                        } catch (URISyntaxException e) {
                            // TODO Fix this!
                            requestUri = uriInfo.getRequestUri();
                        }
                        request.setUris(request.getBaseUri(), requestUri);
                        preFilter.filter(request);
                        requestUri = request.getRequestUri();
                    }

                    String queryId = request.getPathSegments().get(2).getPath();
                    final Resource base = getBaseName(requestUri, ResourceTemplate.FOUR_PART);
                    final Resource association = getAssociationName(requestUri, ResourceTemplate.FOUR_PART);
                    ServiceResponse contextResponse = resourceService.getEntities(base, queryId, association, resource, requestUri);
                    if (entityBodyList.isEmpty()) {
                        entityBodyList.addAll(contextResponse.getEntityBodyList());
                    } else {
                        for (EntityBody respEntityBody : contextResponse.getEntityBodyList()) {
                            EntityBody entityBody = getMatchingRecord(respEntityBody, entityBodyList);
                            if (entityBody != null) {
                                entityBody = (EntityBody) mapMerge(entityBody, respEntityBody);
                            } else {
                                entityBodyList.add(respEntityBody);
                            }
                        }
                    }

                    if ((!context.equals(OTHER)) && (context.equals(principal.getEntity().getType()))) {
                        ContainerResponse response = new ContainerResponse(null, request, null);
                        response.setStatus(200);
                        response.getHttpHeaders().add("TotalCount", contextResponse.getEntityCount());
                        response.getHttpHeaders().add("userContext", context);
                        postFilter.filter(request, response);
                    }
                }

                ServiceResponse combinedResponse = new ServiceResponse(entityBodyList, entityBodyList.size());

                return combinedResponse;

//                final Resource base = resourceHelper.getBaseName(uriInfo, ResourceTemplate.FOUR_PART);
//                final Resource association = resourceHelper.getAssociationName(uriInfo, ResourceTemplate.FOUR_PART);
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

    @SuppressWarnings("unchecked")
    private Map<String, Object> mapMerge(Map<String, Object> original, final Map<String, Object> newMap) {
        for (String key : newMap.keySet()) {
            if (!original.containsKey(key)) {
                original.put(key, newMap.get(key));
            }
            else if (newMap.get(key) instanceof Map && original.get(key) instanceof Map) {
                Map<String, Object> originalChild = (Map<String, Object>) original.get(key);
                Map<String, Object> newChild = (Map<String, Object>) newMap.get(key);
                original.put(key, mapMerge(originalChild, newChild));
            }
        }
        return original;
    }

    private EntityBody getMatchingRecord(final EntityBody searchEntityBody, final List<EntityBody> entityBodyList) {
        for (EntityBody entityBody : entityBodyList) {
            if (searchEntityBody.get("id").equals(entityBody.get("id"))) {
                return entityBody;
            }
        }

        return null;
    }
}
