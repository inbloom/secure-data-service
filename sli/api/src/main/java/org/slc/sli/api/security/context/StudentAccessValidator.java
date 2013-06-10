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
package org.slc.sli.api.security.context;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.common.constants.EntityNames;

/**
 * This class encapsulates access rules (URL wise)
 * for a student principal
 * 
 * @author ycao
 * 
 */
@Component
public class StudentAccessValidator {
    
    /**
     * this validator can only validates url in the format of /{entityType}/{id}/{subEntityType}
     * 
     * i.e. /sections/{id}/studentSectionAssociations
     */
    private static final Map<String, Set<String>> THREE_PART_ALLOWED;
    
    /**
     * this validator can only validates url in the format of
     * /{entityType}/{id}/{subEntityType}/{subsubentityType}
     * 
     * i.e. /schools/{id}/teacherSchoolAssociations/teachers
     */
    private static final Map<String, Set<List<String>>> FOUR_PART_ALLOWED;
    
    /**
     * discipline related entities
     */
    private static final Set<String> DISCIPLINE_RELATED = new HashSet<String>(
            Arrays.asList(EntityNames.DISCIPLINE_ACTION,
                    EntityNames.DISCIPLINE_INCIDENT,
                    EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION,
                    ResourceNames.DISCIPLINE_ACTIONS,
                    ResourceNames.DISCIPLINE_INCIDENTS,
                    ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS
                    ));

    static {
        // THREE PARTS
        Map<String, Set<String>> threeParts = new HashMap<String, Set<String>>();
        // sessions
        Set<String> sessionsAllowedThreeParts = new HashSet<String>();
        sessionsAllowedThreeParts.add(ResourceNames.COURSE_OFFERINGS);
        sessionsAllowedThreeParts.add(ResourceNames.SECTIONS);
        threeParts.put(ResourceNames.SESSIONS, sessionsAllowedThreeParts);
        
        // schools
        Set<String> schoolsAllowedThreeParts = new HashSet<String>();
        schoolsAllowedThreeParts.add(ResourceNames.COURSE_OFFERINGS);
        schoolsAllowedThreeParts.add(ResourceNames.COURSES);
        schoolsAllowedThreeParts.add(ResourceNames.SESSIONS);
        schoolsAllowedThreeParts.add(ResourceNames.GRADUATION_PLANS);
        threeParts.put(ResourceNames.SCHOOLS, schoolsAllowedThreeParts);

        // edorgs
        Set<String> edOrgsAllowedThreeParts = new HashSet<String>();
        edOrgsAllowedThreeParts.add(ResourceNames.STUDENT_COMPETENCY_OBJECTIVES);
        threeParts.put(ResourceNames.EDUCATION_ORGANIZATIONS, edOrgsAllowedThreeParts);

        // students
        Set<String> studentsAllowedThreeParts = new HashSet<String>();
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_PARENT_ASSOCIATIONS);
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS);
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_SECTION_ASSOCIATIONS);
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS);
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_COHORT_ASSOCIATIONS);
        threeParts.put(ResourceNames.STUDENTS, studentsAllowedThreeParts);

        // studentParentAssociations
        Set<String> studentParentAssociationsAllowedThreeParts = new HashSet<String>();
        studentParentAssociationsAllowedThreeParts.add(ResourceNames.PARENTS);
        threeParts.put(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, studentParentAssociationsAllowedThreeParts);
        
        // studentSchoolAssociations
        Set<String> studentSchoolAssociationsAllowedThreeParts = new HashSet<String>();
        studentSchoolAssociationsAllowedThreeParts.add(ResourceNames.SCHOOLS);
        threeParts.put(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, studentSchoolAssociationsAllowedThreeParts);

        // studentProgramAssociations
        Set<String> studentProgramAssociationsAllowedThreeParts = new HashSet<String>();
        studentProgramAssociationsAllowedThreeParts.add(ResourceNames.PROGRAMS);
        studentProgramAssociationsAllowedThreeParts.add(ResourceNames.STUDENTS);
        threeParts.put(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, studentProgramAssociationsAllowedThreeParts);

        // studentSectionAssociations
        Set<String> studentSectionAssociationsAllowedThreeParts = new HashSet<String>();
        studentSectionAssociationsAllowedThreeParts.add(ResourceNames.SECTIONS);
        studentSectionAssociationsAllowedThreeParts.add(ResourceNames.STUDENTS);
        threeParts.put(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, studentSectionAssociationsAllowedThreeParts);

        THREE_PART_ALLOWED = Collections.unmodifiableMap(threeParts);
        
        // FOUR PARTS
        Map<String, Set<List<String>>> fourParts = new HashMap<String, Set<List<String>>>();
        // sessions
        Set<List<String>> sessionsAllowedFourParts = new HashSet<List<String>>();
        sessionsAllowedFourParts.add(Arrays.asList(ResourceNames.COURSE_OFFERINGS, ResourceNames.COURSES));
        fourParts.put(ResourceNames.SESSIONS, sessionsAllowedFourParts);
        
        // schools
        Set<List<String>> schoolsAllowedFourParts = new HashSet<List<String>>();
        schoolsAllowedFourParts.add(Arrays.asList(ResourceNames.SESSIONS, ResourceNames.GRADING_PERIODS));
        fourParts.put(ResourceNames.SCHOOLS, schoolsAllowedFourParts);

        // students
        Set<List<String>> studentsAllowedFourParts = new HashSet<List<String>>();
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, ResourceNames.PARENTS));
        fourParts.put(ResourceNames.STUDENTS, studentsAllowedFourParts);

        FOUR_PART_ALLOWED = Collections.unmodifiableMap(fourParts);
    }
    

    /**
     * check if a path can be accessed according to stored business rules
     * 
     * the path passed in must be preprocessed so it doesn't contain empty
     * segments or the api version
     * 
     * @param paths
     *            i.e. sections/{id}/studentSectionAssociations
     * @return true if accessible by student
     */
    public boolean isAllowed(List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            return false;
        }
        
        if (isDisiplineRelated(paths)) {
            return false;
        }
        
        String baseEntity = paths.get(0);
        
        if (paths.size() == 2) {
            // two parts are always allowed
            return true;
        }

        if (paths.size() == 3) {
            return THREE_PART_ALLOWED.get(baseEntity) != null
                    && THREE_PART_ALLOWED.get(baseEntity).contains(paths.get(2));
        }
        
        if (paths.size() == 4) {
            List<String> subUrl = Arrays.asList(paths.get(2), paths.get(3));
            return FOUR_PART_ALLOWED.get(baseEntity) != null
                    && FOUR_PART_ALLOWED.get(baseEntity).contains(subUrl);
        }
        
        return false;
    }
    
    private boolean isDisiplineRelated(List<String> paths) {
        for (String s : paths) {
            if (DISCIPLINE_RELATED.contains(s)) {
                return true;
            }
        }

        return false;
    }

}
