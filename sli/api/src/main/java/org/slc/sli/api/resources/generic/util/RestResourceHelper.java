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

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;
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

    @Override
    public Resource getResourceName(final UriInfo uriInfo, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uriInfo, template);
        return getResource(RESOURCE_KEY, matchList);
    }


    @Override
    public Resource getBaseName(final UriInfo uriInfo, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uriInfo, template);
        return getResource(BASE_KEY, matchList);
    }

    @Override
    public String getResourcePath(final UriInfo uriInfo, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uriInfo, template);
        final String path = matchList.get(VERSION_KEY) + SEP + matchList.get(RESOURCE_KEY);

        switch (template) {
            case ONE_PART:
                return path;
            case TWO_PART:
                return getTwoPartPath(path);
            case THREE_PART:
                return getThreePartPath(matchList);
            case FOUR_PART:
                return getFourPartPath(matchList);
            case CUSTOM:
                return "";
            default:
                throw new AssertionError("Non-valid template");
        }
    }

    @Override
    public Resource getAssociationName(final UriInfo uriInfo, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uriInfo, template);
        return getResource(ASSOCIATION_KEY, matchList);
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

    private Map<String, String> getMatchList(final UriInfo uriInfo, final ResourceTemplate template) {
        final UriTemplate uriTemplate = new UriTemplate(template.getTemplate());
        String path = uriInfo.getRequestUri().getPath();
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
}
