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
        
        // system
        Set<String> systemAllowedThreeParts = new HashSet<String>();
        systemAllowedThreeParts.add("session");
        systemAllowedThreeParts.add("debug");
        threeParts.put("system", systemAllowedThreeParts);
        
        // assessments
        Set<String> assessmentsAllowedThreeParts = new HashSet<String>();
        assessmentsAllowedThreeParts.add(ResourceNames.LEARNINGOBJECTIVES);
        assessmentsAllowedThreeParts.add(ResourceNames.LEARNINGSTANDARDS);
        threeParts.put(ResourceNames.ASSESSMENTS, assessmentsAllowedThreeParts);

        // sections
        Set<String> sectionsAllowedThreeParts = new HashSet<String>();
        sectionsAllowedThreeParts.add(ResourceNames.GRADEBOOK_ENTRIES);
        sectionsAllowedThreeParts.add(ResourceNames.STUDENT_SECTION_ASSOCIATIONS);
        threeParts.put(ResourceNames.SECTIONS, sectionsAllowedThreeParts);

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
        schoolsAllowedThreeParts.add(ResourceNames.SECTIONS);
        schoolsAllowedThreeParts.add(ResourceNames.GRADUATION_PLANS);
        threeParts.put(ResourceNames.SCHOOLS, schoolsAllowedThreeParts);

        // edorgs
        Set<String> edOrgsAllowedThreeParts = new HashSet<String>();
        edOrgsAllowedThreeParts.add(ResourceNames.STUDENT_COMPETENCY_OBJECTIVES);
        edOrgsAllowedThreeParts.add(ResourceNames.COURSES);
        edOrgsAllowedThreeParts.add(ResourceNames.EDUCATION_ORGANIZATIONS);
        edOrgsAllowedThreeParts.add(ResourceNames.GRADUATION_PLANS);
        edOrgsAllowedThreeParts.add(ResourceNames.SCHOOLS);
        threeParts.put(ResourceNames.EDUCATION_ORGANIZATIONS, edOrgsAllowedThreeParts);

        // courseTranscripts 
        Set<String> courseTranscriptsAllowedThreeParts = new HashSet<String>();
        courseTranscriptsAllowedThreeParts.add(ResourceNames.COURSES);
        threeParts.put(ResourceNames.COURSE_TRANSCRIPTS, courseTranscriptsAllowedThreeParts);
        
        // courses
        Set<String> coursesAllowedThreeParts = new HashSet<String>();
        coursesAllowedThreeParts.add(ResourceNames.COURSE_OFFERINGS);
        threeParts.put(ResourceNames.COURSES, coursesAllowedThreeParts);

        // courseOfferings
        Set<String> courseOfferingsAllowedThreeParts = new HashSet<String>();
        courseOfferingsAllowedThreeParts.add(ResourceNames.COURSES);
        courseOfferingsAllowedThreeParts.add(ResourceNames.SECTIONS);
        courseOfferingsAllowedThreeParts.add(ResourceNames.SESSIONS);
        threeParts.put(ResourceNames.COURSE_OFFERINGS, courseOfferingsAllowedThreeParts);

        // studentCompetencies 
        Set<String> studentCompetenciesAllowedThreeParts = new HashSet<String>();
        studentCompetenciesAllowedThreeParts.add(ResourceNames.REPORT_CARDS);
        threeParts.put(ResourceNames.STUDENT_COMPETENCIES, studentCompetenciesAllowedThreeParts);

        // studentAcademicRecords 
        Set<String> studentAcademicRecordsAllowedThreeParts = new HashSet<String>();
        studentAcademicRecordsAllowedThreeParts.add(ResourceNames.COURSE_TRANSCRIPTS);
        threeParts.put(ResourceNames.STUDENT_ACADEMIC_RECORDS, studentAcademicRecordsAllowedThreeParts);

        // studentAssessments 
        Set<String> studentAssessmentsAllowedThreeParts = new HashSet<String>();
        studentAssessmentsAllowedThreeParts.add(ResourceNames.ASSESSMENTS);
        threeParts.put(ResourceNames.STUDENT_ASSESSMENTS, studentAssessmentsAllowedThreeParts);

        // students
        Set<String> studentsAllowedThreeParts = new HashSet<String>();
        studentsAllowedThreeParts.add(ResourceNames.ATTENDANCES);
        studentsAllowedThreeParts.add(ResourceNames.COURSE_TRANSCRIPTS);
        studentsAllowedThreeParts.add(ResourceNames.REPORT_CARDS);
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_ACADEMIC_RECORDS);
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_ASSESSMENTS);
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_PARENT_ASSOCIATIONS);
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS);
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_SECTION_ASSOCIATIONS);
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS);
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_COHORT_ASSOCIATIONS);
        studentsAllowedThreeParts.add(ResourceNames.STUDENT_GRADEBOOK_ENTRIES);
        studentsAllowedThreeParts.add(ResourceNames.YEARLY_ATTENDANCES);
        threeParts.put(ResourceNames.STUDENTS, studentsAllowedThreeParts);

        // learningObjectives
        Set<String> learningObjectivesAllowedThreeParts = new HashSet<String>();
        learningObjectivesAllowedThreeParts.add("childLearningObjectives");
        learningObjectivesAllowedThreeParts.add("parentLearningObjectives");
        learningObjectivesAllowedThreeParts.add(ResourceNames.LEARNINGSTANDARDS);
        threeParts.put(ResourceNames.LEARNINGOBJECTIVES, learningObjectivesAllowedThreeParts);

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

        // studentCohortAssociations
        Set<String> studentCohortAssociationsAllowedThreeParts = new HashSet<String>();
        studentCohortAssociationsAllowedThreeParts.add(ResourceNames.COHORTS);
        studentCohortAssociationsAllowedThreeParts.add(ResourceNames.STUDENTS);
        threeParts.put(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, studentCohortAssociationsAllowedThreeParts);

        THREE_PART_ALLOWED = Collections.unmodifiableMap(threeParts);
        
        // FOUR PARTS
        Map<String, Set<List<String>>> fourParts = new HashMap<String, Set<List<String>>>();
        // courses
        Set<List<String>> coursesAllowedFourParts = new HashSet<List<String>>();
        coursesAllowedFourParts.add(Arrays.asList(ResourceNames.COURSE_OFFERINGS, ResourceNames.SESSIONS));
        fourParts.put(ResourceNames.COURSES, coursesAllowedFourParts);

        // sessions
        Set<List<String>> sessionsAllowedFourParts = new HashSet<List<String>>();
        sessionsAllowedFourParts.add(Arrays.asList(ResourceNames.COURSE_OFFERINGS, ResourceNames.COURSES));
        fourParts.put(ResourceNames.SESSIONS, sessionsAllowedFourParts);
        
        // sections
        Set<List<String>> sectionsAllowedFourParts = new HashSet<List<String>>();
        sectionsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, ResourceNames.STUDENTS));
        fourParts.put(ResourceNames.SECTIONS, sectionsAllowedFourParts);

        // schools
        Set<List<String>> schoolsAllowedFourParts = new HashSet<List<String>>();
        schoolsAllowedFourParts.add(Arrays.asList(ResourceNames.SESSIONS, ResourceNames.GRADING_PERIODS));
        schoolsAllowedFourParts.add(Arrays.asList(ResourceNames.COURSE_OFFERINGS, ResourceNames.COURSES));
        fourParts.put(ResourceNames.SCHOOLS, schoolsAllowedFourParts);

        // students
        Set<List<String>> studentsAllowedFourParts = new HashSet<List<String>>();
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, ResourceNames.PARENTS));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, ResourceNames.COHORTS));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, ResourceNames.PROGRAMS));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, ResourceNames.SCHOOLS));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, ResourceNames.SECTIONS));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, ResourceNames.GRADES));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, ResourceNames.STUDENT_COMPETENCIES));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.COURSE_TRANSCRIPTS, ResourceNames.COURSES));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_ASSESSMENTS, ResourceNames.ASSESSMENTS));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_ACADEMIC_RECORDS, ResourceNames.COURSE_TRANSCRIPTS));
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
        
        if (isDisiplineRelated(paths)) {
            return false;
        }
        
        String baseEntity = paths.get(0);
        
        if (paths.size() == 1 && baseEntity.equals("home")) {
            // student can access /home
            return true;
        }
        
        if (paths.size() == 2) {
            // two parts are always allowed
            return true;
        }

        if (paths.size() == 3) {
        	if (paths.get(2).equals(ResourceNames.CUSTOM)) {
        		//custom endpoints always allowed
        		return true;
        	}      	     	
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
