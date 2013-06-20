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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.PathSegment;

import com.sun.jersey.spi.container.ContainerRequest;

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
        systemAllowedThreeParts.add("check");
        systemAllowedThreeParts.add("logout");
        systemAllowedThreeParts.add("email");
        threeParts.put("system", systemAllowedThreeParts);

        // assessments
        Set<String> assessmentsAllowedThreeParts = new HashSet<String>();
        assessmentsAllowedThreeParts.add(ResourceNames.LEARNINGOBJECTIVES);
        assessmentsAllowedThreeParts.add(ResourceNames.LEARNINGSTANDARDS);
        threeParts.put(ResourceNames.ASSESSMENTS, assessmentsAllowedThreeParts);

        // cohorts
        Set<String> cohortsAllowedThreeParts = new HashSet<String>();
        cohortsAllowedThreeParts.add(ResourceNames.STUDENT_COHORT_ASSOCIATIONS);
        cohortsAllowedThreeParts.add(ResourceNames.STAFF_COHORT_ASSOCIATIONS);
        threeParts.put(ResourceNames.COHORTS, cohortsAllowedThreeParts);

        // programs
        Set<String> programsAllowedThreeParts = new HashSet<String>();
        programsAllowedThreeParts.add(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS);
        programsAllowedThreeParts.add(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS);
        threeParts.put(ResourceNames.PROGRAMS, programsAllowedThreeParts);

        // sections
        Set<String> sectionsAllowedThreeParts = new HashSet<String>();
        sectionsAllowedThreeParts.add(ResourceNames.GRADEBOOK_ENTRIES);
        sectionsAllowedThreeParts.add(ResourceNames.STUDENT_SECTION_ASSOCIATIONS);
        sectionsAllowedThreeParts.add(ResourceNames.TEACHER_SECTION_ASSOCIATIONS);
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
        schoolsAllowedThreeParts.add(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS);
        threeParts.put(ResourceNames.SCHOOLS, schoolsAllowedThreeParts);

        // edorgs
        Set<String> edOrgsAllowedThreeParts = new HashSet<String>();
        edOrgsAllowedThreeParts.add(ResourceNames.STUDENT_COMPETENCY_OBJECTIVES);
        edOrgsAllowedThreeParts.add(ResourceNames.COURSES);
        edOrgsAllowedThreeParts.add(ResourceNames.EDUCATION_ORGANIZATIONS);
        edOrgsAllowedThreeParts.add(ResourceNames.GRADUATION_PLANS);
        edOrgsAllowedThreeParts.add(ResourceNames.SCHOOLS);
        edOrgsAllowedThreeParts.add(ResourceNames.SESSIONS);
        edOrgsAllowedThreeParts.add(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS);
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

        // staffCohortAssociations
        Set<String> staffCohortAssociationsAllowedThreeParts = new HashSet<String>();
        staffCohortAssociationsAllowedThreeParts.add(ResourceNames.COHORTS);
        staffCohortAssociationsAllowedThreeParts.add(ResourceNames.STAFF);
        threeParts.put(ResourceNames.STAFF_COHORT_ASSOCIATIONS, staffCohortAssociationsAllowedThreeParts);

        // staffEdOrgAssociations
        Set<String> staffEdOrgAssociationsAllowedThreeParts = new HashSet<String>();
        staffEdOrgAssociationsAllowedThreeParts.add(ResourceNames.EDUCATION_ORGANIZATIONS);
        staffEdOrgAssociationsAllowedThreeParts.add(ResourceNames.STAFF);
        threeParts.put(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, staffEdOrgAssociationsAllowedThreeParts);
        
        // staffProgramAssociations
        Set<String> staffProgramAssociationsAllowedThreeParts = new HashSet<String>();
        staffProgramAssociationsAllowedThreeParts.add(ResourceNames.PROGRAMS);
        staffProgramAssociationsAllowedThreeParts.add(ResourceNames.STAFF);
        threeParts.put(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS, staffProgramAssociationsAllowedThreeParts);

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
        studentAssessmentsAllowedThreeParts.add(ResourceNames.STUDENTS);
        threeParts.put(ResourceNames.STUDENT_ASSESSMENTS, studentAssessmentsAllowedThreeParts);

        // students
        Set<String> studentsAllowedThreeParts = new HashSet<String>();
        studentsAllowedThreeParts.add(ResourceNames.ATTENDANCES);
        //studentsAllowedThreeParts.add(ResourceNames.COURSE_TRANSCRIPTS);
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
        studentParentAssociationsAllowedThreeParts.add(ResourceNames.STUDENTS);
        threeParts.put(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, studentParentAssociationsAllowedThreeParts);

        // studentSchoolAssociations
        Set<String> studentSchoolAssociationsAllowedThreeParts = new HashSet<String>();
        studentSchoolAssociationsAllowedThreeParts.add(ResourceNames.SCHOOLS);
        studentSchoolAssociationsAllowedThreeParts.add(ResourceNames.STUDENTS);
        threeParts.put(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, studentSchoolAssociationsAllowedThreeParts);

        // studentProgramAssociations
        Set<String> studentProgramAssociationsAllowedThreeParts = new HashSet<String>();
        studentProgramAssociationsAllowedThreeParts.add(ResourceNames.PROGRAMS);
        studentProgramAssociationsAllowedThreeParts.add(ResourceNames.STUDENTS);
        threeParts.put(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, studentProgramAssociationsAllowedThreeParts);

        // studentSectionAssociations
        Set<String> studentSectionAssociationsAllowedThreeParts = new HashSet<String>();
        studentSectionAssociationsAllowedThreeParts.add(ResourceNames.GRADES);
        studentSectionAssociationsAllowedThreeParts.add(ResourceNames.SECTIONS);
        studentSectionAssociationsAllowedThreeParts.add(ResourceNames.STUDENTS);
        studentSectionAssociationsAllowedThreeParts.add(ResourceNames.STUDENT_COMPETENCIES);
        threeParts.put(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, studentSectionAssociationsAllowedThreeParts);

        // studentCohortAssociations
        Set<String> studentCohortAssociationsAllowedThreeParts = new HashSet<String>();
        studentCohortAssociationsAllowedThreeParts.add(ResourceNames.COHORTS);
        studentCohortAssociationsAllowedThreeParts.add(ResourceNames.STUDENTS);
        threeParts.put(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, studentCohortAssociationsAllowedThreeParts);
        
        // teacherSchoolAssociations
        Set<String> teacherSchoolAssociationsAllowedThreeParts = new HashSet<String>();
        teacherSchoolAssociationsAllowedThreeParts.add(ResourceNames.SCHOOLS);
        teacherSchoolAssociationsAllowedThreeParts.add(ResourceNames.TEACHERS);
        threeParts.put(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, teacherSchoolAssociationsAllowedThreeParts);
        
        // teacherSectionAssociations
        Set<String> teacherSectionAssociationsAllowedThreeParts = new HashSet<String>();
        teacherSectionAssociationsAllowedThreeParts.add(ResourceNames.SECTIONS);
        teacherSectionAssociationsAllowedThreeParts.add(ResourceNames.TEACHERS);
        threeParts.put(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, teacherSectionAssociationsAllowedThreeParts);

        THREE_PART_ALLOWED = Collections.unmodifiableMap(threeParts);

        // FOUR PARTS
        Map<String, Set<List<String>>> fourParts = new HashMap<String, Set<List<String>>>();

        // courses
        Set<List<String>> coursesAllowedFourParts = new HashSet<List<String>>();
        coursesAllowedFourParts.add(Arrays.asList(ResourceNames.COURSE_OFFERINGS, ResourceNames.SESSIONS));
        fourParts.put(ResourceNames.COURSES, coursesAllowedFourParts);
        
        // cohorts
        Set<List<String>> cohortsAllowedFourParts = new HashSet<List<String>>();
        cohortsAllowedFourParts.add(Arrays.asList(ResourceNames.STAFF_COHORT_ASSOCIATIONS, ResourceNames.STAFF));
        cohortsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, ResourceNames.STUDENTS));
        fourParts.put(ResourceNames.COHORTS, cohortsAllowedFourParts);
        
        // educationOrganizations
        Set<List<String>> educationOrganizationsAllowedFourParts = new HashSet<List<String>>();
        educationOrganizationsAllowedFourParts.add(Arrays.asList(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, ResourceNames.STAFF));
        fourParts.put(ResourceNames.EDUCATION_ORGANIZATIONS, educationOrganizationsAllowedFourParts);

        // sessions
        Set<List<String>> sessionsAllowedFourParts = new HashSet<List<String>>();
        sessionsAllowedFourParts.add(Arrays.asList(ResourceNames.COURSE_OFFERINGS, ResourceNames.COURSES));
        fourParts.put(ResourceNames.SESSIONS, sessionsAllowedFourParts);

        // sections
        Set<List<String>> sectionsAllowedFourParts = new HashSet<List<String>>();
        sectionsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, ResourceNames.STUDENTS));
        sectionsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, ResourceNames.GRADEBOOK_ENTRIES));
        sectionsAllowedFourParts.add(Arrays.asList(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, ResourceNames.TEACHERS));
        fourParts.put(ResourceNames.SECTIONS, sectionsAllowedFourParts);

        // edorgs
        Set<List<String>> edOrgsAllowedFourParts = new HashSet<List<String>>();
        edOrgsAllowedFourParts.add(Arrays.asList(ResourceNames.SESSIONS, ResourceNames.GRADING_PERIODS));
        edOrgsAllowedFourParts.add(Arrays.asList(ResourceNames.COURSE_OFFERINGS, ResourceNames.COURSES));
        fourParts.put(ResourceNames.EDUCATION_ORGANIZATIONS, edOrgsAllowedFourParts);

        // programs
        Set<List<String>> programsAllowedFourParts = new HashSet<List<String>>();
        programsAllowedFourParts.add(Arrays.asList(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS, ResourceNames.STAFF));
        programsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, ResourceNames.STUDENTS));
        fourParts.put(ResourceNames.PROGRAMS, programsAllowedFourParts);

        // schools
        Set<List<String>> schoolsAllowedFourParts = new HashSet<List<String>>();
        schoolsAllowedFourParts.add(Arrays.asList(ResourceNames.SESSIONS, ResourceNames.GRADING_PERIODS));
        schoolsAllowedFourParts.add(Arrays.asList(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, ResourceNames.TEACHERS));
        // schoolsAllowedFourParts.add(Arrays.asList(ResourceNames.COURSE_OFFERINGS,
        // ResourceNames.COURSES));
        fourParts.put(ResourceNames.SCHOOLS, schoolsAllowedFourParts);

        // students
        Set<List<String>> studentsAllowedFourParts = new HashSet<List<String>>();
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, ResourceNames.PARENTS));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, ResourceNames.COHORTS));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, ResourceNames.PROGRAMS));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, ResourceNames.SCHOOLS));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, ResourceNames.SECTIONS));
        //studentsAllowedFourParts.add(Arrays.asList(ResourceNames.COURSE_TRANSCRIPTS, ResourceNames.COURSES));
        studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_ASSESSMENTS, ResourceNames.ASSESSMENTS));
        // studentsAllowedFourParts.add(Arrays.asList(ResourceNames.STUDENT_ACADEMIC_RECORDS,
        // ResourceNames.COURSE_TRANSCRIPTS));
        fourParts.put(ResourceNames.STUDENTS, studentsAllowedFourParts);

        FOUR_PART_ALLOWED = Collections.unmodifiableMap(fourParts);
    }


    /**
     * check if a path can be accessed according to stored business rules
     *
     * @param ContainerRequest request 
     * @return true if accessible by student
     */
    public boolean isAllowed(ContainerRequest request) {
        if (request == null || request.getPathSegments() == null) {
            return false;
        }
        List<PathSegment> segs = request.getPathSegments();
        List<String> paths = new ArrayList<String>();
        
        // first one is version, system calls (un-versioned) have been handled elsewhere
        for (int i = 1; i < segs.size(); ++i) {
            if (segs.get(i) != null) {
                String path = segs.get(i).getPath();
                if (path != null && !path.isEmpty()) {
                    paths.add(segs.get(i).getPath());
                }
            }
        }

        if (paths.isEmpty()) {
            return false;
        }

        if (isDisiplineRelated(paths)) {
            return false;
        }

        String baseEntity = paths.get(0);

        switch (paths.size()) {
            case 1:
                return baseEntity.equals("home") || !request.getQueryParameters().isEmpty();
            case 2:
                return true;
            case 3:
                if (paths.get(2).equals(ResourceNames.CUSTOM)) {
                    // custom endpoints always allowed
                    return true;
                }
                return THREE_PART_ALLOWED.get(baseEntity) != null
                        && THREE_PART_ALLOWED.get(baseEntity).contains(paths.get(2));
            case 4:
                List<String> subUrl = Arrays.asList(paths.get(2), paths.get(3));
                return FOUR_PART_ALLOWED.get(baseEntity) != null
                        && FOUR_PART_ALLOWED.get(baseEntity).contains(subUrl);
            default:
                return false;
        }
    }

    private boolean isDisiplineRelated(List<String> paths) {
        for (String s : paths) {
            if (DISCIPLINE_RELATED.contains(s)) {
                return true;
            }
        }

        return false;
    }

    // this default scope method is used to generate a list of whitelisted url for easy validation
    // in scripts
    List<String> getAllWhiteLists() {
        List<String> allLists = new ArrayList<String>();
        for (Map.Entry<String, Set<String>> entry : THREE_PART_ALLOWED.entrySet()) {
           for (String s : entry.getValue()) {
                allLists.add(String.format("/%s/{id}/%s", entry.getKey(), s));
           }
        }

        for (Map.Entry<String, Set<List<String>>> entry : FOUR_PART_ALLOWED.entrySet()) {
            for (List<String> list : entry.getValue()) {
                allLists.add(String.format("/%s/{id}/%s/%s", entry.getKey(), list.get(0), list.get(1)));
            }
        }

        Collections.sort(allLists);
        return allLists;
    }
}
