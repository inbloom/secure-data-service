package org.slc.sli.api.security.resolve.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        Map<String, String> paths = new HashMap<String, String>();
        paths.put("metaData.idNamespace", regionId);
        paths.put("body.staffUniqueStateId", externalUserId);
        
        for (String entityName : ENTITY_NAMES) {
            Iterable<Entity> staff = repo.findAllByPaths(entityName, paths, neutralQuery);

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
