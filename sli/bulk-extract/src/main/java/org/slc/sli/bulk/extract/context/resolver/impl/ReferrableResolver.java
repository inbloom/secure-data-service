package org.slc.sli.bulk.extract.context.resolver.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class ReferrableResolver implements ContextResolver {
    private static final Logger LOG = LoggerFactory.getLogger(EducationOrganizationContextResolver.class);
    
    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;
    
    private final Map<String, Set<String>> cache = new HashMap<String, Set<String>>();

    public ReferrableResolver() {
        super();
    }
    
    /**
     * Find the governing LEAs based on the id
     * 
     * @param id
     *            the id of the entity to look up
     * @return the list of leas
     */
    public Set<String> findGoverningLEA(String id) {
        if (getCache().containsKey(id)) {
            LOG.debug("got LEAs from cache for {}", id);
            return getCache().get(id);
        }
        Entity entity = getRepo().findById(getCollection(), id);
        return findGoverningLEA(entity);
    }
    
    protected Repository<Entity> getRepo() {
        return repo;
    }
    
    protected Map<String, Set<String>> getCache() {
        return cache;
    }
    
    protected abstract String getCollection();
    
}