package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;

/**
 * Resolves which discipline actions a given staff is allowed to see
 *
 * @author syau
 *
 */
@Component
public class StaffDisciplineActionResolver implements EntityContextResolver {

    @Autowired
    private ResolveCreatorsEntitiesHelper creatorResolverHelper;

    @Autowired
    private AssociativeContextHelper helper;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.STAFF.equals(fromEntityType) && EntityNames.DISCIPLINE_ACTION.equals(toEntityType);
    }

    // Staff should have access to discipline actions to which they are referred.
    @Override
    public List<String> findAccessible(Entity principal) {

        List<String> referenceIds = new ArrayList<String>();
        referenceIds.add(principal.getEntityId());

        List<String> references = helper.findEntitiesContainingReference(EntityNames.DISCIPLINE_ACTION, "staffId", referenceIds);

        references.addAll(creatorResolverHelper.getAllowedForCreator(EntityNames.DISCIPLINE_ACTION));
        return references;
    }
}
