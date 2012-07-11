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


package org.slc.sli.api.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import javax.ws.rs.core.Response;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * Holder for security utilities
 * 
 * @author dkornishev
 */
public class SecurityUtil {
    
    private static final Authentication FULL_ACCESS_AUTH;
    public static final String SYSTEM_ENTITY = "system_entity";
    
    private static ThreadLocal<Authentication> cachedAuth = new ThreadLocal<Authentication>();
    
    static {
        SLIPrincipal system = new SLIPrincipal("SYSTEM");
        system.setEntity(new MongoEntity(SYSTEM_ENTITY, new HashMap<String, Object>()));
        
        FULL_ACCESS_AUTH = new PreAuthenticatedAuthenticationToken(system, "API", Arrays.asList(Right.FULL_ACCESS));
    }
    
    public static <T> T sudoRun(SecurityTask<T> task) {
        T toReturn = null;
        
        cachedAuth.set(SecurityContextHolder.getContext().getAuthentication());
        
        try {
            SecurityContextHolder.getContext().setAuthentication(FULL_ACCESS_AUTH);
            toReturn = task.execute();
        } finally {
            SecurityContextHolder.getContext().setAuthentication(cachedAuth.get());
        }
        
        return toReturn;
    }
    
    /**
     * Callback for security-related tasks
     * 
     * @author dkornishev
     */
    public static interface SecurityTask<T> {
        public T execute();
    }
    
    public static boolean hasRight(Right required) {
        Collection<GrantedAuthority> rights = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return rights.contains(required);
    }
    
    public static boolean hasRole(String role) {
        SLIPrincipal principal = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null) {
            principal = (SLIPrincipal) context.getAuthentication().getPrincipal();
            return principal.getRoles().contains(role);
        }
        
        return false;
    }
    
    public static String getEdOrg() {
        SLIPrincipal principal = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null) {
            principal = (SLIPrincipal) context.getAuthentication().getPrincipal();
            return principal.getEdOrg();
        }
        return null;
    }
    
    public static String getTenantId() {
        SLIPrincipal principal = null;
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null) {
            principal = (SLIPrincipal) context.getAuthentication().getPrincipal();
            return principal.getTenantId();
        }
        return null;
    }
    
    public static Response forbiddenResponse() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth instanceof OAuth2Authentication
                && ((OAuth2Authentication) auth).getUserAuthentication() instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException("Login Required");
        }
        
        EntityBody body = new EntityBody();
        body.put("response", "\"You are not authorized to perform this action.\"");
        return Response.status(Response.Status.FORBIDDEN).entity(body).build();
    }
    
    private static Response forbiddenResponse(String response) {
        EntityBody body = new EntityBody();
        body.put("response", response);
        return Response.status(Response.Status.FORBIDDEN).entity(body).build();
    }
    
    /**
     * Throws an InsufficientAuthenticationException (401) if a user is logged in anonymously,
     * e.g. they don't have an access token or they have an expired token.
     */
    public static void ensureAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof OAuth2Authentication
                && ((OAuth2Authentication) auth).getUserAuthentication() instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException("Login Required");
        }
    }
    
    /**
     * Hosted users are those who are hosted in the SLI's IDP.
     * e.g. Developers, operators LEA/SEA admins
     *
     * @return true if the user is hosted, false otherwise
     */
    public static boolean isHostedUser(Repository<Entity> repo, SLIPrincipal principal) {
        String realmId = principal.getRealm();

        Entity entity = repo.findById("realm", realmId);
        if (entity != null) {
            Boolean admin = (Boolean) entity.getBody().get("admin");
            return admin != null ? admin : false;
        }
        return false;
    }
}
