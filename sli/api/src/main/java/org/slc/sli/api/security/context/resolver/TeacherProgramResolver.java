package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Resolves teacher's access and security context to program records.
 * Uses staffProgramAssociation and filters on the association end date.
 */
@Component
public class TeacherProgramResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private StudentSectionAssociationEndDateFilter dateFilter;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.PROGRAM.equals(toEntityType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findAccessible(Entity principal) {

        // teacher -> staffProgramAssociation
        Iterable<Entity> staffProgramAssociations = helper.getReferenceEntities(EntityNames.STAFF_PROGRAM_ASSOCIATION, ParameterConstants.STAFF_ID, Arrays.asList(principal.getEntityId()));

        // filter on end_date to get list of programIds
        List<String> programIds = new ArrayList<String>();
        final String currentDate = dateFilter.getCurrentDate();
        for (Entity assoc : staffProgramAssociations) {
            String endDate = (String) assoc.getBody().get(ParameterConstants.END_DATE);
            if (endDate == null || endDate.isEmpty() || dateFilter.isFirstDateBeforeSecondDate(currentDate, endDate)) {
                programIds.addAll((List<String>) assoc.getBody().get(ParameterConstants.PROGRAM_ID));
            }
        }

        return programIds;
    }

}
