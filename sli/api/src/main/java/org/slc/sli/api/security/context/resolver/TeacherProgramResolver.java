package org.slc.sli.api.security.context.resolver;

import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;

/**
 * Resolves which programs a given teacher is allowed to see
 *
 * @author vmcglaughlin
 *
 */
@Component
public class TeacherProgramResolver extends StaffProgramResolver implements EntityContextResolver {

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.PROGRAM.equals(toEntityType);
    }

}
