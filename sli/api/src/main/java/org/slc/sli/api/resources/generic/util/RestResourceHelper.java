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
package org.slc.sli.api.resources.generic.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.jersey.PostProcessFilter;
import org.slc.sli.api.jersey.PreProcessFilter;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.GenericResource;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
/**
 * @author jstokes
 */
@Component
public class RestResourceHelper implements ResourceHelper {
    private static final String RESOURCE_KEY = "resource";
    private static final String BASE_KEY = "base";
    private static final String VERSION_KEY = "version";
    private static final String ASSOCIATION_KEY = "association";
    private static final String SEP = "/";

    @Autowired
    private EntityDefinitionStore entityDefinitionStore;

    @Autowired
    private PreProcessFilter preFilter;

    @Autowired
    private PostProcessFilter postFilter;

    @Autowired
    @Qualifier("defaultResourceService")
    private ResourceService resourceService;

    @Override
    public Resource getResourceName(final UriInfo uriInfo, final ResourceTemplate template) {
        return getResourceName(uriInfo.getRequestUri(), template);
    }

    @Override
    public Resource getBaseName(final UriInfo uriInfo, final ResourceTemplate template) {
        return getBaseName(uriInfo.getRequestUri(), template);
    }

    @Override
    public String getResourcePath(final UriInfo uriInfo, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uriInfo.getRequestUri(), template);
        final String path = matchList.get(VERSION_KEY) + SEP + matchList.get(RESOURCE_KEY);

        switch (template) {
            case THREE_PART:
                return getThreePartPath(matchList);
            case FOUR_PART:
                return getFourPartPath(matchList);
            case FIVE_PART:
                return getThreePartPath(matchList);
            case SIX_PART:
                return getFourPartPath(matchList);
            case CUSTOM:
                return "";
            default:
                throw new AssertionError("Non-valid template");
        }
    }

    @Override
    public Resource getAssociationName(final UriInfo uriInfo, final ResourceTemplate template) {
        return getAssociationName(uriInfo.getRequestUri(), template);
    }
    @Override
    public EntityDefinition getEntityDefinition(final Resource resource) {
        return getEntityDefinition(resource.getResourceType());
    }

    @Override
    public EntityDefinition getEntityDefinition(String resource) {
        EntityDefinition definition = entityDefinitionStore.lookupByResourceName(resource);

        return definition;
    }

    @Override
    public EntityDefinition getEntityDefinitionByType(String type) {
        EntityDefinition definition = entityDefinitionStore.lookupByEntityType(type);

        return definition;
    }

    @Override
    public boolean resolveResourcePath(final String uri, final ResourceTemplate template) {
        final UriTemplate uriTemplate = new UriTemplate(template.getTemplate());

        return uriTemplate.matches(uri);
    }

    @Override
    public String extractVersion(List<PathSegment> segments) {
        if (!segments.isEmpty()) {
            return segments.get(0).getPath();
        }

        return PathConstants.V1;
    }

    private Resource getBaseName(final URI uri, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uri, template);
        return getResource(BASE_KEY, matchList);
    }

    private Resource getAssociationName(final URI uri, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uri, template);
        return getResource(ASSOCIATION_KEY, matchList);
    }

    private Resource getResourceName(final URI uri, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uri, template);
        return getResource(RESOURCE_KEY, matchList);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ServiceResponse getEntitiesForOtherUserContext(final UriInfo uriInfo, final ServiceResponse firstResponse, GenericResource caller) {
        // First, make call to PostProcess filter for first call.
        ContainerRequest request = SecurityUtil.getLastRequest();
        ContainerResponse response = new ContainerResponse(null, request, null);
        response.setStatus(200);
        response.getHttpHeaders().add("TotalCount", firstResponse.getEntityCount());
        response.getHttpHeaders().add("userContext", SecurityUtil.getContext());
        postFilter.filter(request, response);

        // Next, make call to PreProcess filter for second call.
        String newContext = (SecurityUtil.getContext() == EntityNames.STAFF) ? EntityNames.TEACHER : EntityNames.STAFF;
        SecurityUtil.setContext(newContext);
        URI requestUri = uriInfo.getRequestUri();
        try {
           String queryString = "";
           if (null != request.getRequestUri().getQuery()) {
               queryString = "?" + request.getRequestUri().getQuery();
           }
           requestUri = new URI(request.getBaseUri().toString() + ((String) request.getProperties().get("original-request")) + queryString);
       } catch (URISyntaxException e) {
           // TODO Fix this!
           requestUri = uriInfo.getRequestUri();
       }
        request.setUris(request.getBaseUri(), requestUri);
        preFilter.filter(request);
        requestUri = request.getRequestUri();

        // Get entities for other staff member context.
        SecurityUtil.setDualContext(false);
        String id;
        Resource base;
        Resource association;
        Resource resource;
        String ids;
        List<String> segments;
        String newUri;
        String queryString;
        ServiceResponse secondResponse;
        try {
        switch (request.getPathSegments().size()) {
        case 3:
            id = request.getPathSegments().get(2).getPath();
            resource = getResourceName(requestUri, ResourceTemplate.TWO_PART);
            secondResponse = resourceService.getEntitiesByIds(resource, id, requestUri);
        break;
        case 4:
            id = request.getPathSegments().get(2).getPath();
            base = getBaseName(requestUri, ResourceTemplate.THREE_PART);
            resource = getResourceName(requestUri, ResourceTemplate.THREE_PART);
            secondResponse = resourceService.getEntities(base, id, resource, requestUri);
        break;
        case 5:
            id = request.getPathSegments().get(2).getPath();
            association = getAssociationName(requestUri, ResourceTemplate.FOUR_PART);
            base = getBaseName(requestUri, ResourceTemplate.FOUR_PART);
            resource = getResourceName(requestUri, ResourceTemplate.FOUR_PART);
            secondResponse = resourceService.getEntities(base, id, association, resource, uriInfo.getRequestUri());
        break;
        case 6:
            ids = caller.locateIds(uriInfo, ResourceTemplate.FIVE_PART);
            segments = caller.extractSegments(uriInfo.getPathSegments(), Arrays.asList(0, 4, 5));
            segments.add(2, ids);
            newUri = String.format("/rest/%s/%s/%s/%s", segments.toArray());
            queryString = caller.getEncodedQueryParameters(requestUri.getQuery());
            requestUri = new ChangedUriInfo(newUri + queryString, uriInfo).getRequestUri();
            id = request.getPathSegments().get(1).getPath();
            base = getBaseName(requestUri, ResourceTemplate.THREE_PART);
            resource = getResourceName(requestUri, ResourceTemplate.THREE_PART);
            secondResponse = resourceService.getEntities(base, id, resource, requestUri);
        break;
        case 7:
            ids = caller.locateIds(uriInfo, ResourceTemplate.SIX_PART);
            segments = caller.extractSegments(uriInfo.getPathSegments(), Arrays.asList(0, 4, 5, 6));
            segments.add(2, ids);
            newUri = String.format("/rest/%s/%s/%s/%s/%s", segments.toArray());
            queryString = caller.getEncodedQueryParameters(uriInfo.getRequestUri().getQuery());
            requestUri = new ChangedUriInfo(newUri + queryString, uriInfo).getRequestUri();
            id = request.getPathSegments().get(2).getPath();
            association = getAssociationName(requestUri, ResourceTemplate.FOUR_PART);
            base = getBaseName(requestUri, ResourceTemplate.FOUR_PART);
            resource = getResourceName(requestUri, ResourceTemplate.FOUR_PART);
            secondResponse = resourceService.getEntities(base, id, resource, requestUri);;
        break;
        default:
            throw new AssertionError("Non-valid template");
        }
        } catch (Exception e) {
            // Use blank response.
            secondResponse = new ServiceResponse(new LinkedList<EntityBody>(), 0);
        }

        // Now, return the combined results.
        List<EntityBody> entityBodyList = firstResponse.getEntityBodyList();
        if (entityBodyList.isEmpty()) {
            entityBodyList.addAll(secondResponse.getEntityBodyList());
        } else {
            for (EntityBody respEntityBody : secondResponse.getEntityBodyList()) {
                EntityBody entityBody = getMatchingRecord(respEntityBody, entityBodyList);
                if (entityBody != null) {
                    entityBody = (EntityBody) mapMerge(entityBody, respEntityBody);
                } else {
                    entityBodyList.add(respEntityBody);
                }
            }
        }

        ServiceResponse combinedResponse = new ServiceResponse(entityBodyList, entityBodyList.size());

        return combinedResponse;
    }

    private Map<String, String> getMatchList(final URI uri, final ResourceTemplate template) {
        final UriTemplate uriTemplate = new UriTemplate(template.getTemplate());
        String path = uri.getPath();
        if (path.endsWith("/")) {
            path = path.substring(0, (path.length() - 1));
        }
        return uriTemplate.match(path);
    }

    private String getFourPartPath(final Map<String, String> matchList) {
        return matchList.get(VERSION_KEY) + SEP + matchList.get(BASE_KEY) + SEP + "{id}"
                + SEP + matchList.get(ASSOCIATION_KEY) + SEP + matchList.get(RESOURCE_KEY);
    }

    private String getTwoPartPath(final String path) {
        return path + SEP + "{id}";
    }

    private String getThreePartPath(final Map<String, String> matchList) {
        return matchList.get(VERSION_KEY) + SEP + matchList.get(BASE_KEY)
                + SEP + "{id}" + SEP + matchList.get(RESOURCE_KEY);
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
