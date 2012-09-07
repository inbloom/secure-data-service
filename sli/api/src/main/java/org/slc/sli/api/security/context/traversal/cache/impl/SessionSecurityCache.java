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

package org.slc.sli.api.security.context.traversal.cache.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.traversal.cache.SecurityCachingStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SessionSecurityCache implements SecurityCachingStrategy {
    public static final String USER_SESSION = "userSession";
    
    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;
    
    private SLIPrincipal principal;
    
    private void initializePrincipal() {
        this.principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    @Override
    public void warm(String cacheId, Set<String> ids) {
        
        //Get my principal so I can access my session object
        Entity userSession = getUserSession();
        
        //Update the session with the new cache.
        userSession.getBody().put(cacheId, ids.toArray());
        
        //Put it back into mongo
        repo.update(USER_SESSION, userSession);
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Set<String> retrieve(String cacheId) {
        info("Using cached context for {}", cacheId);
        Map<String, Object> body = (Map<String, Object>) getUserSession().getBody();
        return new HashSet<String>((List<String>) body.get(cacheId));
    }
    
    @Override
    public void expire() {
        // We don't expire this thing. It goes away with user sessions.
        
    }
    
    @Override
    public boolean contains(String cacheId) {
        return getUserSession().getBody().containsKey(cacheId);
    }
    
    private Entity getUserSession() {
        initializePrincipal();
        NeutralQuery sessionQuery = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, principal.getSessionId()));
        Entity userSession = repo.findOne(USER_SESSION, sessionQuery);
        return userSession;
    }
    
}
