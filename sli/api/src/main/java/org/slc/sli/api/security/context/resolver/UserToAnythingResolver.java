/**
 * 
 */
package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.security.schema.SchemaDataProvider;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        return fromEntityType.equals("user");
    }
    
    /* (non-Javadoc)
     * @see org.slc.sli.api.security.context.resolver.EntityContextResolver#findAccessible(org.slc.sli.domain.Entity)
     */
    @Override
    public List<String> findAccessible(Entity principal) {
        EntityDefinition def = store.lookupByEntityType(toEntity);
        // Any non-admin are denied.
        if (!ADMIN_SPHERE.equals(provider.getDataSphere(def.getType()))) {
            info("Denying access to all entities of type {}", toEntity);
            return new ArrayList<String>();
        }
        // We give access to all admin.
        List<String> ids = new ArrayList<String>();
        info("Granting admin sphere to resource: {}", def.getResourceName());
        Iterable<String> it = this.repository.findAllIds(def.getResourceName(), null);
        for (String id : it) {
            ids.add(id);
        }
        return ids;
    }
    
}
