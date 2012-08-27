package org.slc.sli.api.resources.generic.config;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads in different nameSpaced resource end points and loads them
 *
 * @author srupasinghe
 *
 */
@Component
public class ResourceEndPoint {

    private static final String BASE_RESOURCE = "org.slc.sli.api.resources.generic.OnePartResource";
    private static final String THREE_PART_RESOURCE = "org.slc.sli.api.resources.generic.ThreePartResource";
    private static final String FOUR_PART_RESOURCE = "org.slc.sli.api.resources.generic.FourPartResource";

    private Map<String, String> resources = new HashMap<String, String>();

    @Autowired
    private ResourceHelper resourceHelper;

    @PostConstruct
    public void load() throws IOException {
        String fileAsString = IOUtils.toString(super.getClass().getResourceAsStream("/wadl/v1_resources.json"));

        ObjectMapper mapper = new ObjectMapper();

        ApiNameSpace apiNameSpace = mapper.readValue(fileAsString, ApiNameSpace.class);
        String nameSpace = apiNameSpace.getNameSpace();

        List<ResourceEndPointTemplate> resources = apiNameSpace.getResources();
        for (ResourceEndPointTemplate resource : resources) {
            //addIDResource(resource);
            buildEndPoints(nameSpace, "", resource);
        }
    }

    protected void addIDResource(ResourceEndPointTemplate template) {
        List<ResourceEndPointTemplate> subResources = template.getSubResources();
        ResourceEndPointTemplate idTemplate = new ResourceEndPointTemplate();
        idTemplate.setPath("/{id}");

        if (subResources != null) {
            subResources.add(idTemplate);
        } else {
            List<ResourceEndPointTemplate> subTemplates = new ArrayList<ResourceEndPointTemplate>();
            subTemplates.add(idTemplate);

            template.setSubResources(subTemplates);
        }
    }

    protected void buildEndPoints(String nameSpace, String resourcePath, ResourceEndPointTemplate template) {
        String fullPath =  nameSpace + resourcePath + template.getPath();

        resources.put(fullPath, getResourceClass("/rest/" + fullPath, template));

        List<ResourceEndPointTemplate> subResources = template.getSubResources();

        if (subResources != null) {
            for (ResourceEndPointTemplate subTemplate : subResources) {
                buildEndPoints(nameSpace, resourcePath + template.getPath(), subTemplate);
            }
        }
    }

    protected String getResourceClass(final String resourcePath, ResourceEndPointTemplate template) {

        if (template.getResourceClass() != null) {
            return template.getResourceClass();
        }

        //use the brute force method for now, we should move to looking up this information from the class itself
        String resourceClass = bruteForceMatch(resourcePath);

        if (resourceClass == null) {
            throw new RuntimeException("Cannot resolve resource handler class");
        }

        return resourceClass;
    }

    protected String bruteForceMatch(final String resourcePath) {

        if (resourceHelper.resolveResourcePath(resourcePath, ResourceTemplate.FOUR_PART)) {
            return FOUR_PART_RESOURCE;
        } else if (resourceHelper.resolveResourcePath(resourcePath, ResourceTemplate.THREE_PART)) {
            return THREE_PART_RESOURCE;
        }else if (resourceHelper.resolveResourcePath(resourcePath, ResourceTemplate.ONE_PART) ||
            resourceHelper.resolveResourcePath(resourcePath, ResourceTemplate.TWO_PART)) {
            return BASE_RESOURCE;
        }

        return null;
    }

    public Map<String, String> getResources() {
        return resources;
    }
}
