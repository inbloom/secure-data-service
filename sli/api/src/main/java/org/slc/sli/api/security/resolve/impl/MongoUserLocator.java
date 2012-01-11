package org.slc.sli.api.security.resolve.impl;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;

/**
 * Attempts to locate a user in SLI mongo data-store
 * 
 * @author dkornishev
 * 
 */
@Component
public class MongoUserLocator implements UserLocator {
    
    public static final Logger        LOG               = LoggerFactory.getLogger(MongoUserLocator.class);
    
    private static final List<String> ENTITY_NAMES      = Arrays.asList("teacher", "staff");
    
    @Autowired
    private EntityRepository          repo;
    
    @Override
    public SLIPrincipal locate(String realm, String externalUserId) {
        LOG.info("Locating user {}@{}", externalUserId, realm);
        SLIPrincipal user = new SLIPrincipal(externalUserId + "@" + realm);
        
        Query query = new Query(Criteria.where("stateId").is(realm).and("body.staffUniqueStateId").is(externalUserId));
        for (String entityName : ENTITY_NAMES) {
            Iterable<Entity> staff = repo.findByFields(entityName, query, 0, 1);
            
            if (staff != null) {
                for (Entity e : staff) {
                    LOG.info("Matched user: {}@{} -> {}", new Object[] { externalUserId, realm, e.getEntityId() });
                    user.setEntity(e);
                }
            }
        }
        
        return user;
    }
    
    public void setRepo(EntityRepository repo) {
        this.repo = repo;
    }
}
