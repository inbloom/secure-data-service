package org.slc.sli.api.service;

import java.util.HashSet;
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

    /**
     * Returns a list of possible ed-orgs.
     * @param tenant Tenant in which the ed-orgs reside.
     * @param edOrg if provided, list will be restricted to that ed-org or lower.
     */
    public Set<String> getAllowedEdOrgs(String tenant, String edOrg) {
        NeutralQuery query = new NeutralQuery();

        if (secUtil.getTenantId() == null && tenant != null) {
            query.addCriteria(new NeutralCriteria("metaData.tenantId", NeutralCriteria.OPERATOR_EQUAL, tenant, false));
        }

        if (edOrg != null) {
            NeutralQuery currentEdOrgQuery = new NeutralQuery(new NeutralCriteria("stateOrganizationId",
                    NeutralCriteria.OPERATOR_EQUAL, edOrg));
            Entity usersEdOrg = this.repo.findOne(EntityNames.EDUCATION_ORGANIZATION, currentEdOrgQuery);
            query.addCriteria(new NeutralCriteria("metaData.edOrgs", NeutralCriteria.OPERATOR_EQUAL, usersEdOrg
                    .getEntityId(), false));
        }

        Set<String> edOrgIds = new HashSet<String>();
        for (Entity e : this.repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
            edOrgIds.add((String) e.getBody().get("stateOrganizationId"));
        }
        
        if (secUtil.hasRole(RoleInitializer.SEA_ADMINISTRATOR) && !edOrgIds.contains(secUtil.getEdOrg())) {
            edOrgIds.add(secUtil.getEdOrg());
        }
        return edOrgIds;
    }
}
