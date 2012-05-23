package org.slc.sli.api.resources.v1.view;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.client.constants.v1.ParameterConstants;

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
    
    public OptionalFieldAppenderFactory() {
    }

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
