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


package org.slc.sli.api.resources.v1.view;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceNames;

/**
 * Factory for returning the needed strategy to create the custom views
 * returned by the api
 * @author srupasinghe
 *
 */
@Component
public class OptionalFieldAppenderFactory {
    private Map<String, OptionalFieldAppender> generators = null;
    public static final String APPENDER_PREFIX = "appender";
    public static final String PARAM_PREFIX = "params";

    @Autowired
    private OptionalFieldAppender studentAssessmentOptionalFieldAppender;

    @Autowired
    private OptionalFieldAppender studentAllAttendanceOptionalFieldAppender;

    @Autowired
    private OptionalFieldAppender studentGradebookOptionalFieldAppender;

    @Autowired
    private OptionalFieldAppender studentTranscriptOptionalFieldAppender;

    @Autowired
    private OptionalFieldAppender studentGradeLevelOptionalFieldAppender;

    @PostConstruct
    protected void init() {
        generators = new HashMap<String, OptionalFieldAppender>();

        generators.put(ResourceNames.SECTIONS + "_" + ParameterConstants.OPTIONAL_FIELD_ASSESSMENTS,
                studentAssessmentOptionalFieldAppender);
        generators.put(ResourceNames.SECTIONS + "_" + ParameterConstants.OPTIONAL_FIELD_GRADEBOOK, studentGradebookOptionalFieldAppender);
        generators.put(ResourceNames.SECTIONS + "_" + ParameterConstants.OPTIONAL_FIELD_TRANSCRIPT, studentTranscriptOptionalFieldAppender);
        generators.put(ResourceNames.SECTIONS + "_" + ParameterConstants.OPTIONAL_FIELD_ATTENDANCES, studentAllAttendanceOptionalFieldAppender);
        generators.put(ResourceNames.SECTIONS + "_" + ParameterConstants.OPTIONAL_FIELD_GRADE_LEVEL, studentGradeLevelOptionalFieldAppender);

        generators.put(ResourceNames.STUDENTS + "_" + ParameterConstants.OPTIONAL_FIELD_ASSESSMENTS, studentAssessmentOptionalFieldAppender);
        generators.put(ResourceNames.STUDENTS + "_" + ParameterConstants.OPTIONAL_FIELD_ATTENDANCES, studentAllAttendanceOptionalFieldAppender);
        generators.put(ResourceNames.STUDENTS + "_" + ParameterConstants.OPTIONAL_FIELD_GRADEBOOK, studentGradebookOptionalFieldAppender);
        generators.put(ResourceNames.STUDENTS + "_" + ParameterConstants.OPTIONAL_FIELD_TRANSCRIPT, studentTranscriptOptionalFieldAppender);
        generators.put(ResourceNames.STUDENTS + "_" + ParameterConstants.OPTIONAL_FIELD_GRADE_LEVEL, studentGradeLevelOptionalFieldAppender);
    }

    /**
     * Returns the correct strategy to get the data for the option provided
     * @param type The type of strategy needed
     * @return
     */
    public OptionalFieldAppender getOptionalFieldAppender(String type) {
        return generators.get(type);
    }
}
