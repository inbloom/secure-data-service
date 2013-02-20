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

package org.slc.sli.domain;

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
    
    public static boolean isFullSuperdoc(Entity entity) {
        return FULL_ENTITIES.containsKey(entity.getType());
    }

}
