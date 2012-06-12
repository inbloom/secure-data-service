package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Resolves which programs a given staff is allowed to see
 *
 * @author vmcglaughlin
 *
 */
//@Component
public class StaffProgramResolver implements EntityContextResolver {

    @Autowired
    private ResolveCreatorsEntitiesHelper creatorResolverHelper;

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private Repository<Entity> repository;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return (EntityNames.STAFF.equals(fromEntityType) || EntityNames.TEACHER.equals(fromEntityType)) && EntityNames.PROGRAM.equals(toEntityType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findAccessible(Entity principal) {

        NeutralQuery neutralQuery = new NeutralQuery();

        List<String> referenceIds = new ArrayList<String>();
        referenceIds.add(principal.getEntityId());

        List<String> references = helper.findEntitiesContainingReference(EntityNames.STAFF_PROGRAM_ASSOCIATION, "staffId", referenceIds);
        List<String> createdContext = creatorResolverHelper.getAllowedForCreator(EntityNames.PROGRAM);

        if (references.isEmpty() && createdContext.isEmpty()) {
            return new ArrayList<String>();
        }

        neutralQuery.addCriteria(new NeutralCriteria("_id", "in", references));

        Iterable<Entity> staffProgramAssociations = repository.findAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, neutralQuery);
        Set<String> programIds = new HashSet<String>();

        for (Entity staffProgramAssociation : staffProgramAssociations) {
            for (String programId : (List<String>) staffProgramAssociation.getBody().get("programId")) {
                if (programId != null) {
                    programIds.add(programId);
                }
            }
        }

        programIds.addAll(createdContext);
        return new ArrayList<String>(programIds);
    }
}
