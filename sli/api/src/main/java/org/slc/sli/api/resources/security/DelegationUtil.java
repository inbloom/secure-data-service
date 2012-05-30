package org.slc.sli.api.resources.security;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.resolver.EdOrgToChildEdOrgNodeFilter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Common methods for classes that deal with delegation
 * 
 */
@Component
public class DelegationUtil {
    
    @Autowired
    private EdOrgToChildEdOrgNodeFilter edOrgNodeFilter;
    
    @Autowired
    Repository<Entity> repo;
    
    public String getUsersStateUniqueId() {
        SLIPrincipal principal = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null) {
            principal = (SLIPrincipal) context.getAuthentication().getPrincipal();
            return principal.getEdOrg();
        }
        return null;
    }
    
    public List<String> getDelegateEdOrgs() {
        String edOrg = getUsersStateUniqueId();
        
        List<String> myEdOrgsIds = edOrgNodeFilter.getChildEducationOrganizations(edOrg);
        List<String> delegateEdOrgs = new ArrayList<String>();
        for (String curEdOrg : myEdOrgsIds) {
            NeutralQuery delegateQuery = new NeutralQuery();
            delegateQuery.addCriteria(new NeutralCriteria("appApprovalEnabled", "=", true));
            delegateQuery.addCriteria(new NeutralCriteria("localEdOrgId", "=", curEdOrg));
            if (repo.findOne(EntityNames.ADMIN_DELEGATION, delegateQuery) != null) {
                delegateEdOrgs.add(curEdOrg);
            }
        }
        debug("Ed orgs that I can delegate are {}", delegateEdOrgs);
        return delegateEdOrgs;
    }
    
}
