package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.slc.sli.api.client.constants.v1.ParameterConstants.STUDENT_RECORD_ACCESS;
import static org.slc.sli.api.client.constants.v1.ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID;

/**
 * Resolves Teachers context to Students. Finds accessible students through section, program, and cohort associations.
 */
@Component
public class TeacherStudentResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private StudentSectionAssociationEndDateFilter dateFilter;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.STUDENT.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {

        Set<String> ids = new TreeSet<String>();

        ids.addAll(findAccessibleThroughSection(principal));
        ids.addAll(findAccessibleThroughCohort(principal));
        ids.addAll(findAccessibleThroughProgram(principal));

        return new ArrayList<String>(ids);
    }

    private List<String> findAccessibleThroughSection(Entity principal) {

        // teacher -> teacherSectionAssociation
        Iterable<Entity> teacherSectionAssociations = helper.getReferenceEntities(EntityNames.TEACHER_SECTION_ASSOCIATION, ParameterConstants.TEACHER_ID, Arrays.asList(principal.getEntityId()));

        // filter on end_date to get list of programIds
        List<String> sectionIds = new ArrayList<String>();
        final String currentDate = dateFilter.getCurrentDate();
        for (Entity assoc : teacherSectionAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty() || (dateFilter.isDateBeforeEndDate(currentDate, endDate))) {
                sectionIds.add((String) assoc.getBody().get(ParameterConstants.SECTION_ID));
            }
        }

        // section -> studentSectionAssociation
        Iterable<Entity> studentSectionAssociations = helper.getReferenceEntities(EntityNames.STUDENT_SECTION_ASSOCIATION, ParameterConstants.SECTION_ID, sectionIds);

        // filter on end_date to get list of students
        List<String> studentIds = new ArrayList<String>();
        for (Entity assoc : studentSectionAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty() || dateFilter.isDateBeforeEndDate(currentDate, endDate)) {
                studentIds.add((String) assoc.getBody().get(ParameterConstants.STUDENT_ID));
            }
        }

        List<String> returnIds = new ArrayList<String>();
        returnIds.addAll(studentIds);
        returnIds.addAll(sectionIds);
        for (String id : returnIds) {
            error("program {}", id);
        }
        return returnIds;
    }

    private List<String> findAccessibleThroughProgram(Entity principal) {

        // teacher -> staffProgramAssociation
        Iterable<Entity> staffProgramAssociations = helper.getReferenceEntities(EntityNames.STAFF_PROGRAM_ASSOCIATION, ParameterConstants.STAFF_ID, Arrays.asList(principal.getEntityId()));

        // filter on end_date to get list of programIds
        List<String> programIds = new ArrayList<String>();
        final String currentDate = dateFilter.getCurrentDate();
        for (Entity assoc : staffProgramAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (dateFilter.isDateBeforeEndDate(currentDate, endDate) && (Boolean) assoc.getBody().get(STUDENT_RECORD_ACCESS)) {
                programIds.add((String) assoc.getBody().get(ParameterConstants.PROGRAM_ID));
            }
        }

        // program -> studentProgramAssociation
        Iterable<Entity> studentProgramAssociations = helper.getReferenceEntities(EntityNames.STUDENT_PROGRAM_ASSOCIATION, ParameterConstants.PROGRAM_ID, programIds);

        // filter on end_date to get list of students
        List<String> studentIds = new ArrayList<String>();
        for (Entity assoc : studentProgramAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (dateFilter.isDateBeforeEndDate(dateFilter.getCurrentDate(), endDate)) {
                studentIds.add((String) assoc.getBody().get(ParameterConstants.STUDENT_ID));
            }
        }

        List<String> returnIds = new ArrayList<String>();
        returnIds.addAll(studentIds);
        returnIds.addAll(programIds);
        for (String id : returnIds) {
            error("program {}", id);
        }
        return returnIds;
    }

    private List<String> findAccessibleThroughCohort(Entity principal) {

        // teacher -> staffCohortAssociation
        Iterable<Entity> staffCohortAssociations = helper.getReferenceEntities(EntityNames.STAFF_COHORT_ASSOCIATION, ParameterConstants.STAFF_ID, Arrays.asList(principal.getEntityId()));

        // filter on end_date to get list of cohortIds
        List<String> cohortIds = new ArrayList<String>();
        for (Entity assoc : staffCohortAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (dateFilter.isDateBeforeEndDate(dateFilter.getCurrentDate(), endDate) && (Boolean) assoc.getBody().get(STUDENT_RECORD_ACCESS)) {
                cohortIds.add((String) assoc.getBody().get(ParameterConstants.COHORT_ID));
            }
        }

        // cohort -> studentCohortAssociation
        Iterable<Entity> studentCohortAssociations = helper.getReferenceEntities(EntityNames.STUDENT_COHORT_ASSOCIATION, ParameterConstants.COHORT_ID, cohortIds);

        // filter on end_date to get list of students
        List<String> studentIds = new ArrayList<String>();
        for (Entity assoc : studentCohortAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (dateFilter.isDateBeforeEndDate(dateFilter.getCurrentDate(), endDate)) {
                studentIds.add((String) assoc.getBody().get(ParameterConstants.STUDENT_ID));
            }
        }

        List<String> returnIds = new ArrayList<String>();
        returnIds.addAll(studentIds);
        returnIds.addAll(cohortIds);
        for (String id : returnIds) {
            error("cohort {}", id);
        }
        return returnIds;
    }
}
