package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * Returns all Education Organization Ids a principal entity has access to
 *
 * @author srupasinghe
 *
 */
@Component
public class EdOrgContextResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private EdOrgToChildEdOrgNodeFilter edOrgToChildEdOrgNodeFilter;

    @Autowired
    private StaffEdOrgEdOrgIDNodeFilter staffEdOrgEdOrgIDNodeFilter;

    @Autowired
    private ResolveCreatorsEntitiesHelper creatorResolverHelper;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return ((fromEntityType != null) && fromEntityType.equals(EntityNames.STAFF));
    }

    @Override
    public List<String> findAccessible(Entity principal) {

        //get the ed org ids
        List<String> ids = helper.findEntitiesContainingReference(EntityNames.STAFF_ED_ORG_ASSOCIATION, "staffReference",
                "educationOrganizationReference", Arrays.asList(principal.getEntityId()));

        //apply the filters
        ids.addAll(staffEdOrgEdOrgIDNodeFilter.filterIds(ids));
        ids.addAll(edOrgToChildEdOrgNodeFilter.filterIds(ids));
        //get the created edorgs
        ids.addAll(creatorResolverHelper.getAllowedForCreator(EntityNames.EDUCATION_ORGANIZATION));

        return ids;
    }
}
