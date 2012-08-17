package org.slc.sli.api.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.util.SecurityUtil.SecurityUtilProxy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Implements logic needed by the SAMT Users resource.
 */
@Component
public class SuperAdminService {
    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    private SecurityUtilProxy secUtil;

    private static final String STATE_EDUCATION_AGENCY = "State Education Agency";
    private static final String LOCAL_EDUCATION_AGENCY = "Local Education Agency";

    /**
     * Returns a list of possible ed-orgs.
     * 
     * @param tenant
     *            Tenant in which the ed-orgs reside.
     * @param edOrg
     *            if provided, list will be restricted to that ed-org or lower.
     */
    public Set<String> getAllowedEdOrgs(String tenant, String edOrg) {
        Set<String> edOrgIds = new HashSet<String>();
        if (secUtil.hasRole(RoleInitializer.LEA_ADMINISTRATOR)) {
            edOrgIds.add(secUtil.getEdOrg());
            return edOrgIds;
        }
        
        NeutralQuery query = new NeutralQuery();

        if (secUtil.getTenantId() == null && tenant != null) {
            query.addCriteria(new NeutralCriteria("metaData.tenantId", NeutralCriteria.OPERATOR_EQUAL, tenant, false));
        }

        for (Entity e : this.repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
            @SuppressWarnings("unchecked")
            List<String> organizationCategories = (List<String>) e.getBody().get("organizationCategories");
            if (organizationCategories != null && isStateOrDistrictEdOrg(organizationCategories)) {
                edOrgIds.add((String) e.getBody().get("stateOrganizationId"));
            }
        }

        if (secUtil.hasRole(RoleInitializer.SEA_ADMINISTRATOR) && !edOrgIds.contains(secUtil.getEdOrg())) {
            edOrgIds.add(secUtil.getEdOrg());
        }
        return edOrgIds;
    }

    private boolean isStateOrDistrictEdOrg(List<String> orgCategories) {
        return orgCategories.contains(STATE_EDUCATION_AGENCY) || orgCategories.contains(LOCAL_EDUCATION_AGENCY);
    }
}
