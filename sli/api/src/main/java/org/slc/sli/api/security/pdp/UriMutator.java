/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.PathSegment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.ResponseTooLargeException;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.SectionHelper;
import org.slc.sli.domain.Entity;

/**
 * Infers context about the {user,requested resource} pair, and restricts blanket API calls to
 * smaller (and generally more manageable) scope.
 */
@Component
public class UriMutator {

    @Resource
    private EdOrgHelper edOrgHelper;

    @Resource
    private SectionHelper sectionHelper;

    /**
     * Acts as a filter to determine if the requested resource, given knowledge of the user
     * requesting it, should be rewritten. Returning null indicates that the URI should NOT be
     * rewritten.
     *
     * @param segments
     *            List of Path Segments representing request URI.
     * @param queryParameters
     *            String containing query parameters.
     * @param user
     *            User requesting resource.
     * @return Pair of {String, String} representing {mutated path (if necessary), mutated
     *         parameters (if necessary)}, where path or parameters will be null if they didn't need
     *         to be rewritten.
     */
    public Pair<String, String> mutate(List<PathSegment> segments, String queryParameters,
            Entity user) {
        String mutatedPath = null;
        String mutatedParameters = queryParameters;

        if (segments.size() < 3) {
            mutatedPath = mutateBaseUri(segments.get(1).getPath(), user);
        } else {
            Pair<String, String> mutated = mutateUriAsNecessary(segments, queryParameters, user);
            mutatedPath = mutated.getLeft();
            mutatedParameters = mutated.getRight();
        }

        return Pair.of(mutatedPath, mutatedParameters);
    }

    /**
     * Mutates the API call (not to a base entity) to a more-specific (and generally more
     * constrained) URI.
     *
     * @param segments
     *            List of Path Segments representing request URI.
     * @param queryParameters
     *            String containing query parameters.
     * @param user
     *            User requesting resource.
     * @return Pair of {String, String} representing {mutated path (if necessary), mutated
     *         parameters (if necessary)}, where path or parameters will be null if they didn't need
     *         to be rewritten.
     */
    private Pair<String, String> mutateUriAsNecessary(List<PathSegment> segments,
            String queryParameters, Entity user) throws ResponseTooLargeException {
        String mutatedPath = null;
        String mutatedParameters = queryParameters;

        List<String> stringifiedSegments = stringifyPathSegments(segments);
        if (isTeacher(user)) {
            if (stringifiedSegments.size() == 4) {
                String baseEntity = stringifiedSegments.get(1);
                String transitiveEntityId = stringifiedSegments.get(2);
                String requestedEntity = stringifiedSegments.get(3);

                // if {id} is in original URI, rewrite will fail if more than one id was specified. if {ids}, multiple ids are allowed.
                // [done] v1/courses/{id}/courseTranscripts          --> [done] /sections/{ids}/studentSectionAssociations/students/courseTranscripts?courseId={id}
                // [done] v1/gradingPeriods/{id}/reportCards         --> [done] /sections/{ids}/studentSectionAssociations/students/reportCards?gradingPeriod={id}
                // [done] v1/gradingPeriods/{id}/grades              --> [done] /sections/{ids}/studentSectionAssociations/students/grades?gradingPeriod={id}
                // [done] v1/sessions/{id}/studentAcademicRecords    --> [done] /sections/{ids}/studentSectionAssociations/students/studentAcademicRecords?sessionId={id}
                // [done] v1/sessions/{id}/sections                  --> [done] /sections/{ids}?sessionId={id}
                // [done] v1/educationOrganizations/{id}/cohorts     --> [done] /teachers/{id}/staffCohortAssociations/cohorts?educationOrgId={id}
                // [done] v1/assessments/{id}/studentAssessments     --> [done] /sections/{ids}/studentSectionAssociations/students/studentAssessments?assessmentId={id}
                // [done] v1/courseOfferings/{id}/sections           --> [done] /sections/{ids}?courseOfferingId={id}
                // [done] v1/learningObjectives/{id}/studentCompetencies --> [done] /sections/{ids}/studentSectionAssociations/studentCompetencies?learningObjectiveId={id}
                // [done] v1/schools/{ids}/studentSchoolAssociations --> [done] /sections/{ids}/studentSectionAssociations/students/studentSchoolAssociations
                // [done] v1/schools/{ids}/sections                  --> [done] /teachers/{id}/teacherSectionAssociations/sections
                if (baseEntity.equals(PathConstants.ASSESSMENTS) && requestedEntity.equals(PathConstants.STUDENT_ASSESSMENT_ASSOCIATIONS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/studentAssessments",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "assessmentId=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "assessmentId=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.COURSES) && requestedEntity.equals(PathConstants.COURSE_TRANSCRIPTS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/courseTranscripts",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "courseId=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "courseId=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.COURSE_OFFERINGS) && requestedEntity.equals(PathConstants.SECTIONS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/sections/%s",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "courseOfferingId=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "courseOfferingId=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.EDUCATION_ORGANIZATIONS) && requestedEntity.equals(PathConstants.COHORTS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/teachers/%s/staffCohortAssociations/cohorts", user.getEntityId());
                    if (mutatedParameters != null) {
                        mutatedParameters = "educationOrgId=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "educationOrgId=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.GRADING_PERIODS) && requestedEntity.equals(PathConstants.GRADES)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/grades",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "gradingPeriod=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "gradingPeriod=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.GRADING_PERIODS) && requestedEntity.equals(PathConstants.REPORT_CARDS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/reportCards",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "gradingPeriod=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "gradingPeriod=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.LEARNING_OBJECTIVES) && requestedEntity.equals(PathConstants.STUDENT_COMPETENCIES)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/studentCompetencies",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "learningObjectiveId=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "learningObjectiveId=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.SESSIONS) && requestedEntity.equals(PathConstants.SECTIONS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/sections/%s",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "sessionId=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "sessionId=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.SESSIONS) && requestedEntity.equals(PathConstants.STUDENT_ACADEMIC_RECORDS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/studentAcademicRecords",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "sessionId=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "sessionId=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && requestedEntity.equals(PathConstants.STUDENT_SCHOOL_ASSOCIATIONS)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/studentSchoolAssociations",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && requestedEntity.equals(PathConstants.SECTIONS)) {
                    mutatedPath = String.format("/teachers/%s/teacherSectionAssociations/sections", user.getEntityId());
                }
            } else if (stringifiedSegments.size() == 5) {
                String baseEntity = stringifiedSegments.get(1);
                String transitiveEntity = stringifiedSegments.get(3);
                String requestedEntity = stringifiedSegments.get(4);

                // [done] v1/schools/{ids}/sections/gradebookEntries           --> [done] /sections/{ids}/gradebookEntries
                // [done] v1/schools/{ids}/sections/studentSectionAssociations --> [done] /sections/{ids}/studentSectionAssociations
                // [done] v1/schools/{ids}/studentSchoolAssociations/students  --> [done] /sections/{ids}/studentSectionAssociations/students
                if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity.equals(PathConstants.SECTIONS) && requestedEntity.equals(PathConstants.GRADEBOOK_ENTRIES)) {
                    mutatedPath = String.format("/sections/%s/gradebookEntries",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity.equals(PathConstants.SECTIONS) && requestedEntity.equals(PathConstants.STUDENT_SECTION_ASSOCIATIONS)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity.equals(PathConstants.STUDENT_SCHOOL_ASSOCIATIONS) && requestedEntity.equals(PathConstants.STUDENTS)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                }
            } else if (stringifiedSegments.size() == 6) {
                String baseEntity = stringifiedSegments.get(1);
                String transitiveEntity1 = stringifiedSegments.get(3);
                String transitiveEntity2 = stringifiedSegments.get(4);
                String requestedEntity = stringifiedSegments.get(5);

                // [done] v1/schools/{ids}/sections/studentSectionAssociations/grades                    --> [done] /sections/{ids}/studentSectionAssociations/grades
                // [done] v1/schools/{ids}/sections/studentSectionAssociations/studentCompetencies       --> [done] /sections/{ids}/studentSectionAssociations/studentCompetencies
                // [done] v1/schools/{ids}/studentSchoolAssociations/students/attendances                --> [done] /sections/{ids}/studentSectionAssociations/students/attendances
                // [done] v1/schools/{ids}/studentSchoolAssociations/students/courseTranscripts          --> [done] /sections/{ids}/studentSectionAssociations/students/courseTranscripts
                // [done] v1/schools/{ids}/studentSchoolAssociations/students/reportCards                --> [done] /sections/{ids}/studentSectionAssociations/students/reportCards
                // [done] v1/schools/{ids}/studentSchoolAssociations/students/studentAcademicRecords     --> [done] /sections/{ids}/studentSectionAssociations/students/studentAcademicRecords
                // [done] v1/schools/{ids}/studentSchoolAssociations/students/studentAssessments         --> [done] /sections/{ids}/studentSectionAssociations/students/studentAssessments
                // [done] v1/schools/{ids}/studentSchoolAssociations/students/studentGradebookEntries    --> [done] /sections/{ids}/studentSectionAssociations/students/studentGradebookEntries
                // [done] v1/schools/{ids}/studentSchoolAssociations/students/studentParentAssociations  --> [done] /sections/{ids}/studentSectionAssociations/students/studentParentAssociations
                // [done] v1/schools/{ids}/teacherSchoolAssociations/teachers/teacherSectionAssociations --> [done] /teachers/{id}/teacherSectionAssociations
                if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity1.equals(PathConstants.SECTIONS) && transitiveEntity2.equals(PathConstants.STUDENT_SECTION_ASSOCIATIONS) && requestedEntity.equals(PathConstants.GRADES)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/grades",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity1.equals(PathConstants.SECTIONS) && transitiveEntity2.equals(PathConstants.STUDENT_SECTION_ASSOCIATIONS) && requestedEntity.equals(PathConstants.STUDENT_COMPETENCIES)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/studentCompetencies",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity1.equals(PathConstants.STUDENT_SCHOOL_ASSOCIATIONS) && transitiveEntity2.equals(PathConstants.STUDENTS) && requestedEntity.equals(PathConstants.ATTENDANCES)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/attendances",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity1.equals(PathConstants.STUDENT_SCHOOL_ASSOCIATIONS) && transitiveEntity2.equals(PathConstants.STUDENTS) && requestedEntity.equals(PathConstants.COURSE_TRANSCRIPTS)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/courseTranscripts",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity1.equals(PathConstants.STUDENT_SCHOOL_ASSOCIATIONS) && transitiveEntity2.equals(PathConstants.STUDENTS) && requestedEntity.equals(PathConstants.REPORT_CARDS)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/reportCards",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity1.equals(PathConstants.STUDENT_SCHOOL_ASSOCIATIONS) && transitiveEntity2.equals(PathConstants.STUDENTS) && requestedEntity.equals(PathConstants.STUDENT_ACADEMIC_RECORDS)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/studentAcademicRecords",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity1.equals(PathConstants.STUDENT_SCHOOL_ASSOCIATIONS) && transitiveEntity2.equals(PathConstants.STUDENTS) && requestedEntity.equals(PathConstants.STUDENT_ASSESSMENT_ASSOCIATIONS)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/studentAssessments",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity1.equals(PathConstants.STUDENT_SCHOOL_ASSOCIATIONS) && transitiveEntity2.equals(PathConstants.STUDENTS) && requestedEntity.equals(PathConstants.STUDENT_GRADEBOOK_ENTRIES)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/studentGradebookEntries",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity1.equals(PathConstants.STUDENT_SCHOOL_ASSOCIATIONS) && transitiveEntity2.equals(PathConstants.STUDENTS) && requestedEntity.equals(PathConstants.STUDENT_PARENT_ASSOCIATIONS)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/studentParentAssociations",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                } else if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity1.equals(PathConstants.TEACHER_SCHOOL_ASSOCIATIONS) && transitiveEntity2.equals(PathConstants.TEACHERS) && requestedEntity.equals(PathConstants.TEACHER_SECTION_ASSOCIATIONS)) {
                    mutatedPath = String.format("/teachers/%s/teacherSectionAssociations", user.getEntityId());
                }

            } else if (stringifiedSegments.size() == 7) {
                String baseEntity = stringifiedSegments.get(1);
                String transitiveEntity1 = stringifiedSegments.get(3);
                String transitiveEntity2 = stringifiedSegments.get(4);
                String transitiveEntity3 = stringifiedSegments.get(5);
                String requestedEntity = stringifiedSegments.get(6);

                // v1/schools/{ids}/studentSchoolAssociations/students/studentParentAssociations/parents --> /sections/{ids}/studentSectionAssociations/students/studentParentAssociations/parents
                if (baseEntity.equals(PathConstants.SCHOOLS) && transitiveEntity1.equals(PathConstants.STUDENT_SCHOOL_ASSOCIATIONS) && transitiveEntity2.equals(PathConstants.STUDENTS) && transitiveEntity3.equals(PathConstants.STUDENT_PARENT_ASSOCIATIONS) && requestedEntity.equals(PathConstants.PARENTS)) {
                    mutatedPath = String.format("/sections/%s/studentSectionAssociations/students/studentParentAssociations/parents",
                            StringUtils.join(sectionHelper.getTeachersSections(user), ","));
                }
            }
        }  else if (isStaff(user)) {
            if (stringifiedSegments.size() == 4) {
                String baseEntity = stringifiedSegments.get(1);
                String transitiveEntityId = stringifiedSegments.get(2);
                String requestedEntity = stringifiedSegments.get(3);

                // if {id} is in original URI, rewrite will fail if more than one id was specified. if {ids}, multiple ids are allowed.
                // [done] v1/courses/{id}/courseTranscripts       --> [done] /schools/{ids}/studentSchoolAssociations/students/courseTranscripts?courseId={id}
                // [done] v1/gradingPeriods/{id}/reportCards      --> [done] /schools/{ids}/studentSchoolAssociations/students/reportCards?gradingPeriod={id}
                // [done] v1/gradingPeriods/{id}/grades           --> [done] /schools/{ids}/studentSchoolAssociations/students/grades?gradingPeriod={id}
                // [done] v1/sessions/{id}/studentAcademicRecords --> [done] /schools/{ids}/studentSchoolAssociations/students/studentAcademicRecords?sessionId={id}
                // [done] v1/sessions/{id}/sections               --> [done] /schools/{ids}/sections?sessionId={id}
                // [done] v1/assessments/{id}/studentAssessments  --> [done] /schools/{ids}/studentSchoolAssociations/students/studentAssessments?assessmentId={id}
                // [done] v1/courseOfferings/{id}/sections        --> [done] /schools/{ids}/sections?courseOfferingId={id}
                if (baseEntity.equals(PathConstants.ASSESSMENTS) && requestedEntity.equals(PathConstants.STUDENT_ASSESSMENT_ASSOCIATIONS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/schools/%s/studentSchoolAssociations/students/studentAssessments",
                            StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "assessmentId=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "assessmentId=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.COURSES) && requestedEntity.equals(PathConstants.COURSE_TRANSCRIPTS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/schools/%s/studentSchoolAssociations/students/courseTranscripts",
                            StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "courseId=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "courseId=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.COURSE_OFFERINGS) && requestedEntity.equals(PathConstants.SECTIONS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/schools/%s/sections",
                            StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "courseOfferingId=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "courseOfferingId=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.GRADING_PERIODS) && requestedEntity.equals(PathConstants.GRADES)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/schools/%s/studentSchoolAssociations/students/grades",
                            StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "gradingPeriod=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "gradingPeriod=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.GRADING_PERIODS) && requestedEntity.equals(PathConstants.REPORT_CARDS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/schools/%s/studentSchoolAssociations/students/reportCards",
                            StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "gradingPeriod=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "gradingPeriod=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.SESSIONS) && requestedEntity.equals(PathConstants.SECTIONS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/schools/%s/sections",
                            StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "sessionId=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "sessionId=" + transitiveEntityId;
                    }
                } else if (baseEntity.equals(PathConstants.SESSIONS) && requestedEntity.equals(PathConstants.STUDENT_ACADEMIC_RECORDS)) {
                    verifySingleTransitiveId(transitiveEntityId);
                    mutatedPath = String.format("/schools/%s/studentSchoolAssociations/students/studentAcademicRecords",
                            StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
                    if (mutatedParameters != null) {
                        mutatedParameters = "sessionId=" + transitiveEntityId + "&" + mutatedParameters;
                    } else {
                        mutatedParameters = "sessionId=" + transitiveEntityId;
                    }
                }
            }
        }

        return Pair.of(mutatedPath, mutatedParameters);
    }

    /**
     * Throws Response Too Large exception if there are multiple _id's specified in the transitive _id path segment.
     *
     * @param id String representing transitive _id path segment.
     * @throws ResponseTooLargeException Thrown if multiple _id's are specified (only one should be specified).
     */
    protected void verifySingleTransitiveId(String id) throws ResponseTooLargeException {
        if (id.split(",").length > 1) {
            throw new ResponseTooLargeException();
        }
    }

    /**
     * Stringifies the specified list of path segments into a list of strings.
     *
     * @param segments
     *            List of Path Segments.
     * @return List of Strings representing the input list of Path Segments.
     */
    protected List<String> stringifyPathSegments(List<PathSegment> segments) {
        List<String> stringified = new ArrayList<String>();
        if (segments != null && !segments.isEmpty()) {
            for (PathSegment segment : segments) {
                stringified.add(segment.getPath());
            }
        }
        return stringified;
    }

    /**
     * Mutates the API call (to a base entity) to a more-specific (and generally more constrained)
     * URI.
     *
     * @param resource
     *            root resource being accessed.
     * @param user
     *            entity representing user making API call.
     * @return Mutated String representing new API call, or null if no mutation takes place.
     */
    public String mutateBaseUri(String resource, Entity user) {
        String result = null;
        boolean success = true;

        if (isTeacher(user)) {
            if (ResourceNames.ASSESSMENTS.equals(resource)
                    || ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS.equals(resource)
                    || ResourceNames.COMPETENCY_LEVEL_DESCRIPTOR_TYPES.equals(resource)
                    || ResourceNames.HOME.equals(resource) || ResourceNames.LEARNINGOBJECTIVES.equals(resource)
                    || ResourceNames.LEARNINGSTANDARDS.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.ATTENDANCES.equals(resource)) {
                result = String.format("/sections/%s/studentSectionAssociations/students/attendances",
                        StringUtils.join(sectionHelper.getTeachersSections(user), ","));
            } else if (ResourceNames.COHORTS.equals(resource)) {
                result = String.format("/staff/%s/staffCohortAssociations/cohorts", user.getEntityId());
            } else if (ResourceNames.COURSES.equals(resource)) {
                result = String.format("/schools/%s/courses",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.COURSE_OFFERINGS.equals(resource)) {
                result = String.format("/schools/%s/courseOfferings",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.COURSE_TRANSCRIPTS.equals(resource)) {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                result = String.format("/sections/%s/studentSectionAssociations/students/courseTranscripts", ids);
            } else if (ResourceNames.DISCIPLINE_ACTIONS.equals(resource)) {
                result = String.format("/staff/%s/disciplineActions", user.getEntityId());
            } else if (ResourceNames.DISCIPLINE_INCIDENTS.equals(resource)) {
                result = String.format("/staff/%s/disciplineIncidents", user.getEntityId());
            } else if (ResourceNames.EDUCATION_ORGANIZATIONS.equals(resource)) {
                result = String.format("/teachers/%s/teacherSchoolAssociations/schools", user.getEntityId());
            } else if (ResourceNames.GRADES.equals(resource)) {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                result = String.format("/sections/%s/studentSectionAssociations/grades", ids);
            } else if (ResourceNames.GRADING_PERIODS.equals(resource)) {
                result = String.format("/schools/%s/sessions/gradingPeriods",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.GRADEBOOK_ENTRIES.equals(resource)) {
                result = String.format("/sections/%s/gradebookEntries",
                        StringUtils.join(sectionHelper.getTeachersSections(user), ","));
            } else if (ResourceNames.PARENTS.equals(resource)) {
                result = String.format(
                        "/sections/%s/studentSectionAssociations/students/studentParentAssociations/parents",
                        StringUtils.join(sectionHelper.getTeachersSections(user), ","));
            } else if (ResourceNames.PROGRAMS.equals(resource)) {
                result = String.format("/staff/%s/staffProgramAssociations/programs", user.getEntityId());
            } else if (ResourceNames.REPORT_CARDS.equals(resource)) {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                result = String.format("/sections/%s/studentSectionAssociations/students/reportCards", ids);
            } else if (ResourceNames.SECTIONS.equals(resource)) {
                result = String.format("/teachers/%s/teacherSectionAssociations/sections", user.getEntityId());
            } else if (ResourceNames.SCHOOLS.equals(resource)) {
                List<String> ids = edOrgHelper.getDirectSchools(user);
                result = String.format("/schools/%s", StringUtils.join(edOrgHelper.getDirectSchools(user), ","));
                success = !ids.isEmpty();
            } else if (ResourceNames.SESSIONS.equals(resource)) {
                result = String.format("/educationOrganizations/%s/sessions",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.STAFF.equals(resource)) {
                result = String.format("/educationOrganizations/%s/staffEducationOrgAssignmentAssociations/staff",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.STAFF_COHORT_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/staffCohortAssociations", user.getEntityId());
            } else if (ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS.equals(resource)) {
                result = String.format("/educationOrganizations/%s/staffEducationOrgAssignmentAssociations",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.STAFF_PROGRAM_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/staffProgramAssociations", user.getEntityId());
            } else if (ResourceNames.STUDENTS.equals(resource)) {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                result = String.format("/sections/%s/studentSectionAssociations/students", ids);
            } else if (ResourceNames.STUDENT_ACADEMIC_RECORDS.equals(resource)) {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                result = String.format("/sections/%s/studentSectionAssociations/students/studentAcademicRecords", ids);
            } else if (ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS.equals(resource)) {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                result = String.format("/sections/%s/studentSectionAssociations/students/studentAssessments", ids);
            } else if (ResourceNames.STUDENT_COMPETENCIES.equals(resource)) {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                result = String.format("/sections/%s/studentSectionAssociations/studentCompetencies", ids);
            } else if (ResourceNames.STUDENT_COHORT_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/staffCohortAssociations/cohorts/studentCohortAssociations", user.getEntityId());
            } else if (ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/disciplineIncidents/studentDisciplineIncidentAssociations", user.getEntityId());
            } else if (ResourceNames.STUDENT_COMPETENCIES.equals(resource)) {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                result = String.format("/sections/%s/studentSectionAssociations/studentCompetencies", ids);
            } else if (ResourceNames.STUDENT_COMPETENCY_OBJECTIVES.equals(resource)) {
                result = String.format("/educationOrganizations/%s/studentCompetencyObjectives",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.STUDENT_GRADEBOOK_ENTRIES.equals(resource)) {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                result = String.format("/sections/%s/studentSectionAssociations/students/studentGradebookEntries", ids);
            } else if (ResourceNames.STUDENT_PARENT_ASSOCIATIONS.equals(resource)) {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                result = String.format("/sections/%s/studentSectionAssociations/students/studentParentAssociations",
                        ids);
            } else if (ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/staffProgramAssociations/programs/studentProgramAssociations",
                        user.getEntityId());
            } else if (ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS.equals(resource)) {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                result = String
                        .format("sections/%s/studentSectionAssociations/students/studentSchoolAssociations", ids);
            } else if (ResourceNames.STUDENT_SECTION_ASSOCIATIONS.equals(resource)) {
                result = String.format("/sections/%s/studentSectionAssociations",
                        StringUtils.join(sectionHelper.getTeachersSections(user), ","));
            } else if (ResourceNames.TEACHERS.equals(resource)) {
                result = String.format("/schools/%s/teacherSchoolAssociations/teachers",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS.equals(resource)) {
                result = String.format("/teachers/%s/teacherSchoolAssociations", user.getEntityId());
            } else if (ResourceNames.TEACHER_SECTION_ASSOCIATIONS.equals(resource)) {
                result = String.format("/teachers/%s/teacherSectionAssociations", user.getEntityId());
            }
        } else if (isStaff(user)) {
            if (ResourceNames.ASSESSMENTS.equals(resource)
                    || ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS.equals(resource)
                    || ResourceNames.COMPETENCY_LEVEL_DESCRIPTOR_TYPES.equals(resource)
                    || ResourceNames.HOME.equals(resource) || ResourceNames.LEARNINGOBJECTIVES.equals(resource)
                    || ResourceNames.LEARNINGSTANDARDS.equals(resource)) {
                result = "/" + resource;
            } else if (ResourceNames.ATTENDANCES.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/studentSchoolAssociations/students/attendances", ids);
            } else if (ResourceNames.COHORTS.equals(resource)) {
                result = String.format("/staff/%s/staffCohortAssociations/cohorts", user.getEntityId());
            } else if (ResourceNames.COURSES.equals(resource)) {
                result = String.format("/schools/%s/courses",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.COURSE_OFFERINGS.equals(resource)) {
                result = String.format("/schools/%s/courseOfferings",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.COURSE_TRANSCRIPTS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/studentSchoolAssociations/students/courseTranscripts", ids);
            } else if (ResourceNames.DISCIPLINE_ACTIONS.equals(resource)) {
                result = String.format("/staff/%s/disciplineActions", user.getEntityId());
            } else if (ResourceNames.DISCIPLINE_INCIDENTS.equals(resource)) {
                result = String.format("/staff/%s/disciplineIncidents", user.getEntityId());
            } else if (ResourceNames.EDUCATION_ORGANIZATIONS.equals(resource)) {
                result = String.format("/staff/%s/staffEducationOrgAssignmentAssociations/educationOrganizations",
                        user.getEntityId());
            } else if (ResourceNames.GRADES.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/sections/studentSectionAssociations/grades", ids);
            } else if (ResourceNames.GRADING_PERIODS.equals(resource)) {
                result = String.format("/schools/%s/sessions/gradingPeriods",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.GRADEBOOK_ENTRIES.equals(resource)) {
                result = String.format("/schools/%s/sections/gradebookEntries",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.PARENTS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format(
                        "/schools/%s/studentSchoolAssociations/students/studentParentAssociations/parents", ids);
            } else if (ResourceNames.PROGRAMS.equals(resource)) {
                result = String.format("/staff/%s/staffProgramAssociations/programs", user.getEntityId());
            } else if (ResourceNames.REPORT_CARDS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/studentSchoolAssociations/students/reportCards", ids);
            } else if (ResourceNames.SCHOOLS.equals(resource)) {
                List<String> ids = edOrgHelper.getDirectSchools(user);
                result = String.format("/schools/%s", StringUtils.join(edOrgHelper.getDirectSchools(user), ","));
                success = !ids.isEmpty();
            } else if (ResourceNames.SECTIONS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/sections", ids);
            } else if (ResourceNames.SESSIONS.equals(resource)) {
                result = String.format("/educationOrganizations/%s/sessions",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.STAFF.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/educationOrganizations/%s/staffEducationOrgAssignmentAssociations/staff", ids);
            } else if (ResourceNames.STAFF_COHORT_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/staffCohortAssociations", user.getEntityId());
            } else if (ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/staffEducationOrgAssignmentAssociations", user.getEntityId());
            } else if (ResourceNames.STAFF_PROGRAM_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/staffProgramAssociations", user.getEntityId());
            } else if (ResourceNames.STUDENTS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/studentSchoolAssociations/students", ids);
            } else if (ResourceNames.STUDENT_ACADEMIC_RECORDS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/studentSchoolAssociations/students/studentAcademicRecords", ids);
            } else if (ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/studentSchoolAssociations/students/studentAssessments", ids);
            } else if (ResourceNames.STUDENT_COHORT_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/staffCohortAssociations/cohorts/studentCohortAssociations", user.getEntityId());
            } else if (ResourceNames.STUDENT_COMPETENCIES.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/sections/studentSectionAssociations/studentCompetencies", ids);
            } else if (ResourceNames.STUDENT_COMPETENCY_OBJECTIVES.equals(resource)) {
                result = String.format("/educationOrganizations/%s/studentCompetencyObjectives",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/disciplineIncidents/studentDisciplineIncidentAssociations", user.getEntityId());
            } else if (ResourceNames.STUDENT_GRADEBOOK_ENTRIES.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/studentSchoolAssociations/students/studentGradebookEntries", ids);
            } else if (ResourceNames.STUDENT_PARENT_ASSOCIATIONS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/studentSchoolAssociations/students/studentParentAssociations", ids);
            } else if (ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS.equals(resource)) {
                result = String.format("/staff/%s/staffProgramAssociations/programs/studentProgramAssociations",
                        user.getEntityId());
            } else if (ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/studentSchoolAssociations", ids);
            } else if (ResourceNames.STUDENT_SECTION_ASSOCIATIONS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/sections/studentSectionAssociations", ids);
            } else if (ResourceNames.TEACHERS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/teacherSchoolAssociations/teachers", ids);
            } else if (ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String.format("/schools/%s/teacherSchoolAssociations", ids);
            } else if (ResourceNames.PROGRAMS.equals(resource)) {
                result = String.format("/staff/%s/staffProgramAssociations/programs", user.getEntityId());
            } else if (ResourceNames.PARENTS.equals(resource)) {
                result = String.format(
                        "/schools/%s/studentSchoolAssociations/students/studentParentAssociations/parents",
                        StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ","));
            } else if (ResourceNames.TEACHER_SECTION_ASSOCIATIONS.equals(resource)) {
                String ids = StringUtils.join(edOrgHelper.getDirectEdOrgAssociations(user), ",");
                result = String
                        .format("/schools/%s/teacherSchoolAssociations/teachers/teacherSectionAssociations", ids);
            }

        }

        if (!success) {
            error("Context Inferrence Failed");
            throw new ContextInferrenceFailedException();
        }

        return result;
    }

    /**
     * Determines if the entity is a teacher.
     *
     * @param principal
     *            User making API call.
     * @return True if principal is a teacher, false otherwise.
     */
    private boolean isTeacher(Entity principal) {
        return principal.getType().equals(EntityNames.TEACHER);
    }

    /**
     * Determines if the entity is a staff member.
     *
     * @param principal
     *            User making API call.
     * @return True if principal is a staff member, false otherwise.
     */
    private boolean isStaff(Entity principal) {
        return principal.getType().equals(EntityNames.STAFF);
    }

    /**
     * Inject section helper (for unit testing).
     *
     * @param sectionHelper
     *            resolver for tying entity to sections.
     */
    protected void setSectionHelper(SectionHelper sectionHelper) {
        this.sectionHelper = sectionHelper;
    }

    /**
     * Inject education organization helper (for unit testing).
     *
     * @param edOrgHelper
     *            resolver for tying entity to education organizations.
     */
    protected void setEdOrgHelper(EdOrgHelper edOrgHelper) {
        this.edOrgHelper = edOrgHelper;
    }
}
