package org.slc.sli.bulk.extract.util;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utils to extract LEAs
 */
@Component
public class LocalEdOrgExtractHelper {

    private Set<String> extractLEAs;
    public static final Object STATE_EDUCATION_AGENCY = "State Education Agency";

    @Autowired
    @Qualifier("validationRepo")
    Repository<Entity> repository;


    /**
     * Returns all top level LEAs that will be extracted in the tenant
     * @return a set of top level lea ids
     */
    public Set<String> getBulkExtractLEAs() {
        if (extractLEAs == null) {
            extractLEAs = new HashSet<String>();

            Set<String> topLevelLEAs = getTopLevelLEAs();
            for (Set<String> appLeas : getBulkExtractLEAsPerApp().values()) {
                for (String lea : appLeas) {
                    if (topLevelLEAs.contains(lea)) {
                        extractLEAs.add(lea);
                    }
                }
            }
        }
        return extractLEAs;
    }

    private Set<String> getTopLevelLEAs() {
        Set<String> topLEAs = new HashSet<String>();

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ORGANIZATION_CATEGORIES,
                NeutralCriteria.CRITERIA_IN, Arrays.asList(STATE_EDUCATION_AGENCY)));
        final Iterable<Entity> entities = repository.findAll(EntityNames.EDUCATION_ORGANIZATION, query);

        if (entities != null) {
            for (Entity entity : entities) {
                query = new NeutralQuery(new NeutralCriteria(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE,
                        NeutralCriteria.CRITERIA_IN, Arrays.asList(entity.getEntityId())));
                final Iterable<String> topLevelLEAs = repository.findAllIds(EntityNames.EDUCATION_ORGANIZATION, query);
                for (String topLevelLEA : topLevelLEAs) {
                    topLEAs.add(topLevelLEA);
                }
            }
        }
        return topLEAs;
    }

    /**
     * Attempts to get all of the LEAs per app that should have a LEA level extract scheduled.
     *
     * @return a set of the LEA ids that need a bulk extract per app
     */
    @SuppressWarnings("unchecked")
    public Map<String, Set<String>> getBulkExtractLEAsPerApp() {
        NeutralQuery appQuery = new NeutralQuery(new NeutralCriteria("applicationId", NeutralCriteria.CRITERIA_IN,
                getBulkExtractApps()));
        Iterable<Entity> apps = repository.findAll("applicationAuthorization", appQuery);
        Map<String, Set<String>> edorgIds = new HashMap<String, Set<String>>();
        for (Entity app : apps) {
            Set<String> edorgs = new HashSet<String>((Collection<String>) app.getBody().get("edorgs"));
            edorgIds.put((String) app.getBody().get("applicationId"), edorgs);
        }
        return edorgIds;
    }

    /**
     * A helper function to get the list of approved app ids that have bulk extract enabled
     *
     * @return a set of approved bulk extract app ids
     */
    @SuppressWarnings("unchecked")
    public Set<String> getBulkExtractApps() {
        TenantContext.setIsSystemCall(true);
        Iterable<Entity> apps = repository.findAll("application", new NeutralQuery());
        TenantContext.setIsSystemCall(false);
        Set<String> appIds = new HashSet<String>();
        for (Entity app : apps) {
            if (app.getBody().containsKey("isBulkExtract") && (Boolean) app.getBody().get("isBulkExtract")) {
                if (((Map<String, Object>) app.getBody().get("registration")).get("status").equals("APPROVED")) {
                    appIds.add(app.getEntityId());
                }
            }
        }
        return appIds;
    }

}
