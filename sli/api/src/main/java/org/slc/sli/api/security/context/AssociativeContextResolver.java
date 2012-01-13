package org.slc.sli.api.security.context;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssociativeContextResolver implements EntityContextResolver {

    private EntityRepository repository;

    private EntityDefinitionStore entityDefinitionStore;

    private String sourceType = null;
    private String targetType = null;
    private List<AssociationDefinition> associativeContextPath = null;

    public AssociativeContextResolver() {

    }

    @Override
    public boolean hasPermission(Entity principal, Entity resource) {
        return true;  //TODO stub. traverse associative context path
    }

    @Override
    public String getSourceType() {
        return sourceType;
    }

    @Override
    public String getTargetType() {
        return targetType;
    }

    public void setSourceType(String type) {
        this.sourceType = type;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public void setAssociativeContextPath(List<AssociationDefinition> associativeContextPath) {
        this.associativeContextPath = associativeContextPath;
    }

    public void setEntityDefinitionStore(EntityDefinitionStore entityDefinitionStore) {
        this.entityDefinitionStore = entityDefinitionStore;
    }

    public void setRepository(EntityRepository repository) {
        this.repository = repository;
    }
}
