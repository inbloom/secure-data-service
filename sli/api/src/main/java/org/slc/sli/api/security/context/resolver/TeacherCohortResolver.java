package org.slc.sli.api.security.context.resolver;

import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
/**
 * Resolves which cohorts a given teacher is allowed to see
 *
 * @author vmcglaughlin
 *
 */
@Component
public class TeacherCohortResolver extends StaffCohortResolver implements EntityContextResolver {

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.COHORT.equals(toEntityType);
    }

}
