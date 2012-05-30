/**
 *
 */
package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.security.schema.SchemaDataProvider;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Resolves "Users" to anything.
 *
 * In particular we only want Users to resolve to administrative resources and be
 * denied to anything else.
 *
 * @author rlatta
 *
 */
@Component
public class UserToAnythingResolver implements EntityContextResolver {
    private String toEntity;
    private static final String ADMIN_SPHERE = "Admin";
    private static final String PUBLIC_SPHERE = "Public";
    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private SchemaDataProvider provider;

    @Autowired
    private Repository<Entity> repository;

    /* (non-Javadoc)
     * @see org.slc.sli.api.security.context.resolver.EntityContextResolver#canResolve(java.lang.String, java.lang.String)
     */
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        this.toEntity = toEntityType;
        debug("Resolving {} -> {}", new String[] { fromEntityType, toEntityType });
        return (fromEntityType.equals("user") || fromEntityType.equals("admin-staff"));
    }

    /* (non-Javadoc)
     * @see org.slc.sli.api.security.context.resolver.EntityContextResolver#findAccessible(org.slc.sli.domain.Entity)
     */
    @Override
    public List<String> findAccessible(Entity principal) {
        EntityDefinition def = store.lookupByEntityType(toEntity);

        if (PUBLIC_SPHERE.equals(provider.getDataSphere(def.getType()))) {
            info("Granting public sphere to resource: {}", def.getResourceName());
        } else if (ADMIN_SPHERE.equals(provider.getDataSphere(def.getType()))) {
            info("Granting admin sphere to resource: {}", def.getResourceName());
        } else {
            info("Denying access to all entities of type {}", toEntity);
            return new ArrayList<String>();
        }
        // We give access to all admin or to public
        List<String> ids = new ArrayList<String>();

        Iterable<String> it = this.repository.findAllIds(def.getStoredCollectionName(), new NeutralQuery());
        for (String id : it) {
            ids.add(id);
        }
        return ids;
    }

}
