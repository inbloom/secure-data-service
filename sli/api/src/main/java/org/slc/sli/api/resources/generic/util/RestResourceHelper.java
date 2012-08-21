package org.slc.sli.api.resources.generic.util;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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

    public static final Map<String, String> REST_RESOURCE_NAME_MAPPING = new HashMap<String, String>();
    static {
        REST_RESOURCE_NAME_MAPPING.put("staffEducationOrgAssignmentAssociations", ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS);
        REST_RESOURCE_NAME_MAPPING.put("studentAssessments",ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS);
    }
    @Autowired
    private EntityDefinitionStore entityDefinitionStore;

    @Override
    public Resource getResourceName(final UriInfo uriInfo, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uriInfo, template);
        return getResource(RESOURCE_KEY,matchList);
    }


    @Override
    public Resource getBaseName(final UriInfo uriInfo, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uriInfo, template);
        return getResource(BASE_KEY,matchList);
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
                // TODO
                return "";
            default:
                throw new AssertionError("Non-valid template");
        }
    }

    @Override
    public Resource getAssociationName(final UriInfo uriInfo, final ResourceTemplate template) {
        final Map<String, String> matchList = getMatchList(uriInfo, template);
        return getResource(ASSOCIATION_KEY,matchList);
    }
    @Override
    public EntityDefinition getEntityDefinition(final Resource resource) {
        return getEntityDefinition(resource.getResourceType());
    }
    @Override
    public EntityDefinition getEntityDefinition(String resource) {
        EntityDefinition definition = entityDefinitionStore.lookupByResourceName(resource);
        if (definition == null) {
            definition = entityDefinitionStore.lookupByResourceName(REST_RESOURCE_NAME_MAPPING.get(resource));
        }
        return definition;
    }

    private Map<String, String> getMatchList(final UriInfo uriInfo, final ResourceTemplate template) {
        final UriTemplate uriTemplate = new UriTemplate(template.getTemplate());
        return uriTemplate.match(uriInfo.getRequestUri().getPath());
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

    private Resource getResource(final String resourceType,final Map<String, String> matchList) {
        String namespace = matchList.get(VERSION_KEY);
        String type =  matchList.get(resourceType);
        return new Resource(namespace, type);

    }
}
