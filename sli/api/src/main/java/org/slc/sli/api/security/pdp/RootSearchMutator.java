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

package org.slc.sli.api.security.pdp;

import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.generic.config.ResourceEndPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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

    private static final List<Pair<String, String>> PARAMETER_RESOURCE_PAIRS = Arrays.asList(
            Pair.of(ParameterConstants.STUDENT_ID, ResourceNames.STUDENTS),
            Pair.of(ParameterConstants.SCHOOL_ID, ResourceNames.SCHOOLS),
            Pair.of(ParameterConstants.STAFF_REFERENCE, ResourceNames.STAFF),
            Pair.of(ParameterConstants.TEACHER_ID, ResourceNames.TEACHERS),
            Pair.of(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, ResourceNames.STUDENT_SECTION_ASSOCIATIONS)
    );

    public String mutatePath(String version, String resource, String queryParameters) {
        String mutatedPath = null;

        Map<String, String> parameters = MutatorUtil.getParameterMap(queryParameters);

        for (Pair<String, String> parameterResourcePair : PARAMETER_RESOURCE_PAIRS) {
            String curParameter = parameterResourcePair.getLeft();
            String curResource = parameterResourcePair.getRight();
            if (parameters.containsKey(curParameter)) {
                if (isValidPath(version, curResource, resource)) {
                    mutatedPath = "/" + curResource + "/" + parameters.get(curParameter) + "/" + resource;
                    break;
                }
            }
        }

        return mutatedPath;
    }

    private boolean isValidPath(String version, String baseResource, String subResources) {
        boolean isValid = false;

        String resourcePath = version + "/" + baseResource + "/" + PathConstants.ID_PLACEHOLDER + "/" + subResources;
        if (resourceEndPoint.getResources().containsKey(resourcePath)) {
            isValid = true;
        }

        return isValid;
    }



}
