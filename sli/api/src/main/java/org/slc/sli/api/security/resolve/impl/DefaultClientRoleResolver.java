/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.security.resolve.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.resolve.ClientRoleResolver;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

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
    private Repository<Entity> repo;
    
    /**
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    public List<String> resolveRoles(final String realmId, List<String> clientRoleNames) {
        List<String> result = new ArrayList<String>();

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        neutralQuery.addCriteria(new NeutralCriteria("_id", "=", realmId));
        
        Iterable<Entity> realms = repo.findAll("realm", neutralQuery);

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
