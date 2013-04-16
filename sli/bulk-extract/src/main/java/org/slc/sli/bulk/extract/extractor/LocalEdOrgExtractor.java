package org.slc.sli.bulk.extract.extractor;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Creates local ed org tarballs
 */
public class LocalEdOrgExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(LocalEdOrgExtractor.class);
    private Repository<Entity> repository;

    /**
     * Creates unencrypted LEA bulk extract files if any are needed for the given tenant
     * @param tenant name of tenant to extract
     */
    public void execute(String tenant) {
        //TODO replace stub do it
        LOG.debug("STUB!");
    }



    /**
     * A helper function to get the list of approved app ids that have bulk extract enabled
     *
     * @return a set of approved bulk extract app ids
     */
    public Set<String> getBulkExctractApps() {
        NeutralQuery appQuery = new NeutralQuery(new NeutralCriteria("isBulkExtract", NeutralCriteria.OPERATOR_EQUAL,
                true));
        appQuery.addCriteria(new NeutralCriteria("registration.status", NeutralCriteria.OPERATOR_EQUAL, "APPROVED"));
        TenantContext.setIsSystemCall(true);
        Iterable<Entity> apps = repository.findAll("application", appQuery);
        TenantContext.setIsSystemCall(false);
        Set<String> appIds = new HashSet<String>();
        for (Entity app : apps) {
            appIds.add(app.getEntityId());
        }
        return appIds;
    }

    /**
     * Attempts to get all of the LEAs that should have a LEA level extract scheduled.
     *
     * @return a set of the LEA ids that need a bulk extract
     */
    @SuppressWarnings("unchecked")
    public Set<String> getBulkExtractLEAs() {
        // for each tenant
        // get LEAs
        // continue if no bulk LEAs are found
        // start the bulk extract process



        NeutralQuery appQuery = new NeutralQuery(new NeutralCriteria("applicationId", NeutralCriteria.CRITERIA_IN,
                getBulkExctractApps()));
        Iterable<Entity> apps = repository.findAll("applicationAuthorization", appQuery);
        Set<String> edorgIds = new HashSet<String>();
        for (Entity app : apps) {
            edorgIds.addAll((Collection<String>) app.getBody().get("edorgs"));
        }
        return edorgIds;
    }

    public void setRepository(Repository<Entity> repository) {
        this.repository = repository;
    }

    public Repository<Entity> getRepository() {
        return repository;
    }
}
