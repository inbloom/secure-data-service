package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
/**
 * Resolves which discipline incident a given staff is allowed to see
 *
 * @author syau
 *
 */
@Component
public class StaffDisciplineIncidentResolver implements EntityContextResolver {

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.STAFF.equals(fromEntityType) && EntityNames.DISCIPLINE_INCIDENT.equals(toEntityType);
    }

    // Temporary: Staff have no access to student records
    // TODO: Staff should have access to discipline records for which their discipline actions are associated
    @Override
    public List<String> findAccessible(Entity principal) {

        // special privilege for demo user
        if (principal.getBody().get("staffUniqueStateId").equals("demo")) {
            info("Resolver override for demo user.");
            return AllowAllEntityContextResolver.SUPER_LIST;
        }

        return new ArrayList<String>();
    }
}
