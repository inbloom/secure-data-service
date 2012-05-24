package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Schools should be made public so that anyone authenticated
 * can see metadata about a particular school
 * @author jstokes
 */
@Component
public class AnythingToSchoolResolver implements EntityContextResolver {
    protected String toEntity;

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private Repository<Entity> repository;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        this.toEntity = toEntityType;
        return toEntityType.equals(EntityNames.SCHOOL);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        EntityDefinition def = store.lookupByEntityType(toEntity);

        List<String> ids = new ArrayList<String>();
        Iterable<String> it = this.repository.findAllIds(def.getStoredCollectionName(), null);
        for (String id: it) {
            ids.add(id);
        }
        return ids;
    }
}
