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

package org.slc.sli.api.security.context.validator;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates the context of a staff member to see the requested set of students. Returns true if the
 * staff member can see ALL of the students, and false otherwise.
 */
@Component
public class StaffToStudentValidator extends AbstractContextValidator {

    private static final Logger LOG = LoggerFactory.getLogger(StaffToStudentValidator.class);

    @Autowired
    private GenericToProgramValidator programValidator;

    @Autowired
    private GenericToCohortValidator cohortValidator;

    @Autowired
    private EdOrgHelper edorgHelper;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.STUDENT.equals(entityType)
                && isStaff();
    }

    @Override
    public Set<String> getValid(String entityType, Set<String> studentIds) {
        LOG.trace(">>>StaffToStudentValidator.getValid(?)", entityType);
        LOG.trace("  studentIds" + ToStringBuilder.reflectionToString(studentIds, ToStringStyle.DEFAULT_STYLE));

        // first check if the entire set is valid. if not, check id by id.
        //return (validate(entityType, studentIds)) ? studentIds : super.getValid(entityType, studentIds);
        return validate(entityType, studentIds);
    }

    @Override
    public Set<String> validate(String entityType, Set<String> studentIds) throws IllegalStateException {
        LOG.debug(">>>StaffToStudentValidator.validate(?)", entityType);
        LOG.debug("  studentIds" + ToStringBuilder.reflectionToString(studentIds, ToStringStyle.DEFAULT_STYLE));

        if (!areParametersValid(EntityNames.STUDENT, entityType, studentIds)) {
            LOG.debug("  !areParametersValid");
            return new HashSet<String>();
        }

        return validateStaffToStudentContextThroughSharedEducationOrganization(studentIds);
    }

    private Set<String> validateStaffToStudentContextThroughSharedEducationOrganization(Collection<String> ids) {
        LOG.trace(">>>StaffToStudentValidator.validateStaffToStudentContextThroughSharedEducationOrganization()");
        LOG.debug("  ids" + ToStringBuilder.reflectionToString(ids, ToStringStyle.DEFAULT_STYLE));

        // lookup current staff edOrg associations and get the Ed Org Ids
        Set<String> staffsEdOrgIds = getStaffCurrentAssociatedEdOrgs();

        // lookup students
        Iterable<Entity> students = getStudentEntitiesFromIds(ids);

        Set<String> validIds = new HashSet<String>();

        if (students != null && students.iterator().hasNext()) {

            LOG.debug("  Iterating on each student entity.");

            for (Entity entity : students) {
                Set<String> studentsEdOrgs = getStudentsEdOrgs(entity);
                Set<String> validPrograms = programValidator.validate(EntityNames.PROGRAM, getValidPrograms(entity));
                Set<String> validCohorts = cohortValidator.validate(EntityNames.COHORT, getValidCohorts(entity));

                boolean hasStaffStudentIntersection = isIntersection(staffsEdOrgIds, studentsEdOrgs);
                LOG.debug("   hasStaffStudentIntersection = " + hasStaffStudentIntersection);

                if ((hasStaffStudentIntersection || !validPrograms.isEmpty() || !validCohorts.isEmpty())) {
                    validIds.add(entity.getEntityId());
                }
            }
        }

        return validIds;
    }

    private Set<String> getValidPrograms(Entity entity) {
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STUDENT_ID,
                NeutralCriteria.OPERATOR_EQUAL, entity.getEntityId()));
        Set<String> programs = new HashSet<String>();
        Iterable<Entity> spas = getRepo().findAll(EntityNames.STUDENT_PROGRAM_ASSOCIATION, basicQuery);
        for (Entity spa : spas) {
            if (isFieldExpired(spa.getBody(), ParameterConstants.END_DATE, true)) {
                continue;
            }
            programs.add((String) spa.getBody().get(ParameterConstants.PROGRAM_ID));
        }
        return programs;
    }

    private Set<String> getValidCohorts(Entity entity) {
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STUDENT_ID,
                NeutralCriteria.OPERATOR_EQUAL, entity.getEntityId()));
        Set<String> cohorts = new HashSet<String>();
        Iterable<Entity> scas = getRepo().findAll(EntityNames.STUDENT_COHORT_ASSOCIATION, basicQuery);
        for (Entity sca : scas) {
            if (isFieldExpired(sca.getBody(), ParameterConstants.END_DATE, true)) {
                continue;
            }
            cohorts.add((String) sca.getBody().get(ParameterConstants.COHORT_ID));
        }
        return cohorts;
    }

    private boolean isIntersection(Set<String> setA, Set<String> setB) {
        boolean isIntersection = false;
        for (Object a : setA) {
            if (setB.contains(a)) {
                isIntersection = true;
                break;
            }
        }
        return isIntersection;
    }

    private Set<String> getStudentsEdOrgs(Entity studentEntity) {
        return new HashSet< String> ( edorgHelper.getDirectSchoolsLineage( studentEntity, true));

    }

    private Iterable<Entity> getStudentEntitiesFromIds(Collection<String> studentIds) {
        LOG.debug("StaffToStudentValidator.getStudentEntitiesFromIds()");
        LOG.debug("  studentIds: " + ToStringBuilder.reflectionToString(studentIds, ToStringStyle.DEFAULT_STYLE));

        NeutralQuery studentQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, new ArrayList<String>(studentIds)));
        studentQuery.setEmbeddedFieldString("schools");
        Iterable<Entity> students = getRepo().findAll(EntityNames.STUDENT, studentQuery);

        if (students == null) {
            LOG.debug("   students NOT found in Repo.");
        } else {
            LOG.debug("   students WERE found in Repo.");
        }

        return students;
    }

    /**
     * @param programValidator
     *            the programValidator to set
     */
    public void setProgramValidator(GenericToProgramValidator programValidator) {
        this.programValidator = programValidator;
    }

    /**
     * @param cohortValidator
     *            the cohortValidator to set
     */
    public void setCohortValidator(GenericToCohortValidator cohortValidator) {
        this.cohortValidator = cohortValidator;
    }
    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.STAFF_CONTEXT;
    }

}
