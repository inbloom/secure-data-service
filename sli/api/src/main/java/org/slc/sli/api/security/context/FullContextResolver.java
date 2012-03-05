package org.slc.sli.api.security.context;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Gives context to all entities of target type.
 */
@Component
public class FullContextResolver implements EntityContextResolver {

    private String source;
    private String target;
    private EntityRepository repository;
    private EntityDefinition definition;

    public FullContextResolver(EntityRepository repository, EntityDefinition entityDefinition) {
        this.repository = repository;
        this.definition = entityDefinition;
    }

    public FullContextResolver() {

    }

    @Override
    public String getSourceType() {
        return source;
    }

    @Override
    public String getTargetType() {
        return target;
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        Iterable<Entity> entities = this.repository.findAll(definition.getStoredCollectionName(), 0, 9999);
        ArrayList<String> ids = new ArrayList<String>();
        for (Entity e : entities) {
            ids.add(e.getEntityId());
        }
        return ids;
    }

    public void setSource(String sourceType) {
        this.source = sourceType;
    }

    public void setTarget(String targetType) {
        this.target = targetType;
    }

    public void setRepository(EntityRepository repo) {
        this.repository = repo;
    }

    public void setDefinition(EntityDefinition definition) {
        this.definition = definition;
    }

}
