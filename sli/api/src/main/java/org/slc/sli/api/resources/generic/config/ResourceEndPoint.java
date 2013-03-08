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

import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.api.resources.generic.FivePartResource;
import org.slc.sli.api.resources.generic.SixPartResource;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Reads in different nameSpaced resource end points and loads them
 *
 * @author srupasinghe
 */
@Component
public class ResourceEndPoint {

    private static final String BASE_RESOURCE = "org.slc.sli.api.resources.generic.DefaultResource";
    private static final String THREE_PART_RESOURCE = "org.slc.sli.api.resources.generic.ThreePartResource";
    private static final String FOUR_PART_RESOURCE = "org.slc.sli.api.resources.generic.FourPartResource";
    private static final String FIVE_PART_RESOURCE = FivePartResource.class.getName();
    private static final String SIX_PART_RESOURCE = SixPartResource.class.getName();

    private Map<String, String> resourceEndPoints = new HashMap<String, String>();

    private List<String> queryingDisallowedEndPoints = new ArrayList<String>();

    private Set<String> dateRangeDisallowedEndPoints = new LinkedHashSet<String>();

    private Set<String> blockGetRequestEndPoints = new LinkedHashSet<String>();

    private Map<String, SortedSet<String>> nameSpaceMappings = new HashMap<String, SortedSet<String>>();

    @Autowired
    private ResourceHelper resourceHelper;

    @PostConstruct
    public void load() throws IOException {
        InputStream is = getClass().getResourceAsStream("/wadl/resources.json");
        try {
            loadNameSpace(is);
        } finally {
            is.close();
        }
    }

    protected ApiNameSpace[] loadNameSpace(InputStream fileStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ApiNameSpace[] apiNameSpaces = mapper.readValue(fileStream, ApiNameSpace[].class);

        for (ApiNameSpace apiNameSpace : apiNameSpaces) {
            String[] nameSpaces = apiNameSpace.getNameSpace();

            for (String nameSpace : nameSpaces) {
                nameSpaceMappings.putAll(buildNameSpaceMappings(nameSpace, nameSpaceMappings));
                List<ResourceEndPointTemplate> resources = apiNameSpace.getResources();
                for (ResourceEndPointTemplate resource : resources) {
                    if (!resource.isQueryable()) {
                        queryingDisallowedEndPoints.add(resource.getPath().substring(1));
                    }
                    if (resource.isDateSearchDisallowed()) {
                        dateRangeDisallowedEndPoints.add(nameSpace + resource.getPath());
                    }
                    if (resource.isBlockGetRequest()) {
                        blockGetRequestEndPoints.add(nameSpace + resource.getPath());
                    }

                    if (resource.getSubResources() != null) {
                        for (ResourceEndPointTemplate subResource : resource.getSubResources()) {
                            if (subResource.isDateSearchDisallowed()) {
                                dateRangeDisallowedEndPoints.add(nameSpace + resource.getPath() + subResource.getPath());
                            }
                        }
                    }

                    resourceEndPoints.putAll(buildEndPoints(nameSpace, "", resource));
                }
            }
        }

        return apiNameSpaces;
    }

    private Map<String, SortedSet<String>> buildNameSpaceMappings(String nameSpace, Map<String, SortedSet<String>> nameSpaceMappings) {
        Map<String, SortedSet<String>> localNameSpaceMappings = new HashMap<String, SortedSet<String>>(nameSpaceMappings);
        String[] versions = nameSpace.split("\\.");

        if (localNameSpaceMappings.containsKey(versions[0])) {
            localNameSpaceMappings.get(versions[0]).add(versions[1]);
        } else {
            SortedSet<String> minorVersions = new TreeSet<String>();
            minorVersions.add(versions[1]);

            localNameSpaceMappings.put(versions[0], minorVersions);
        }

        return localNameSpaceMappings;
    }

    protected Map<String, String> buildEndPoints(String nameSpace, String resourcePath, ResourceEndPointTemplate template) {
        Map<String, String> resources = new HashMap<String, String>();
        String fullPath = nameSpace + resourcePath + template.getPath();

        resources.put(fullPath, getResourceClass("/rest/" + fullPath, template));

        List<ResourceEndPointTemplate> subResources = template.getSubResources();

        if (subResources != null) {
            for (ResourceEndPointTemplate subTemplate : subResources) {
                resources.putAll(buildEndPoints(nameSpace, resourcePath + template.getPath(), subTemplate));
            }
        }

        return resources;
    }

    protected String getResourceClass(final String resourcePath, ResourceEndPointTemplate template) {

        if (template.getResourceClass() != null) {
            return template.getResourceClass();
        }

        //use the brute force method for now, we should move to looking up this information from the class itself
        String resourceClass = bruteForceMatch(resourcePath);

        return resourceClass;
    }

    public List<String> getQueryingDisallowedEndPoints() {
        return this.queryingDisallowedEndPoints;
    }

    public Set<String> getDateRangeDisallowedEndPoints() {
        return this.dateRangeDisallowedEndPoints;
    }

    public Set<String> getBlockGetRequestEndPoints() {
        return this.blockGetRequestEndPoints;
    }

    protected String bruteForceMatch(final String resourcePath) {

        if (resourceHelper.resolveResourcePath(resourcePath, ResourceTemplate.SIX_PART)) {
            return SIX_PART_RESOURCE;
        } else if (resourceHelper.resolveResourcePath(resourcePath, ResourceTemplate.FIVE_PART)) {
            return FIVE_PART_RESOURCE;
        } else if (resourceHelper.resolveResourcePath(resourcePath, ResourceTemplate.FOUR_PART)) {
            return FOUR_PART_RESOURCE;
        } else if (resourceHelper.resolveResourcePath(resourcePath, ResourceTemplate.THREE_PART)) {
            return THREE_PART_RESOURCE;
        } else if (resourceHelper.resolveResourcePath(resourcePath, ResourceTemplate.ONE_PART)
                || resourceHelper.resolveResourcePath(resourcePath, ResourceTemplate.TWO_PART)) {
            return BASE_RESOURCE;
        }

        throw new ResourceEndPointException("Cannot resolve resource handler class");

    }

    public Map<String, String> getResources() {
        return resourceEndPoints;
    }

    public Map<String, SortedSet<String>> getNameSpaceMappings() {
        return nameSpaceMappings;
    }

    public void setNameSpaceMappings(Map<String, SortedSet<String>> nameSpaceMappings) {
        this.nameSpaceMappings = nameSpaceMappings;
    }
}
