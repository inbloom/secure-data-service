package org.slc.sli.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Common class for constants for the resources
 *
 * @author srupasinghe
 *
 */
public class ResourceConstants {

    public static final String SELF = "self";
    public static final String CUSTOM = "custom";
    public static final String LINKS = "links";

    public static final String QUERY_PARAM_GRADE = "grade";
    public static final String QUERY_PARAM_SUBJECT = "subject";
    public static final String QUERY_PARAM_TYPE = "assessmentType";

    public static final String PATH_PARAM_DISTRICT = "districtId";
    public static final String PATH_PARAM_SCHOOL = "schoolId";

    public static final String RESOURCE_PATH_DISTRICT = "district";
    public static final String RESOURCE_PATH_SCHOOL = "school";

    public static final String ENTITY_EXPOSE_TYPE_AGGREGATIONS = "aggregations";
    public static final String ENTITY_TYPE_AGGREGATION = "aggregation";

    public static final String ENTITY_BODY_EDORG_ID = "educationOrganizationId";
    public static final String ENTITY_BODY_STAFF_ID = "staffId";

    public static final String ENTITY_BODY_SCHOOL_ID = "schoolId";
    public static final String ENTITY_BODY_DISTRICT_ID = "districtId";
    public static final String ENTITY_BODY_GROUPBY = "groupBy";
    
    public static final String ENTITY_METADATA_TENANT_ID = "tenantId";

    public static final String RESOURCE_PATH_AGG = "aggregation";

    public static final Map<String, String> RESOURCE_PATH_MAPPINGS = new HashMap<String, String>();
    static {
        RESOURCE_PATH_MAPPINGS.put("educationOrganization", RESOURCE_PATH_DISTRICT);
        RESOURCE_PATH_MAPPINGS.put("school", RESOURCE_PATH_SCHOOL);
    }
}
