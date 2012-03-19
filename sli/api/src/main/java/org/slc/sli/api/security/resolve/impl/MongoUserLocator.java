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
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Attempts to locate a user in SLI mongo data-store
 *
 * @author dkornishev
 */
@Component
public class MongoUserLocator implements UserLocator {

    public static final Logger LOG = LoggerFactory.getLogger(MongoUserLocator.class);

    private static final List<String> ENTITY_NAMES = Arrays.asList("teacher", "staff");

    @Autowired
    private Repository<Entity> repo;

    @Override
    public SLIPrincipal locate(String regionId, String externalUserId) {
        LOG.info("Locating user {}@{}", externalUserId, regionId);
        SLIPrincipal user = new SLIPrincipal(externalUserId + "@" + regionId);
        user.setExternalId(externalUserId);

        Query query = new Query(Criteria.where("metaData.idNamespace").is(regionId).and("body.staffUniqueStateId").is(externalUserId));
        for (String entityName : ENTITY_NAMES) {
            Iterable<Entity> staff = repo.findByQuery(entityName, query, 0, 1);

            if (staff != null && staff.iterator().hasNext()) {
                Entity entity = staff.iterator().next();
                LOG.info("Matched user: {}@{} -> {}", new Object[] { externalUserId, regionId, entity.getEntityId() });
                user.setEntity(entity);
                break;
            }
        }
        
        if (user.getEntity() == null) {
            LOG.warn("Failed to locate user {} in the datastore", user.getId());
        }

        return user;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }
}
