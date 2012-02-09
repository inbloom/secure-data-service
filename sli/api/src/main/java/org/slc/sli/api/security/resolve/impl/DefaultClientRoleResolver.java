package org.slc.sli.api.security.resolve.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.resolve.ClientRoleResolver;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;

/**
 * Default converter for client roles to sli roles Does absolutely nothing but
 * give back exactly same list
 * 
 * @author dkornishev
 * 
 */
@Component
public class DefaultClientRoleResolver implements ClientRoleResolver {
    
    @Autowired
    private EntityRepository repo;
    
    /**
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    public List<String> resolveRoles(final String realmId, List<String> clientRoleNames) {
        List<String> result = new ArrayList<String>();
        Iterable<Entity> realms = repo.findByQuery("realm", new Query(Criteria.where("body.realm").is(realmId)), 0, 1);
        Map<String, Object> realm = null;
        for (Entity firstRealm : realms) {
            realm = firstRealm.getBody();
        }
        
        Map<String, List<Map<String, Object>>> mappings = null;
        
        if (realm != null) {
            mappings = (Map<String, List<Map<String, Object>>>) realm.get("mappings");
        }
        
        if (mappings != null) {
            List<Map<String, Object>> roles = mappings.get("role");
            
            if (roles != null) {
                for (Map<String, Object> role : roles) {
                    String sliRoleName = (String) role.get("sliRoleName");
                    List<String> clientRoleNameList = (List<String>) role.get("clientRoleName");
                    
                    // Intersection operation. Discovers if any user roles match role mappings in the role def
                    clientRoleNameList.retainAll(clientRoleNames);
                    
                    if (!clientRoleNameList.isEmpty()) {
                        result.add(sliRoleName);
                    }
                }
            }
        }
        return result;
    }
}
