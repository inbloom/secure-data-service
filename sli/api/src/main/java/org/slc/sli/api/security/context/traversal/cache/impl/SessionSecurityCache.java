package org.slc.sli.api.security.context.traversal.cache.impl;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slc.sli.api.constants.EntityNames;
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
    public static final String STUDENT_CACHE = "students";
    public static final String SECTION_CACHE = "sections";
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
        userSession.getBody().put(STUDENT_CACHE, ids.toArray());
        
        //Put it back into mongo
        repo.update(USER_SESSION, userSession);
        
    }
    
    @Override
    public Set<String> retrieve(String cacheId) {
        return new HashSet<String>((Set<String>) getUserSession().getBody().get(cacheId));
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
