package org.slc.sli.api.security.resolve.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.security.resolve.ClientRoleResolver;
import org.slc.sli.api.security.roles.RoleRightAccess;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
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
    private RoleRightAccess roleRightAccess;

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private EntityRepository repo;

    private EntityService service;

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName("realm");
        setService(def.getService());
    }

    // Injector
    public void setStore(EntityDefinitionStore store) {
        this.store = store;
    }

    // Injector
    public void setService(EntityService service) {
        this.service = service;
    }

    @SuppressWarnings("unchecked")
    @Override
    /**
     */
    public List<String> resolveRoles(final String realmId,
            List<String> clientRoleNames) {
        List<String> result = new ArrayList<String>();
        Map<String, Object> realm = SecurityUtil.sudoRun(new SecurityTask<Map<String, Object>>() {
        
         @Override
         public Map<String, Object> execute() {
             Iterable<Entity> realms = repo.findByQuery("realm", new Query(Criteria.where("body.realm").is(realmId)), 0, 1);
             Map<String, Object> realm = null;
             for (Entity firstRealm : realms) {
                 realm = firstRealm.getBody();
             }
             return realm;
         }
         });
        

        Map<String, List<String>> mappings = null;
        if (realm != null) {
            mappings = (Map<String, List<String>>) realm.get("mappings");
        }
        if (mappings != null) {
            for (String sliRole : mappings.keySet()) {
                List<String> clientRolesForSliRole = mappings.get(sliRole);
                for (String clientRole : clientRoleNames) {
                    if (clientRolesForSliRole.contains(clientRole)) {
                        result.add(sliRole);
                    }
                }
            }
        }
        return result;
    }
}
