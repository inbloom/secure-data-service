package org.slc.sli.common.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * define the super doc entities that always return with embedded fields included
 * 
 * @author Dong Liu dliu@wgen.net
 */

public class FullSuperDoc {
    
    public static final Map<String, Set<String>> FULL_ENTITIES = new HashMap<String, Set<String>>();
    
    static {
        Set<String> assessmentNestedFields = new HashSet<String>();
        assessmentNestedFields.addAll(Arrays.asList("assessmentItem", "objectiveAssessment"));
        Set<String> saNestedFields = new HashSet<String>();
        saNestedFields.addAll(Arrays.asList("studentObjectiveAssessment", "studentAssessmentItem"));
        FULL_ENTITIES.put("assessment", assessmentNestedFields);
        FULL_ENTITIES.put("studentAssessment", saNestedFields);
    }

}
