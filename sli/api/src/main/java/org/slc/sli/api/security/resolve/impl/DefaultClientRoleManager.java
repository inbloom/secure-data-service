package org.slc.sli.api.security.resolve.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.security.resolve.ClientRoleManager;
import org.slc.sli.api.security.roles.RoleRightAccess;
import org.slc.sli.api.service.EntityService;

/**
 * Default converter for client roles to sli roles
 * Does absolutely nothing but give back exactly same list
 * 
 * @author dkornishev
 * 
 */
@Component
public class DefaultClientRoleManager implements ClientRoleManager {
    
    @Autowired
    private RoleRightAccess roleRightAccess;
    
    @Autowired
    private EntityDefinitionStore store;
    
    private EntityService service;

    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName("realm");
        setService(def.getService());
    }
   
    //Injector
    public void setStore(EntityDefinitionStore store) {
        this.store = store;
    }

    //Injector
    public void setService(EntityService service) {
        this.service = service;
    }

    @Override
    /**
     */
    public List<String> resolveRoles(String realmId, List<String> clientRoleNames) {
        return clientRoleNames;
//        System.out.println("In resolveRoles()");
//        List<String> result = new ArrayList<String>();
//        for (String clientRoleName : clientRoleNames) {
//            if (roleRightAccess.findRoleByName(clientRoleName) != null) {
//                result.add(clientRoleName);
//            } else {
//                @SuppressWarnings("unchecked")
//                Map<String, List<String>> mappings = (Map<String, List<String>>) service.get(realmId).get("mappings");
//                System.out.println("--- Mappings are " + mappings + " and client role is " + clientRoleName);
//                if (mappings != null) {
//                    for (String key : mappings.keySet()) {
//                        if (mappings.get(key).contains(clientRoleName)) {
//                            result.add(clientRoleName);
//                        }
//                    }
//                }
//                
//            }
//        }
//        return result;
    }
    
}
