package org.slc.sli.bulk.extract.extractor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates local ed org tarballs
 */
public class LocalEdOrgExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(LocalEdOrgExtractor.class);
    private Repository<Entity> repository;
    private Map<String, String> edorgToLEACache;

    /**
     * Creates unencrypted LEA bulk extract files if any are needed for the given tenant
     * @param tenant name of tenant to extract
     */
    public void execute(String tenant) {
        //TODO replace stub do it
        LOG.debug("STUB!");
        
        Set<String> leas = getBulkExtractLEAs();
        edorgToLEACache = buildEdorgCache(leas);
        
    }

    /**
     * Returns a map that maps an edorg to it's top level LEA, used as a cache
     * to speed up extract
     * 
     * @param leas
     * @return
     */
    private Map<String, String> buildEdorgCache(Set<String> leas) {
        Map<String, String> cache = new HashMap<String, String>();
        for (String lea : leas) {
            Set<String> children = getChildEdOrgs(Arrays.asList(lea));
            for (String child : children) {
                cache.put(child, lea);
            }
        }
        return cache;
    }

    /**
     * A helper function to get the list of approved app ids that have bulk extract enabled
     *
     * @return a set of approved bulk extract app ids
     */
    @SuppressWarnings("unchecked")
    public Set<String> getBulkExtractApps() {
        // Butt-hole table scans prevent us from using a query, so we'll just scan all the apps!
        NeutralQuery appQuery = new NeutralQuery(new NeutralCriteria("isBulkExtract", NeutralCriteria.OPERATOR_EQUAL,
                true));
        appQuery.addCriteria(new NeutralCriteria("registration.status", NeutralCriteria.OPERATOR_EQUAL, "APPROVED"));
        TenantContext.setIsSystemCall(true);
        Iterable<Entity> apps = repository.findAll("application", new NeutralQuery());
        TenantContext.setIsSystemCall(false);
        Set<String> appIds = new HashSet<String>();
        for (Entity app : apps) {
            if (app.getBody().containsKey("isBulkExtract") && (Boolean) app.getBody().get("isBulkExtract") == true) {
                if (((String) ((Map<String, Object>) app.getBody().get("registration")).get("status"))
                        .equals("APPROVED")) {
                    appIds.add(app.getEntityId());
                }
            }
        }
        return appIds;
    }
    
    /**
     * Returns a list of child edorgs given a collection of parents
     * 
     * @param edOrgs
     * @return a set of child edorgs
     */
    private Set<String> getChildEdOrgs(Collection<String> edOrgs) {
        if (edOrgs.isEmpty()) {
            return new HashSet<String>();
        }
        
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE,
                NeutralCriteria.CRITERIA_IN, edOrgs));
        Iterable<Entity> childrenIds = repository.findAll(EntityNames.EDUCATION_ORGANIZATION, query);
        Set<String> children = new HashSet<String>();
        for (Entity child : childrenIds) {
            children.add(child.getEntityId());
        }
        if (!children.isEmpty()) {
            children.addAll(getChildEdOrgs(children));
        }
        return children;
    }

    /**
     * Attempts to get all of the LEAs that should have a LEA level extract scheduled.
     *
     * @return a set of the LEA ids that need a bulk extract
     */
    @SuppressWarnings("unchecked")
    public Set<String> getBulkExtractLEAs() {
        NeutralQuery appQuery = new NeutralQuery(new NeutralCriteria("applicationId", NeutralCriteria.CRITERIA_IN,
                getBulkExtractApps()));
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
