package org.slc.sli.api.resources.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * TODO git rm me
 * Temporary check to make sure ingestion has not been locked for this tenant
 * Remove this once ingestion is fixed to be able to run concurrent jobs
 */
@Component
public class IngestionTenantLockChecker {
    private final Repository<Entity> repo;

    @Autowired
    public IngestionTenantLockChecker(@Qualifier("validationRepo") Repository<Entity> repo) {
        super();
        this.repo = repo;
    }

    public boolean ingestionLocked(String tenantId) {
        return repo.findOne("tenantJobLock", new NeutralQuery(new NeutralCriteria("_id", "=", tenantId))) != null;
    }

}
