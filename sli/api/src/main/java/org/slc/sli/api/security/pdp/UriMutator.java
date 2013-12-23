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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ws.rs.core.PathSegment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.api.exceptions.UriMutationException;
import org.slc.sli.api.resources.security.ApplicationResource;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.domain.enums.Right;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.BasicDefinitionStore;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.ResponseTooLargeException;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.GradingPeriodHelper;
import org.slc.sli.api.security.context.resolver.SectionHelper;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SessionUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Infers context about the {user,requested resource} pair, and restricts blanket API calls to
 * smaller (and generally more manageable) scope.
 */
@Component
public class UriMutator {
    
    private static final Logger LOG = LoggerFactory.getLogger(UriMutator.class);

    public static final int NUM_SEGMENTS_IN_TWO_PART_REQUEST = 3;
    public static final int NUM_SEGMENTS_IN_ONE_PART_REQUEST = 2;

    @Resource
    private EdOrgHelper edOrgHelper;

    @Resource
    private SectionHelper sectionHelper;

    @Resource
    private RootSearchMutator rootSearchMutator;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    private BasicDefinitionStore definitionStore;

    @Autowired
    private DateHelper dateHelper;

    private Map<String, MutateInfo> teacherSectionMutations;

    @Autowired
    private GradingPeriodHelper gradingPeriodHelper;

    @SuppressWarnings("unchecked")
    private static final List<Pair<String, String>> PARAMETER_RESOURCE_PAIRS = Arrays.asList(
            Pair.of(ParameterConstants.STUDENT_UNIQUE_STATE_ID, ResourceNames.STUDENTS),
            Pair.of(ParameterConstants.STAFF_UNIQUE_STATE_ID, ResourceNames.STAFF),
            Pair.of(ParameterConstants.PARENT_UNIQUE_STATE_ID, ResourceNames.PARENTS),
            Pair.of(ParameterConstants.STATE_ORGANIZATION_ID, ResourceNames.EDUCATION_ORGANIZATIONS));

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
     * @return MutatedContainer representing {mutated path (if necessary), mutated
     *         parameters (if necessary)}, where path will be null or parameters the empty string if they didn't need
     *         to be rewritten.
     */
    public MutatedContainer mutate(List<PathSegment> segments, String queryParameters, SLIPrincipal principal, String clientId) {
        Entity user = principal.getEntity();
        MutatedContainer mutated = new MutatedContainer();
        mutated.setQueryParameters(queryParameters);
        if (mutated.getQueryParameters() == null) {
            mutated.setQueryParameters("");
        }

        Map<String, String> parameters = MutatorUtil.getParameterMap(mutated.getQueryParameters());
        for (Pair<String, String> parameterResourcePair : PARAMETER_RESOURCE_PAIRS) {
            String parameter = parameterResourcePair.getLeft();
            String resource = parameterResourcePair.getRight();
            if (parameters.containsKey(parameter)) {
                EntityDefinition definition = definitionStore.lookupByResourceName(resource);
                if (definition != null) {
                    NeutralQuery query = new NeutralQuery(new NeutralCriteria(parameter,
                            NeutralCriteria.OPERATOR_EQUAL, parameters.get(parameter)));
                    Entity e = repo.findOne(definition.getType(), query);
                    if (e != null) {
                        MutatedContainer newMutated = new MutatedContainer();
                        String path = String.format("/%s/%s", resource, e.getEntityId());
                        if (EntityNames.TEACHER.equals(e.getType())) {
                            path = String.format("/teachers/%s", e.getEntityId());
                        } else if (EntityNames.STAFF.equals(e.getType())) {
                            path = String.format("/staff/%s", e.getEntityId());
                        }
                        newMutated.setPath(path);

                        LOG.info("Rewriting URI to {} based on natural keys", newMutated.getPath());
                        return newMutated;
                    }
                }
            }
        }

        mutated.setPath(null);
        mutated.setQueryParameters(queryParameters);
        MutatedContainer generalMutation = doGeneralMutations(stringifyPathSegments(segments), queryParameters, user);

        if( generalMutation != null) {
          mutated = generalMutation;
        }
        else if (segments.size() < NUM_SEGMENTS_IN_TWO_PART_REQUEST) {

            if (!shouldSkipMutation(segments, mutated.getQueryParameters(), principal, clientId)) {
                if (segments.size() == 1) {
                    // api/v1
                    mutated = mutateBaseUri(segments.get(0).getPath(), ResourceNames.HOME,
                            mutated.getQueryParameters(), user);
                } else {
                    mutated = mutateBaseUri(segments.get(0).getPath(), segments.get(1).getPath(),
                            mutated.getQueryParameters(), user);
                }
            }
        } else {
            mutated = mutateUriAsNecessary(segments, mutated.getQueryParameters(), user);
        }

        return mutated;
    }

    private Set<String> publicResourcesThatAllowSearch;

    @PostConstruct
    void init() {
        publicResourcesThatAllowSearch = new HashSet<String>(Arrays.asList(ResourceNames.EDUCATION_ORGANIZATIONS,
                ResourceNames.SCHOOLS));

        teacherSectionMutations = new HashMap<String, MutateInfo>() {
            {
                // TWO TYPE
                put(joinPathSegments(PathConstants.ASSESSMENTS, PathConstants.STUDENT_ASSESSMENTS), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/studentAssessments", "assessmentId"));
                put(joinPathSegments(PathConstants.COURSES, PathConstants.COURSE_TRANSCRIPTS), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/studentAcademicRecords/courseTranscripts",
                        "courseId"));
                put(joinPathSegments(PathConstants.COURSE_OFFERINGS, PathConstants.SECTIONS), new MutateInfo(
                        "/sections/%s/", "courseOfferingId"));
                put(joinPathSegments(PathConstants.SESSIONS, PathConstants.SECTIONS), new MutateInfo("/sections/%s/",
                        "sessionId"));
                put(joinPathSegments(PathConstants.LEARNING_OBJECTIVES, PathConstants.STUDENT_COMPETENCIES),
                        new MutateInfo("/sections/%s/studentSectionAssociations/studentCompetencies",
                                "objectiveId.learningObjectiveId"));
                put(joinPathSegments(PathConstants.GRADING_PERIODS, PathConstants.REPORT_CARDS), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/reportCards", "gradingPeriodId"));
                put(joinPathSegments(PathConstants.GRADING_PERIODS, PathConstants.GRADES), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/grades", "gradingPeriodId"));
                put(joinPathSegments(PathConstants.SESSIONS, PathConstants.STUDENT_ACADEMIC_RECORDS), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/studentAcademicRecords", "sessionId"));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/studentSchoolAssociations", null));
                put(joinPathSegments(PathConstants.EDUCATION_ORGANIZATIONS, PathConstants.COHORTS), new MutateInfo(
                        "/teachers/%s/staffCohortAssociations/cohorts", "educationOrgId", true));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.SECTIONS), new MutateInfo(
                        "/teachers/%s/teacherSectionAssociations/sections", null, true));
                put(joinPathSegments(PathConstants.EDUCATION_ORGANIZATIONS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS),
                        new MutateInfo("/sections/%s/studentSectionAssociations/students/studentSchoolAssociations",
                                null));

                // THREE TYPE
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.SECTIONS, PathConstants.GRADEBOOK_ENTRIES),
                        new MutateInfo("/sections/%s/gradebookEntries", null));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.SECTIONS,
                        PathConstants.STUDENT_SECTION_ASSOCIATIONS), new MutateInfo(
                        "/sections/%s/studentSectionAssociations", null));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS,
                        PathConstants.STUDENTS), new MutateInfo("/sections/%s/studentSectionAssociations/students",
                        null));
                put(joinPathSegments(PathConstants.EDUCATION_ORGANIZATIONS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS,
                        PathConstants.STUDENTS), new MutateInfo("/sections/%s/studentSectionAssociations/students",
                        null));

                // FOUR TYPE
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.SECTIONS,
                        PathConstants.STUDENT_SECTION_ASSOCIATIONS, PathConstants.GRADES), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/grades", null));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.SECTIONS,
                        PathConstants.STUDENT_SECTION_ASSOCIATIONS, PathConstants.STUDENT_COMPETENCIES),
                        new MutateInfo("/sections/%s/studentSectionAssociations/studentCompetencies", null));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS,
                        PathConstants.STUDENTS, PathConstants.ATTENDANCES), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/attendances", null));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS,
                        PathConstants.STUDENTS, PathConstants.COURSE_TRANSCRIPTS), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/studentAcademicRecords/courseTranscripts",
                        null));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS,
                        PathConstants.STUDENTS, PathConstants.REPORT_CARDS), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/reportCards", null));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS,
                        PathConstants.STUDENTS, PathConstants.STUDENT_ACADEMIC_RECORDS), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/studentAcademicRecords", null));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS,
                        PathConstants.STUDENTS, PathConstants.STUDENT_ASSESSMENTS), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/studentAssessments", null));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS,
                        PathConstants.STUDENTS, PathConstants.STUDENT_GRADEBOOK_ENTRIES), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/studentGradebookEntries", null));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS,
                        PathConstants.STUDENTS, PathConstants.STUDENT_PARENT_ASSOCIATIONS), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/studentParentAssociations", null));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.TEACHER_SCHOOL_ASSOCIATIONS,
                        PathConstants.TEACHERS, PathConstants.TEACHER_SECTION_ASSOCIATIONS), new MutateInfo(
                        "/teachers/%s/teacherSectionAssociations", null, true));

                // FIVE TYPE
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS,
                        PathConstants.STUDENTS, PathConstants.STUDENT_PARENT_ASSOCIATIONS, PathConstants.PARENTS),
                        new MutateInfo(
                                "/sections/%s/studentSectionAssociations/students/studentParentAssociations/parents",
                                null));
                put(joinPathSegments(PathConstants.SCHOOLS, PathConstants.STUDENT_SCHOOL_ASSOCIATIONS,
                        PathConstants.STUDENTS, PathConstants.STUDENT_ACADEMIC_RECORDS,
                        PathConstants.COURSE_TRANSCRIPTS), new MutateInfo(
                        "/sections/%s/studentSectionAssociations/students/studentAcademicRecords/courseTranscripts",
                        null));
            }
        };

    }

    private boolean shouldSkipMutation(List<PathSegment> segments, String queryParameters, SLIPrincipal principal, String clientId) {
        return shouldSkipMutationToEnableSearch(segments, queryParameters) ||
                ( SessionUtil.isAdminApp(clientId,repo) && hasAppAuthRight(principal) && isEducationOrganizationsEndPoint(segments) );
    }



    // Check whether the principal has the 'APP_AUTHORIZE' right for any of its roles in any edorg
    private boolean hasAppAuthRight(SLIPrincipal principal) {
        return principal.getAllRights(true).contains(Right.APP_AUTHORIZE);
    }

    // Check whether the 'educationOrganizations' endpoint is hit directly
    private boolean isEducationOrganizationsEndPoint(List<PathSegment> segments) {
        boolean skipMutation = false;
        if (segments.size() == NUM_SEGMENTS_IN_ONE_PART_REQUEST) {
            final int baseResourceIndex = 1;
            if (ResourceNames.EDUCATION_ORGANIZATIONS.equals(segments.get(baseResourceIndex).getPath())) {
                skipMutation = true;
            }
        }
        return skipMutation;
    }

    private boolean shouldSkipMutationToEnableSearch(List<PathSegment> segments, String queryParameters) {
        boolean skipMutation = false;
        if (segments.size() == NUM_SEGMENTS_IN_ONE_PART_REQUEST) {
            String[] queries = queryParameters != null ? queryParameters.split("&") : new String[0];
            for (String query : queries) {
                if (!query
                        .matches("(limit|offset|expandDepth|includeFields|excludeFields|sortBy|sortOrder|optionalFields|views|includeCustom|selector)=.+")) {
                    final int baseResourceIndex = 1;
                    if (publicResourcesThatAllowSearch.contains(segments.get(baseResourceIndex).getPath())) {
                        skipMutation = true;
                        break;
                    }
                }
            }

        }
        return skipMutation;
    }

    private class MutateInfo {
        String mutatedPathFormat;
        String mutatedParameter;
        boolean usePrincipleId;

        private MutateInfo(String mutatedPathFormat, String mutatedParameter, boolean usePrincipleId) {
            this.mutatedPathFormat = mutatedPathFormat;
            this.mutatedParameter = mutatedParameter;
            this.usePrincipleId = usePrincipleId;
        }

        private MutateInfo(String mutatedPathFormat, String mutatedParameter) {
            this(mutatedPathFormat, mutatedParameter, false);
        }

        public String getMutatedPathFormat() {
            return mutatedPathFormat;
        }

        public String getMutatedParameter() {
            return mutatedParameter;
        }

        public boolean isUsePrincipleId() {
            return usePrincipleId;
        }
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
     * @return MutatedContainer representing {mutated path (if necessary), mutated
     *         parameters (if necessary)}, where path or parameters will be null if they didn't need
     *         to be rewritten.
     */
    private MutatedContainer mutateUriAsNecessary(List<PathSegment> segments, String queryParameters, Entity user)
            throws ResponseTooLargeException {
        MutatedContainer mutatedPathAndParameters = null;
        if (mutateToTeacher()) {
            mutatedPathAndParameters = mutateTeacherRequest(segments, queryParameters, user);
        } else if (mutateToStaff()) {
            mutatedPathAndParameters = mutateStaffRequest(segments, queryParameters, user);
        } else if (isStudent(user) || isParent(user)) {
            mutatedPathAndParameters = mutateStudentParentRequest(stringifyPathSegments(segments), queryParameters,
                    user);
        }

        return mutatedPathAndParameters;
    }

    private MutatedContainer generateMutatedUrl(String queryParameters, String format, String... args) {
        MutatedContainer mutated = new MutatedContainer();
        mutated.setQueryParameters(queryParameters != null ? queryParameters : "");
        String mutatedURL = String.format(format, args);
        mutated.setPath(mutatedURL);
        return mutated;
    }

    private MutatedContainer doGeneralMutations(List<String> segmentStrings, String queryParameters, Entity user) {
        MutatedContainer mutated = null;
        int segmentSize = segmentStrings.size();

        if(segmentSize == 2) {
            String baseEntity = segmentStrings.get(1);
            //does following mutation
            //v1.2/calendarDates   ->   v1.2/calendarDates/<user's edOrg Ids>
            if (baseEntity.equals("calendarDates")) {
                mutated = generateMutatedUrl(queryParameters, "/educationOrganizations/%s/calendarDates", StringUtils.join(edOrgHelper.getDirectEdorgs(user), ","));
            } else if (baseEntity.equals(PathConstants.CLASS_PERIODS)) {
                mutated = generateMutatedUrl(queryParameters, "/educationOrganizations/%s/classPeriods", StringUtils.join(edOrgHelper.getDirectEdorgs(user), ","));
            } else if (baseEntity.equals(PathConstants.BELL_SCHEDULES)) {
                mutated = generateMutatedUrl(queryParameters, "/educationOrganizations/%s/bellSchedules", StringUtils.join(edOrgHelper.getDirectEdorgs(user), ","));
            }
        }
        else if (segmentSize == 4) {
            String baseEntity = segmentStrings.get(1);
            String resourceEntity = segmentStrings.get(3);
            //does following mutation
            //v1.2/gradingPeriods/b62056a2f0463864ed2d81dd6ce121c1b7b8d950_id/calendarDates   ->   v1.2/calendarDates/521570eb6dc0e988c4553b91c6c4dadc2a02c487_id
            //where 521570eb6dc0e988c4553b91c6c4dadc2a02c487_id is the calendarDateReference inside gradingPeriod b62056a2f0463864ed2d81dd6ce121c1b7b8d950_id
            //The ThreePartResource.java does not work for this relationship because it wrongly assumes that calendarDate contains a reference to gradingPeriod.
            if(baseEntity.equals("gradingPeriods") && resourceEntity.equals("calendarDates")) {
                String [] gradingPeriodIds = segmentStrings.get(2).split(",");
                List<String> gradingPeriodCalendarDates = gradingPeriodHelper.getCalendarDatesForGradingPeriods(queryParameters, gradingPeriodIds);

                if(gradingPeriodCalendarDates.size() > 0) {
                    mutated = new MutatedContainer();
                    mutated.setQueryParameters(queryParameters != null ? queryParameters : "");
                    String mutatedURL = String.format("/calendarDates/%s", StringUtils.join(gradingPeriodCalendarDates.toArray(), ","));
                    mutated.setPath(mutatedURL);
                }   else {    //no calendarDates associated with this grading Period
                    LOG.info("Cannot find any calendarDates associated with gradingPeriods [{}]", Arrays.asList(gradingPeriodIds));
                }
            }
        }
        return mutated;
    }


    private MutatedContainer mutateStudentParentRequest(List<String> segmentStrings, String queryParameters, Entity user) {
        MutatedContainer mutated = new MutatedContainer();
        mutated.setQueryParameters(queryParameters != null ? queryParameters : "");
        SLIPrincipal principal = SecurityUtil.getSLIPrincipal();

        if (segmentStrings.size() == 2) {
            String baseEntity = segmentStrings.get(1);

            if (PathConstants.COURSES.equals(baseEntity)) {
                mutated.setPath(String.format("/schools/%s/courses",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
            } else if (Arrays.asList(PathConstants.SCHOOLS, PathConstants.EDUCATION_ORGANIZATIONS).contains(baseEntity)) {
                mutated.setPath(String.format("/schools/%s", StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
            } else if (PathConstants.COURSE_OFFERINGS.equals(baseEntity)) {
                mutated.setPath(String.format("/schools/%s/courseOfferings",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
            } else if (PathConstants.SESSIONS.equals(baseEntity)) {
                mutated.setPath(String.format("/schools/%s/sessions",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
            } else if (PathConstants.GRADING_PERIODS.equals(baseEntity)) {
                mutated.setPath(String.format("/schools/%s/sessions/gradingPeriods",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
            } else if (PathConstants.GRADUATION_PLANS.equals(baseEntity)) {
                mutated.setPath(String.format("/schools/%s/graduationPlans",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
            } else if (Arrays.asList(PathConstants.LEARNING_STANDARDS, PathConstants.LEARNING_OBJECTIVES).contains(
                    baseEntity)) {
                mutated.setPath(String.format("/search/%s", baseEntity));
            } else if (PathConstants.STUDENT_COMPETENCY_OBJECTIVES.equals(baseEntity)) {
                mutated.setPath(String.format("/educationOrganizations/%s/studentCompetencyObjectives",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
            } else if (PathConstants.ASSESSMENTS.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/studentAssessments/assessments", getStudentIds(principal)));
            } else if (PathConstants.ATTENDANCES.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/attendances", getStudentIds(principal)));
            } else if (PathConstants.COHORTS.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/studentCohortAssociations/cohorts",
                        getStudentIds(principal)));
            } else if (PathConstants.COURSE_TRANSCRIPTS.equals(baseEntity)) {
                mutated.setPath(String.format("/studentAcademicRecords/%s/courseTranscripts",
                        getStudentAcademicRecordsIds(user)));
            } else if (PathConstants.GRADES.equals(baseEntity)) {
                mutated.setPath(String.format("/studentSectionAssociations/%s/grades",
                        getStudentSectionAssocIds(principal)));
            } else if (PathConstants.GRADEBOOK_ENTRIES.equals(baseEntity)) {
                mutated.setPath(String.format("/sections/%s/gradebookEntries", getSectionIds(principal)));
            } else if (PathConstants.PARENTS.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/studentParentAssociations/parents",
                        getStudentIds(principal)));
            } else if (PathConstants.PROGRAMS.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/studentProgramAssociations/programs",
                        getStudentIds(principal)));
            } else if (PathConstants.SECTIONS.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/studentSectionAssociations/sections",
                        getStudentIds(principal)));
            } else if (PathConstants.REPORT_CARDS.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/reportCards", getStudentIds(principal)));
            } else if (PathConstants.TEACHERS.equals(baseEntity)) {
                mutated.setPath(String.format("/sections/%s/teacherSectionAssociations/teachers",
                        getSectionIds(principal)));
            } else if (PathConstants.TEACHER_SECTION_ASSOCIATIONS.equals(baseEntity)) {
                mutated.setPath(String.format("/sections/%s/teacherSectionAssociations", getSectionIds(principal)));
            } else if (PathConstants.TEACHER_SCHOOL_ASSOCIATIONS.equals(baseEntity)) {
                mutated.setPath(String.format("/schools/%s/teacherSchoolAssociations",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
            } else if (PathConstants.STAFF.equals(baseEntity)) {
                mutated.setPath(String.format(
                        "/educationOrganizations/%s/staffEducationOrgAssignmentAssociations/staff",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
            } else if (PathConstants.STAFF_COHORT_ASSOCIATIONS.equals(baseEntity)) {
                mutated.setPath(String.format("/cohorts/%s/staffCohortAssociations", getCohortIds(principal)));
            } else if (PathConstants.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS.equals(baseEntity)) {
                mutated.setPath(String.format("/educationOrganizations/%s/staffEducationOrgAssignmentAssociations",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
            } else if (PathConstants.STAFF_PROGRAM_ASSOCIATIONS.equals(baseEntity)) {
                mutated.setPath(String.format("/programs/%s/staffProgramAssociations", getProgramIds(principal)));
            } else if (PathConstants.STUDENTS.equals(baseEntity)) {
                if (isStudent(user)) {
                    mutated.setPath(String.format("/sections/%s/studentSectionAssociations/students",
                            getSectionIds(principal)));
                } else if (isParent(user)) {
                    mutated.setPath(String.format("/parents/%s/studentParentAssociations/students", user.getEntityId()));
                }
            } else if (PathConstants.STUDENT_ACADEMIC_RECORDS.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/studentAcademicRecords", getStudentIds(principal)));
            } else if (PathConstants.STUDENT_ASSESSMENTS.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/studentAssessments", getStudentIds(principal)));
            } else if (PathConstants.STUDENT_COHORT_ASSOCIATIONS.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/studentCohortAssociations", getStudentIds(principal)));
            } else if (PathConstants.STUDENT_COMPETENCIES.equals(baseEntity)) {
                mutated.setPath(String.format("/studentSectionAssociations/%s/studentCompetencies",
                        getStudentSectionAssocIds(principal)));
            } else if (PathConstants.STUDENT_GRADEBOOK_ENTRIES.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/studentGradebookEntries",
                        StringUtils.join(getStudentIds(principal))));
            } else if (PathConstants.STUDENT_PARENT_ASSOCIATIONS.equals(baseEntity)) {
                if (isStudent(user)) {
                    mutated.setPath(String.format("/students/%s/studentParentAssociations",
                            StringUtils.join(getStudentIds(principal))));
                } else if (isParent(user)) {
                    mutated.setPath(String.format("/parents/%s/studentParentAssociations", user.getEntityId()));
                }
            } else if (PathConstants.STUDENT_PROGRAM_ASSOCIATIONS.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/studentProgramAssociations",
                        StringUtils.join(getStudentIds(principal))));
            } else if (PathConstants.STUDENT_SCHOOL_ASSOCIATIONS.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/studentSchoolAssociations",
                        StringUtils.join(getStudentIds(principal))));
            } else if (PathConstants.STUDENT_SECTION_ASSOCIATIONS.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/studentSectionAssociations",
                        StringUtils.join(getStudentIds(principal))));
            } else if (PathConstants.YEARLY_ATTENDANCES.equals(baseEntity)) {
                mutated.setPath(String.format("/students/%s/yearlyAttendances",
                        StringUtils.join(getStudentIds(principal))));
            } else if (PathConstants.DISCIPLINE_ACTIONS.equals(baseEntity)) {
                throw new APIAccessDeniedException("Students do not have access to discipline actions.", true);
            } else if (PathConstants.DISCIPLINE_INCIDENTS.equals(baseEntity)) {
                throw new APIAccessDeniedException("Students do not have access to discipline incidents.", true);
            } else if (PathConstants.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS.equals(baseEntity)) {
                throw new APIAccessDeniedException("Students do not have access to discipline incident associations.", true);
            } else if (ResourceNames.HOME.equals(baseEntity)) {
                mutated.setPath("/home");
            } else if (ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS.equals(baseEntity)) {
                mutated.setPath("/search/" + baseEntity);
            } else {
                throw new IllegalArgumentException("Not supported yet...");
            }
        } else if (segmentStrings.size() >= 4) {
            String baseEntity = segmentStrings.get(1);
            String secondEntity = segmentStrings.get(3);
            if (ResourceNames.STUDENT_SECTION_ASSOCIATIONS.equals(segmentStrings.get(1))) {
                // /studentSectionAssociation/{id}/students and
                // /studentSectionAssociation/{id}/sections
                // have to be re-written to /section/{id}/*
                if (ResourceNames.STUDENTS.equals(secondEntity) || ResourceNames.SECTIONS.equals(secondEntity)) {
                    List<String> sections = sectionHelper.getSectionsFromStudentSectionAssociation(segmentStrings.get(2)
                            .split(","));
                    String sectionUri = String.format("/sections/%s",
                            StringUtils.join(sections.toArray(new String[0]), ","));
                    if (ResourceNames.STUDENTS.equals(secondEntity)) {
                        sectionUri = sectionUri + "/studentSectionAssociations/students";
                    }
                    mutated.setPath(sectionUri);
                }
            } else if (ResourceNames.LEARNINGOBJECTIVES.equals(baseEntity) && ResourceNames.STUDENT_COMPETENCIES.equals(secondEntity)) {
                throw new APIAccessDeniedException("url is not accessible to students or parents", true);
            }
        }
        return mutated;
    }

    private MutatedContainer mutateTeacherRequest(List<PathSegment> segments, String queryParameters, Entity user) {
        MutatedContainer mutated = new MutatedContainer();
        mutated.setQueryParameters(queryParameters);

        List<String> segmentStrings = stringifyPathSegments(segments);
        String joinedSegments = null;
        String baseEntityIds = null;

        if (segmentStrings.size() > NUM_SEGMENTS_IN_TWO_PART_REQUEST) {

            int ENTITY_IDS_SEGMENT_INDEX = 2;
            int API_VERSION_SEGMENT_INDEX = 0;
            baseEntityIds = segmentStrings.get(ENTITY_IDS_SEGMENT_INDEX);
            segmentStrings.remove(ENTITY_IDS_SEGMENT_INDEX);
            segmentStrings.remove(API_VERSION_SEGMENT_INDEX);
            joinedSegments = joinPathSegments(segmentStrings);
        }

        if (joinedSegments != null) {
            MutateInfo mutateInfo = teacherSectionMutations.get(joinedSegments);
            if (mutateInfo != null) {
                String ids;
                if (mutateInfo.usePrincipleId) {
                    ids = user.getEntityId();
                } else {
                    ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                }
                mutated.setPath(String.format(mutateInfo.getMutatedPathFormat(), ids));

                if (mutateInfo.getMutatedParameter() != null) {
                    verifySingleTransitiveId(baseEntityIds);
                    mutated.setQueryParameters(mutuateQueryParameterString(mutateInfo.getMutatedParameter(),
                            baseEntityIds, queryParameters));
                }
            }
        }
        return mutated;
    }

    private MutatedContainer mutateStaffRequest(List<PathSegment> segments, String queryParameters, Entity user) {

        MutatedContainer mutated = new MutatedContainer();
        mutated.setQueryParameters(queryParameters != null ? queryParameters : "");

        List<String> segmentStrings = stringifyPathSegments(segments);
        if (segmentStrings.size() == 4) {
            String baseEntity = segmentStrings.get(1);
            String transitiveEntityId = segmentStrings.get(2);
            String requestedEntity = segmentStrings.get(3);

            String modifiedRequest = reconnectPathSegments(Arrays.asList(baseEntity, requestedEntity));
            if (modifiedRequest.equals(PathConstants.ASSESSMENTS + ";" + PathConstants.STUDENT_ASSESSMENTS + ";")) {
                verifySingleTransitiveId(transitiveEntityId);
                mutated.setPath(String.format("/schools/%s/studentSchoolAssociations/students/studentAssessments",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
                mutated.setQueryParameters(mutuateQueryParameterString("assessmentId", transitiveEntityId,
                        mutated.getQueryParameters()));
            } else if (modifiedRequest.equals(PathConstants.COURSES + ";" + PathConstants.COURSE_TRANSCRIPTS + ";")) {
                verifySingleTransitiveId(transitiveEntityId);
                mutated.setPath(String.format(
                        "/schools/%s/studentSchoolAssociations/students/studentAcademicRecords/courseTranscripts",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
                mutated.setQueryParameters(mutuateQueryParameterString("courseId", transitiveEntityId,
                        mutated.getQueryParameters()));
            } else if (modifiedRequest.equals(PathConstants.COURSE_OFFERINGS + ";" + PathConstants.SECTIONS + ";")) {
                verifySingleTransitiveId(transitiveEntityId);
                mutated.setPath(String.format("/schools/%s/sections",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
                mutated.setQueryParameters(mutuateQueryParameterString("courseOfferingId", transitiveEntityId,
                        mutated.getQueryParameters()));
            } else if (modifiedRequest.equals(PathConstants.GRADING_PERIODS + ";" + PathConstants.GRADES + ";")) {
                verifySingleTransitiveId(transitiveEntityId);
                mutated.setPath(String.format("/schools/%s/sections/studentSectionAssociations/grades",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
                mutated.setQueryParameters(mutuateQueryParameterString("gradingPeriodId", transitiveEntityId,
                        mutated.getQueryParameters()));
            } else if (modifiedRequest.equals(PathConstants.GRADING_PERIODS + ";" + PathConstants.REPORT_CARDS + ";")) {
                verifySingleTransitiveId(transitiveEntityId);
                mutated.setPath(String.format("/schools/%s/studentSchoolAssociations/students/reportCards",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
                mutated.setQueryParameters(mutuateQueryParameterString("gradingPeriodId", transitiveEntityId,
                        mutated.getQueryParameters()));
            } else if (modifiedRequest.equals(PathConstants.SESSIONS + ";" + PathConstants.SECTIONS + ";")) {
                verifySingleTransitiveId(transitiveEntityId);
                mutated.setPath(String.format("/schools/%s/sections",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
                mutated.setQueryParameters(mutuateQueryParameterString("sessionId", transitiveEntityId,
                        mutated.getQueryParameters()));
            } else if (modifiedRequest.equals(PathConstants.SESSIONS + ";" + PathConstants.STUDENT_ACADEMIC_RECORDS
                    + ";")) {
                verifySingleTransitiveId(transitiveEntityId);
                mutated.setPath(String.format("/schools/%s/studentSchoolAssociations/students/studentAcademicRecords",
                        StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
                mutated.setQueryParameters(mutuateQueryParameterString("sessionId", transitiveEntityId,
                        mutated.getQueryParameters()));
            } else if (modifiedRequest.equals(PathConstants.LEARNING_OBJECTIVES + ";"
                    + PathConstants.STUDENT_COMPETENCIES + ";")) {
                verifySingleTransitiveId(transitiveEntityId);
                mutated.setPath(String.format("/schools/%s/sections/studentSectionAssociations/studentCompetencies",
                        StringUtils.join(edOrgHelper.getStaffEdOrgsAndChildren(), ",")));
                mutated.setQueryParameters(mutuateQueryParameterString("learningObjectiveId", transitiveEntityId,
                        mutated.getQueryParameters()));
            }
        }
        return mutated;
    }

    /**
     * Joins a list of path segments and returns a string representing the path traversed.
     *
     * @param segments
     *            List of Strings representing Path Segments.
     * @return String representing the list of Path Segments.
     */
    protected String joinPathSegments(String... segments) {
        return joinPathSegments(Arrays.asList(segments));
    }

    /**
     * Joins a list of path segments and returns a string representing the path traversed.
     *
     * @param segments
     *            List of Strings representing Path Segments.
     * @return String representing the list of Path Segments.
     */
    protected String joinPathSegments(List<String> segments) {
        StringBuilder builder = new StringBuilder();
        boolean firstSegment = true;
        for (String segment : segments) {
            if (firstSegment) {
                firstSegment = false;
            } else {
                builder.append(";");
            }
            builder.append(segment);
        }
        return builder.toString();
    }

    /**
     * Reconnects a list of path segments and returns a string representing the path traversed.
     *
     * @param segments
     *            List of Strings representing Path Segments.
     * @return String representing the list of Path Segments.
     */
    protected String reconnectPathSegments(List<String> segments) {
        StringBuilder builder = new StringBuilder();
        for (String segment : segments) {
            builder.append(segment).append(";");
        }
        return builder.toString();
    }

    /**
     * Mutates the existing query parameter string by pre-pending the _id of the transitive entity
     * that's part of the rewritten URI.
     *
     * @param transitiveEntityField
     *            Field used to identify transitive entity.
     * @param transitiveEntityId
     *            UUID of the transitive entity.
     * @param existingParameters
     *            Existing query parameter string.
     * @return String representing new query parameter string.
     */
    protected String mutuateQueryParameterString(String transitiveEntityField, String transitiveEntityId,
            String existingParameters) {
        if (existingParameters != null) {
            return transitiveEntityField + "=" + transitiveEntityId + "&" + existingParameters;
        } else {
            return transitiveEntityField + "=" + transitiveEntityId;
        }
    }

    /**
     * Throws Response Too Large exception if there are multiple _id's specified in the transitive
     * _id path segment.
     *
     * @param id
     *            String representing transitive _id path segment.
     * @throws ResponseTooLargeException
     *             Thrown if multiple _id's are specified (only one should be specified).
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

    private MutatedContainer mutateBaseUriForTeacher(String resource, String mutatedParameters, Entity user) {

        MutatedContainer mutated = new MutatedContainer();
        mutated.setQueryParameters(mutatedParameters);
        if (ResourceNames.LEARNINGOBJECTIVES.equals(resource) || ResourceNames.LEARNINGSTANDARDS.equals(resource)
                || ResourceNames.ASSESSMENTS.equals(resource)
                || ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS.equals(resource)
                || ResourceNames.STUDENT_COMPETENCY_OBJECTIVES.equals(resource)
                || ResourceNames.SESSIONS.equals(resource) || ResourceNames.COURSES.equals(resource)
                || ResourceNames.COURSE_OFFERINGS.equals(resource) || ResourceNames.GRADING_PERIODS.equals(resource)) {
            mutated.setPath("/" + ResourceNames.SEARCH + "/" + resource);
            Map<String, String> mutatedHeaders = new HashMap<String, String>();
            mutatedHeaders.put("Content-Type", "application/vnd.slc.search.full+json");
            mutated.setHeaders(mutatedHeaders);
        } else if (ResourceNames.HOME.equals(resource)) {
            mutated.setPath("/" + resource);
        } else if (ResourceNames.ATTENDANCES.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter("/sections/%s/studentSectionAssociations/students/attendances",
                        mutatedParameters, ParameterConstants.SECTION_ID);
            } else {
                mutated.setPath(String.format("/sections/%s/studentSectionAssociations/students/attendances",
                        StringUtils.join(sectionHelper.getTeachersSections(user), ",")));
            }
        } else if (ResourceNames.COHORTS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffCohortAssociations/cohorts", user.getEntityId()));
        } else if (ResourceNames.COURSE_TRANSCRIPTS.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter(
                        "/sections/%s/studentSectionAssociations/students/studentAcademicRecords/courseTranscripts",
                        mutatedParameters, ParameterConstants.SECTION_ID);
            } else {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                mutated.setPath(String.format(
                        "/sections/%s/studentSectionAssociations/students/studentAcademicRecords/courseTranscripts",
                        ids));
            }
        } else if (ResourceNames.DISCIPLINE_ACTIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/disciplineActions", user.getEntityId()));
        } else if (ResourceNames.DISCIPLINE_INCIDENTS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/disciplineIncidents", user.getEntityId()));
        } else if (ResourceNames.EDUCATION_ORGANIZATIONS.equals(resource)) {
            mutated.setPath(String.format("/teachers/%s/teacherSchoolAssociations/schools", user.getEntityId()));
        } else if (ResourceNames.GRADES.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter("/sections/%s/studentSectionAssociations/grades", mutatedParameters,
                        ParameterConstants.SECTION_ID);
            } else {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                mutated.setPath(String.format("/sections/%s/studentSectionAssociations/grades", ids));
            }
        } else if (ResourceNames.GRADEBOOK_ENTRIES.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter("/sections/%s/gradebookEntries", mutatedParameters,
                        ParameterConstants.SECTION_ID);
            } else {
                mutated.setPath(String.format("/sections/%s/gradebookEntries",
                        StringUtils.join(sectionHelper.getTeachersSections(user), ",")));
            }
        } else if (ResourceNames.PARENTS.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter(
                        "/sections/%s/studentSectionAssociations/students/studentParentAssociations/parents",
                        mutatedParameters, ParameterConstants.SECTION_ID);
            } else {
                mutated.setPath(String.format(
                        "/sections/%s/studentSectionAssociations/students/studentParentAssociations/parents",
                        StringUtils.join(sectionHelper.getTeachersSections(user), ",")));
            }
        } else if (ResourceNames.PROGRAMS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffProgramAssociations/programs", user.getEntityId()));
        } else if (ResourceNames.REPORT_CARDS.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter("/sections/%s/studentSectionAssociations/students/reportCards",
                        mutatedParameters, ParameterConstants.SECTION_ID);
            } else {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                mutated.setPath(String.format("/sections/%s/studentSectionAssociations/students/reportCards", ids));
            }
        } else if (ResourceNames.SECTIONS.equals(resource)) {
            mutated.setPath(String.format("/teachers/%s/teacherSectionAssociations/sections", user.getEntityId()));
        } else if (ResourceNames.SCHOOLS.equals(resource)) {
            mutated.setPath(String.format("/teachers/%s/teacherSchoolAssociations/schools", user.getEntityId())); // teachers/id/teacherschoolassociations/schools
        } else if (ResourceNames.STAFF.equals(resource)) {
            mutated.setPath(String.format("/educationOrganizations/%s/staffEducationOrgAssignmentAssociations/staff",
                    StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
        } else if (ResourceNames.STAFF_COHORT_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffCohortAssociations", user.getEntityId()));
        } else if (ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/educationOrganizations/%s/staffEducationOrgAssignmentAssociations",
                    StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
        } else if (ResourceNames.STAFF_PROGRAM_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffProgramAssociations", user.getEntityId()));
        } else if (ResourceNames.STUDENTS.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter("/sections/%s/studentSectionAssociations/students", mutatedParameters,
                        ParameterConstants.SECTION_ID);
            } else {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                mutated.setPath(String.format("/sections/%s/studentSectionAssociations/students", ids));
            }
        } else if (ResourceNames.STUDENT_ACADEMIC_RECORDS.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter(
                        "/sections/%s/studentSectionAssociations/students/studentAcademicRecords", mutatedParameters,
                        ParameterConstants.SECTION_ID);
            } else {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                mutated.setPath(String.format(
                        "/sections/%s/studentSectionAssociations/students/studentAcademicRecords", ids));
            }
        } else if (ResourceNames.STUDENT_ASSESSMENTS.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter("/sections/%s/studentSectionAssociations/students/studentAssessments",
                        mutatedParameters, ParameterConstants.SECTION_ID);
            } else {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                mutated.setPath(String.format("/sections/%s/studentSectionAssociations/students/studentAssessments",
                        ids));
            }
        } else if (ResourceNames.STUDENT_COHORT_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffCohortAssociations/cohorts/studentCohortAssociations",
                    user.getEntityId()));
        } else if (ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/disciplineIncidents/studentDisciplineIncidentAssociations",
                    user.getEntityId()));
        } else if (ResourceNames.STUDENT_COMPETENCIES.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter("/sections/%s/studentSectionAssociations/studentCompetencies",
                        mutatedParameters, ParameterConstants.SECTION_ID);
            } else {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                mutated.setPath(String.format("/sections/%s/studentSectionAssociations/studentCompetencies", ids));
            }
        } else if (ResourceNames.STUDENT_COMPETENCY_OBJECTIVES.equals(resource)) {
            mutated.setPath(String.format("/educationOrganizations/%s/studentCompetencyObjectives",
                    StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
        } else if (ResourceNames.STUDENT_GRADEBOOK_ENTRIES.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter(
                        "/sections/%s/studentSectionAssociations/students/studentGradebookEntries", mutatedParameters,
                        ParameterConstants.SECTION_ID);
            } else {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                mutated.setPath(String.format(
                        "/sections/%s/studentSectionAssociations/students/studentGradebookEntries", ids));
            }
        } else if (ResourceNames.STUDENT_PARENT_ASSOCIATIONS.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter(
                        "/sections/%s/studentSectionAssociations/students/studentParentAssociations",
                        mutatedParameters, ParameterConstants.SECTION_ID);
            } else {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                mutated.setPath(String.format(
                        "/sections/%s/studentSectionAssociations/students/studentParentAssociations", ids));
            }
        } else if (ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffProgramAssociations/programs/studentProgramAssociations",
                    user.getEntityId()));
        } else if (ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter(
                        "/sections/%s/studentSectionAssociations/students/studentSchoolAssociations",
                        mutatedParameters, ParameterConstants.SECTION_ID);
            } else {
                String ids = StringUtils.join(sectionHelper.getTeachersSections(user), ",");
                mutated.setPath(String.format(
                        "sections/%s/studentSectionAssociations/students/studentSchoolAssociations", ids));
            }
        } else if (ResourceNames.STUDENT_SECTION_ASSOCIATIONS.equals(resource)) {
            if (mutatedParameters.contains(ParameterConstants.SECTION_ID)) {
                return formQueryBasedOnParameter("/sections/%s/studentSectionAssociations", mutatedParameters,
                        ParameterConstants.SECTION_ID);
            } else {
                mutated.setPath(String.format("/sections/%s/studentSectionAssociations",
                        StringUtils.join(sectionHelper.getTeachersSections(user), ",")));
            }
        } else if (ResourceNames.TEACHERS.equals(resource)) {
            mutated.setPath(String.format("/schools/%s/teacherSchoolAssociations/teachers",
                    StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
        } else if (ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/teachers/%s/teacherSchoolAssociations", user.getEntityId()));
        } else if (ResourceNames.TEACHER_SECTION_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/teachers/%s/teacherSectionAssociations", user.getEntityId()));
        }

        return mutated;
    }

    private MutatedContainer mutateBaseUriForStaff(String resource, final String mutatedParameters, Entity user,
            String queryParameters) {

        MutatedContainer mutated = new MutatedContainer();
        mutated.setQueryParameters(mutatedParameters);

        if (ResourceNames.LEARNINGOBJECTIVES.equals(resource) || ResourceNames.LEARNINGSTANDARDS.equals(resource)
                || ResourceNames.ASSESSMENTS.equals(resource)
                || ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS.equals(resource)
                || ResourceNames.STUDENT_COMPETENCY_OBJECTIVES.equals(resource)
                || ResourceNames.SESSIONS.equals(resource) || ResourceNames.COURSES.equals(resource)
                || ResourceNames.COURSE_OFFERINGS.equals(resource) || ResourceNames.GRADING_PERIODS.equals(resource)) {
            mutated.setPath("/" + ResourceNames.SEARCH + "/" + resource);
            Map<String, String> mutatedHeaders = new HashMap<String, String>();
            mutatedHeaders.put("Content-Type", "application/vnd.slc.search.full+json");
            mutated.setHeaders(mutatedHeaders);
        } else if (ResourceNames.HOME.equals(resource)) {
            mutated.setPath("/" + resource);
        } else if (ResourceNames.ATTENDANCES.equals(resource)) {
            String ids = getQueryValueForQueryParameters(ParameterConstants.STUDENT_ID, queryParameters);
            if (ids != null) {
                mutated.setQueryParameters(removeQueryFromQueryParameters(ParameterConstants.STUDENT_ID,
                        queryParameters));
                mutated.setPath(String.format("/students/%s/attendances", ids));
            } else {
                ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
                mutated.setPath(String.format("/schools/%s/studentSchoolAssociations/students/attendances", ids));
            }

        } else if (ResourceNames.COHORTS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffCohortAssociations/cohorts", user.getEntityId()));
        } else if (ResourceNames.COURSE_TRANSCRIPTS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format(
                    "/schools/%s/studentSchoolAssociations/students/studentAcademicRecords/courseTranscripts", ids));
        } else if (ResourceNames.DISCIPLINE_ACTIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/disciplineActions", user.getEntityId()));
        } else if (ResourceNames.DISCIPLINE_INCIDENTS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/disciplineIncidents", user.getEntityId()));
        } else if (ResourceNames.EDUCATION_ORGANIZATIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffEducationOrgAssignmentAssociations/educationOrganizations",
                    user.getEntityId()));
        } else if (ResourceNames.GRADES.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/sections/studentSectionAssociations/grades", ids));
        } else if (ResourceNames.GRADEBOOK_ENTRIES.equals(resource)) {
            mutated.setPath(String.format("/schools/%s/sections/gradebookEntries",
                    StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
        } else if (ResourceNames.PARENTS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format(
                    "/schools/%s/studentSchoolAssociations/students/studentParentAssociations/parents", ids));
        } else if (ResourceNames.PROGRAMS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffProgramAssociations/programs", user.getEntityId()));
        } else if (ResourceNames.REPORT_CARDS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/studentSchoolAssociations/students/reportCards", ids));
        } else if (ResourceNames.SCHOOLS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffEducationOrgAssignmentAssociations/schools",
                    user.getEntityId()));
        } else if (ResourceNames.SECTIONS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/sections", ids));
        } else if (ResourceNames.STAFF.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/educationOrganizations/%s/staffEducationOrgAssignmentAssociations/staff",
                    ids));
        } else if (ResourceNames.STAFF_COHORT_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffCohortAssociations", user.getEntityId()));
        } else if (ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffEducationOrgAssignmentAssociations", user.getEntityId()));
        } else if (ResourceNames.STAFF_PROGRAM_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffProgramAssociations", user.getEntityId()));
        } else if (ResourceNames.STUDENTS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/studentSchoolAssociations/students", ids));
        } else if (ResourceNames.STUDENT_ACADEMIC_RECORDS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/studentSchoolAssociations/students/studentAcademicRecords", ids));
        } else if (ResourceNames.STUDENT_ASSESSMENTS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/studentSchoolAssociations/students/studentAssessments", ids));
        } else if (ResourceNames.STUDENT_COHORT_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffCohortAssociations/cohorts/studentCohortAssociations",
                    user.getEntityId()));
        } else if (ResourceNames.STUDENT_COMPETENCIES.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/sections/studentSectionAssociations/studentCompetencies", ids));
        } else if (ResourceNames.STUDENT_COMPETENCY_OBJECTIVES.equals(resource)) {
            mutated.setPath(String.format("/educationOrganizations/%s/studentCompetencyObjectives",
                    StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
        } else if (ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/disciplineIncidents/studentDisciplineIncidentAssociations",
                    user.getEntityId()));
        } else if (ResourceNames.STUDENT_GRADEBOOK_ENTRIES.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String
                    .format("/schools/%s/studentSchoolAssociations/students/studentGradebookEntries", ids));
        } else if (ResourceNames.STUDENT_PARENT_ASSOCIATIONS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/studentSchoolAssociations/students/studentParentAssociations",
                    ids));
        } else if (ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffProgramAssociations/programs/studentProgramAssociations",
                    user.getEntityId()));
        } else if (ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/studentSchoolAssociations", ids));
        } else if (ResourceNames.STUDENT_SECTION_ASSOCIATIONS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/sections/studentSectionAssociations", ids));
        } else if (ResourceNames.TEACHERS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/teacherSchoolAssociations/teachers", ids));
        } else if (ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/teacherSchoolAssociations", ids));
        } else if (ResourceNames.PROGRAMS.equals(resource)) {
            mutated.setPath(String.format("/staff/%s/staffProgramAssociations/programs", user.getEntityId()));
        } else if (ResourceNames.PARENTS.equals(resource)) {
            mutated.setPath(String.format(
                    "/schools/%s/studentSchoolAssociations/students/studentParentAssociations/parents",
                    StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",")));
        } else if (ResourceNames.TEACHER_SECTION_ASSOCIATIONS.equals(resource)) {
            String ids = StringUtils.join(edOrgHelper.getDirectEdorgs(user), ",");
            mutated.setPath(String.format("/schools/%s/teacherSchoolAssociations/teachers/teacherSectionAssociations",
                    ids));
        }

        return mutated;
    }

    /**
     * Mutates the API call (to a base entity) to a more-specific (and generally more constrained)
     * URI.
     *
     * @param resource - root resource being accessed.
     * @param version - API version.
     * @param queryParameters - URI query parameters.
     * @param user - entity representing user making API call.
     * @return Mutated String representing new API call, or null if no mutation takes place.
     */
    public MutatedContainer mutateBaseUri(String version, String resource, final String queryParameters, Entity user) {
        MutatedContainer mutated = new MutatedContainer();
        mutated.setQueryParameters(queryParameters);
        if (mutated.getQueryParameters() == null) {
            mutated.setQueryParameters("");
        }

        mutated.setPath(rootSearchMutator.mutatePath(version, resource, mutated.getQueryParameters()));
        if (mutated.getPath() == null && mutateToTeacher()) {
            return this.mutateBaseUriForTeacher(resource, mutated.getQueryParameters(), user);
        } else if (mutated.getPath() == null && mutateToStaff()) {
            return this.mutateBaseUriForStaff(resource, mutated.getQueryParameters(), user,
                    mutated.getQueryParameters());
        } else if (mutated.getPath() == null && isStudent(user) || isParent(user)) {
            return this.mutateStudentParentRequest(Arrays.<String> asList(version, resource),
                    mutated.getQueryParameters(), user);
        } else {
            return mutated;
        }
    }

    private String getQueryValueForQueryParameters(String queryName, String queryParameters) {
        String queryValue = null;
        String[] queries = queryParameters.split("&");
        String queryRegEx = "^" + Matcher.quoteReplacement(queryName) + "=.+";

        for (String query : queries) {
            if (query.matches(queryRegEx)) {
                int indexOfQueryValue = queryRegEx.length() - 3;
                queryValue = query.substring(indexOfQueryValue);
                break;
            }
        }

        return queryValue;
    }

    private String removeQueryFromQueryParameters(String queryName, String queryParameters) {
        String queryRegEx = Matcher.quoteReplacement(queryName) + "=[^&]*&?";
        return queryParameters.replaceFirst(Matcher.quoteReplacement(queryRegEx), "");
    }

    private boolean isStudent(Entity principal) {
        return principal.getType().equals(EntityNames.STUDENT);
    }

    private boolean isParent(Entity principal) {
        return principal.getType().equals(EntityNames.PARENT);
    }

    /**
     * Determines if the URI should be mutated to a teacher context for a federated user.
     *
     * @return - True if the user has teacher context, false otherwise
     */
    private boolean mutateToTeacher() {
        return (SecurityUtil.getUserContext() == SecurityUtil.UserContext.TEACHER_CONTEXT);
    }

    /**
     * Determines if the URI should be mutated to a staff context for a federated user.
     *
     * @return - True if the user has staff or dual context, false otherwise
     */
    private boolean mutateToStaff() {
        return ((SecurityUtil.getUserContext() == SecurityUtil.UserContext.STAFF_CONTEXT) ||
                (SecurityUtil.getUserContext() == SecurityUtil.UserContext.DUAL_CONTEXT));
    }

    private String getStudentIds(SLIPrincipal principal) {
        Collection<String> studentIds;
        Entity entity = principal.getEntity();
        if (isStudent(entity)) {
            studentIds = Arrays.asList(entity.getEntityId());
        } else if (isParent(entity)) {
            // use evil thread local to get the student list
            studentIds = principal.getOwnedStudentIds();
        } else {
            studentIds = Collections.emptyList();
        }
        return StringUtils.join(studentIds, ",");
    }

    private String getStudentAcademicRecordsIds(Entity principal) {
        return StringUtils.join(getStudentRelatedRecords(principal, EntityNames.STUDENT_ACADEMIC_RECORD), ",");
    }

    private String getStudentSectionAssocIds(SLIPrincipal principal) {
        Set<String> assocIds = new HashSet<String>();
        for (Entity student : principal.getOwnedStudentEntities()) {
            assocIds.addAll(getStudentRelatedRecords(student, EntityNames.STUDENT_SECTION_ASSOCIATION));
        }
        return StringUtils.join(assocIds, ",");
    }

    private String getSectionIds(SLIPrincipal principal) {
        Collection<String> sectionIds = new HashSet<String>();
        for (Entity student : principal.getOwnedStudentEntities()) {
            sectionIds.addAll(sectionHelper.getStudentsSections(student));
        }
        return StringUtils.join(sectionIds, ",");
    }

    private String getProgramIds(SLIPrincipal principal) {
        Set<String> programIds = new HashSet<String>();
        for (Entity student : principal.getOwnedStudentEntities()) {
            programIds.addAll(getProgramIdsForStudent(student));
        }

        return StringUtils.join(programIds, ",");
    }

    private Set<String> getProgramIdsForStudent(Entity student) {
        Set<String> programsIds = null;
        if (isStudent(student)) {
            programsIds = getSubdocIds(student, EntityNames.STUDENT_PROGRAM_ASSOCIATION, ParameterConstants.PROGRAM_ID);
        }

        if (programsIds == null || programsIds.isEmpty()) {
            throw new UriMutationException("No association to any programs");
        }

        return programsIds;
    }

    private String getCohortIds(SLIPrincipal principal) {
        Set<String> cohortsIds = new HashSet<String>();
        for (Entity student : principal.getOwnedStudentEntities()) {
            cohortsIds.addAll(getCohortIdsForStudent(student));
        }

        return StringUtils.join(cohortsIds, ",");
    }

    private Set<String> getCohortIdsForStudent(Entity student) {
        Set<String> cohortsIds = null;
        if (isStudent(student)) {
            cohortsIds = getSubdocIds(student, EntityNames.STUDENT_COHORT_ASSOCIATION, ParameterConstants.COHORT_ID);
        }

        if (cohortsIds == null || cohortsIds.isEmpty()) {
            throw new UriMutationException("No association to any cohorts");
        }
        return cohortsIds;
    }

    private Set<String> getSubdocIds(Entity superdoc, String subdocType, String subdocField) {
        Set<String> subdocFields = new HashSet<String>();
        Map<String, List<Entity>> myEmbeddedData = superdoc.getEmbeddedData();
        List<Entity> myAssociations = myEmbeddedData == null ? Collections.<Entity> emptyList() : myEmbeddedData
                .get(subdocType);
        if (myAssociations != null && !myAssociations.isEmpty()) {
            for (Entity association : myAssociations) {
                if (!dateHelper.isFieldExpired(association.getBody(), ParameterConstants.END_DATE)) {
                    subdocFields.add((String) association.getBody().get(subdocField));
                }
            }
        }

        return subdocFields;
    }

    private List<String> getStudentRelatedRecords(Entity principal, String entityType) {
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.STUDENT_ID,
                NeutralCriteria.CRITERIA_IN, SecurityUtil.getSLIPrincipal().getOwnedStudentIds()));
        List<String> recordIds = new ArrayList<String>();

        Iterable<String> allIds = repo.findAllIds(entityType, query);

        for (String recordId : allIds) {
            recordIds.add(recordId);
        }

        return recordIds;
    }

    private MutatedContainer formQueryBasedOnParameter(String path, String parameters, String parameter) {
        MutatedContainer mutated = new MutatedContainer();

        String[] queryParameters = parameters.split("&");
        for (int i = 0; i < queryParameters.length; i++) {
            String queryParameter = queryParameters[i];
            String[] values = queryParameter.split("=");
            if (values.length == 2) {
                if (values[0].equals(parameter) && values[1] != null && !values[1].isEmpty()) {
                    mutated.setPath(String.format(path, values[1]));
                    mutated.setQueryParameters(removeQueryParameter(parameters, parameter));
                    break;
                }
            }
        }

        return mutated;
    }

    private String removeQueryParameter(String parameters, String queryParameterToRemove) {
        if (parameters == null || parameters.isEmpty()) {
            return parameters;
        }

        StringBuilder builder = new StringBuilder();
        String[] queryParameters = parameters.split("&");
        for (String queryParameter : queryParameters) {
            if (!queryParameter.startsWith(queryParameterToRemove)) {
                builder.append(queryParameter).append("&");
            }
        }

        if (builder.length() > 0) {
            return builder.substring(0, builder.length() - 2);
        }
        return "";
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
