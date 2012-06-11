package org.slc.sli.api.security.context.resolver;

import static org.slc.sli.api.client.constants.v1.ParameterConstants.STUDENT_RECORD_ACCESS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;

/**
 * Resolves Teachers context to Students. Finds accessible students through section, program, and cohort associations.
 */
@Component
public class TeacherStudentResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private StudentSectionAssociationEndDateFilter dateFilter;

    @Value("${sli.security.gracePeriod}")
    private String sectionGracePeriod;

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

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final String sectionGraceDate = helper.getFilterDate(sectionGracePeriod, calendar);

        for (Entity assoc : teacherSectionAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty() || (dateFilter.isFirstDateBeforeSecondDate(sectionGraceDate, endDate))) {
                sectionIds.add((String) assoc.getBody().get(ParameterConstants.SECTION_ID));
            }
        }

        // section -> studentSectionAssociation
        Iterable<Entity> studentSectionAssociations = helper.getReferenceEntities(EntityNames.STUDENT_SECTION_ASSOCIATION, ParameterConstants.SECTION_ID, sectionIds);

        // filter on end_date to get list of students
        List<String> studentIds = new ArrayList<String>();
        for (Entity assoc : studentSectionAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty() || dateFilter.isFirstDateBeforeSecondDate(sectionGraceDate, endDate)) {
                studentIds.add((String) assoc.getBody().get(ParameterConstants.STUDENT_ID));
            }
        }

        List<String> returnIds = new ArrayList<String>();
        returnIds.addAll(studentIds);
        returnIds.addAll(sectionIds);
        for (String id : returnIds) {
            debug("program {}", id);
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
            if ((Boolean) assoc.getBody().get(STUDENT_RECORD_ACCESS)) {
                String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
                if (endDate == null || endDate.isEmpty() || dateFilter.isFirstDateBeforeSecondDate(currentDate, endDate)) {
                    programIds.addAll((List<String>) assoc.getBody().get(ParameterConstants.PROGRAM_ID));
                }
            }
        }

        // program -> studentProgramAssociation
        Iterable<Entity> studentProgramAssociations = helper.getReferenceEntities(EntityNames.STUDENT_PROGRAM_ASSOCIATION, ParameterConstants.PROGRAM_ID, programIds);

        // filter on end_date to get list of students
        List<String> studentIds = new ArrayList<String>();
        for (Entity assoc : studentProgramAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty() || dateFilter.isFirstDateBeforeSecondDate(currentDate, endDate)) {
                studentIds.add((String) assoc.getBody().get(ParameterConstants.STUDENT_ID));
            }
        }

        List<String> returnIds = new ArrayList<String>();
        returnIds.addAll(studentIds);
        returnIds.addAll(programIds);
        for (String id : returnIds) {
            debug("program {}", id);
        }
        return returnIds;
    }

    private List<String> findAccessibleThroughCohort(Entity principal) {

        // teacher -> staffCohortAssociation
        Iterable<Entity> staffCohortAssociations = helper.getReferenceEntities(EntityNames.STAFF_COHORT_ASSOCIATION, ParameterConstants.STAFF_ID, Arrays.asList(principal.getEntityId()));

        // filter on end_date to get list of cohortIds
        final String currentDate = dateFilter.getCurrentDate();
        List<String> cohortIds = new ArrayList<String>();
        for (Entity assoc : staffCohortAssociations) {
            if ((Boolean) assoc.getBody().get(STUDENT_RECORD_ACCESS)) {
                String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
                if (endDate == null || endDate.isEmpty() || dateFilter.isFirstDateBeforeSecondDate(currentDate, endDate)) {
                    cohortIds.addAll((List<String>) assoc.getBody().get(ParameterConstants.COHORT_ID));
                }
            }
        }

        // cohort -> studentCohortAssociation
        Iterable<Entity> studentCohortAssociations = helper.getReferenceEntities(EntityNames.STUDENT_COHORT_ASSOCIATION, ParameterConstants.COHORT_ID, cohortIds);

        // filter on end_date to get list of students
        List<String> studentIds = new ArrayList<String>();
        for (Entity assoc : studentCohortAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty() || dateFilter.isFirstDateBeforeSecondDate(currentDate, endDate)) {
                studentIds.add((String) assoc.getBody().get(ParameterConstants.STUDENT_ID));
            }
        }

        List<String> returnIds = new ArrayList<String>();
        returnIds.addAll(studentIds);
        returnIds.addAll(cohortIds);
        for (String id : returnIds) {
            debug("cohort {}", id);
        }
        return returnIds;
    }
}
