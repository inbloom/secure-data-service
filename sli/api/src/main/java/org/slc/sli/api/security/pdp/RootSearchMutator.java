package org.slc.sli.api.security.pdp;

import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.generic.config.ResourceEndPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mutates root entity requests that contain ids as query parameters
 * Example: /v1/attendances?studentId=some_ids becomes /students/some_ids/attendances
 */
@Component
public class RootSearchMutator {

    @Autowired
    ResourceEndPoint resourceEndPoint;

    private static final List<Pair<String, String>> PARAMETER_RESOURCE_PARIS = Arrays.asList(
            Pair.of(ParameterConstants.STUDENT_ID, ResourceNames.STUDENTS),
            Pair.of(ParameterConstants.SCHOOL_ID, ResourceNames.SCHOOLS),
            Pair.of(ParameterConstants.STAFF_REFERENCE, ResourceNames.STAFF),
            Pair.of(ParameterConstants.TEACHER_ID, ResourceNames.TEACHERS),
            Pair.of(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, ResourceNames.STUDENT_SECTION_ASSOCIATIONS)
    );

    public String mutatePath(String resource, String queryParameters) {
        String mutatedPath = null;

        Map<String, String> parameters = getParameterMap(queryParameters);

        for (Pair<String, String> parameterResourcePair : PARAMETER_RESOURCE_PARIS) {
            String curParameter = parameterResourcePair.getLeft();
            String curResource = parameterResourcePair.getRight();
            if (parameters.containsKey(curParameter)) {
                if (isValidPath(curResource, resource)) {
                    mutatedPath = "/" + curResource + "/" + parameters.get(curParameter) + "/" + resource;
                    break;
                }
            }
        }

        return mutatedPath;
    }

    private boolean isValidPath(String baseResource, String subResources) {
        boolean isValid = false;

        String resourcePath = PathConstants.V1 + "/" + baseResource + "/" + PathConstants.ID_PLACEHOLDER + "/" + subResources;
        if (resourceEndPoint.getResources().containsKey(resourcePath)) {
            isValid = true;
        }

        return isValid;
    }

    private Map<String, String> getParameterMap(String queryParameters) {
        Map<String, String> parameters = new HashMap<String, String>();

        for (String query : queryParameters.split("&")) {
            String[] keyAndValue = query.split("=", 2);
            if (keyAndValue.length == 2) {
                parameters.put(keyAndValue[0], keyAndValue[1]);
            }
        }
        return parameters;
    }

}