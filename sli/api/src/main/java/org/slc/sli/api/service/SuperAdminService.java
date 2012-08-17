package org.slc.sli.api.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.util.SecurityUtil.SecurityUtilProxy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

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

    public static final String STATE_EDUCATION_AGENCY = "State Education Agency";
    public static final String LOCAL_EDUCATION_AGENCY = "Local Education Agency";

    /**
     * Returns a list of possible ed-orgs. By default restricted to State and Local Education Agencies
     *
     * @param tenant
     *            Tenant in which the ed-orgs reside.
     * @param edOrg
     *            if provided, list will be restricted to that ed-org or lower.
     */
    public Set<String> getAllowedEdOrgs(String tenant, String edOrg) {
        return getAllowedEdOrgs(tenant, edOrg, null, false);
    }

    /**
     * Returns a list of possible ed-orgs that is one of the interested type.
     * By default restricted to State and Local Education Agencies
     * @param tenant
     *            Tenant in which the ed-orgs reside.
     * @param edOrg
     *            if provided, list will be restricted to that ed-org or lower.
     * @param interestedTypes
     *            if provided list will be restricted to those types
     * @param strict
     *            only return edorgs in database if set to true
     */
    public Set<String> getAllowedEdOrgs(String tenant, String edOrg, Collection<String> interestedTypes, boolean strict) {
        if (interestedTypes == null) {
            interestedTypes = new HashSet<String>();
            interestedTypes.add(STATE_EDUCATION_AGENCY);
            interestedTypes.add(LOCAL_EDUCATION_AGENCY);
        }

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
            String tmpEdOrg = (String) e.getBody().get("stateOrganizationId");
            @SuppressWarnings("unchecked")
            List<String> organizationCategories = (List<String>) e.getBody().get("organizationCategories");
            if (organizationCategories != null) {
                for (String category : organizationCategories) {
                    if (interestedTypes.contains(category)) {
                        edOrgIds.add(tmpEdOrg);
                        break;
                    }
                }
            }
        }

        if (!strict && secUtil.hasRole(RoleInitializer.SEA_ADMINISTRATOR) && !edOrgIds.contains(secUtil.getEdOrg())) {
            edOrgIds.add(secUtil.getEdOrg());
        }

        return edOrgIds;

    }

}
