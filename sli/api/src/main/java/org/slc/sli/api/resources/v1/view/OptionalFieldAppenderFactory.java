package org.slc.sli.api.resources.v1.view;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.v1.ParameterConstants;

/**
 * Factory for returning the needed strategy to create the custom views
 * returned by the api
 * @author srupasinghe
 *
 */
@Component
public class OptionalFieldAppenderFactory {
    private Map<String, OptionalFieldAppender> generators = new HashMap<String, OptionalFieldAppender>();
    
    @Autowired
    private OptionalFieldAppender studentAssessmentOptionalFieldAppender;
    
    @Autowired
    private OptionalFieldAppender studentAttendanceOptionalFieldAppender;
    
    public OptionalFieldAppenderFactory() {
    }
    
    @PostConstruct
    protected void init() {
        generators.put(ParameterConstants.OPTIONAL_FIELD_ASSESSMENTS, studentAssessmentOptionalFieldAppender);
        generators.put(ParameterConstants.OPTIONAL_FIELD_ATTENDANCES, studentAttendanceOptionalFieldAppender);
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
