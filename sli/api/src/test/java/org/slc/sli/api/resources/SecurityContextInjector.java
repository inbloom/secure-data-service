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


package org.slc.sli.api.resources;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.mockito.Mockito;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Simple class for injecting a security context for unit tests.
 *
 * @author shalka
 */
@Component
public class SecurityContextInjector {
    public static final String ED_ORG_ID = "1111-1111-1111";
    private static final String DEFAULT_REALM_ID = "dc=slidev,dc=net";
    public static final String TENANT_ID = "Midgar";
    private static final String SESSION_ID = "SOME_SESSION_ID";
    
    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    private RolesToRightsResolver resolver;

    public void setCustomContext(String user, String fullName, String realm, List<String> roles, Entity entity,
            String edOrgId) {
        SLIPrincipal principal = buildPrincipal(user, fullName, realm, roles, entity);
        principal.setEdOrg(edOrgId);
        setSecurityContext(principal);
    }

    public void setAdminContext() {
        String user = "administrator";
        String fullName = "IT Administrator";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("admin-staff");
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity);
        principal.setEdOrg(ED_ORG_ID);
        principal.setEdOrgId(ED_ORG_ID);
        setSecurityContext(principal);
    }

    public void setAccessAllAdminContext() {
        String user = "administrator";
        String fullName = "IT Administrator";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("access-all-staff");
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity);
        principal.setEdOrg(ED_ORG_ID);
        setSecurityContext(principal);
    }


    public void setDeveloperContext() {
        String user = "developer";
        String fullName = "App Developer";
        List<String> roles = Arrays.asList(RoleInitializer.APP_DEVELOPER);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("admin-staff");
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity);
        principal.setExternalId("developer");
        principal.setRoles(roles);
        setSecurityContext(principal);

        // TODO why do developers have admin access?
        Right[] rights = new Right[] { Right.ADMIN_ACCESS, Right.DEV_APP_CRUD, Right.INGEST_DATA }; //Have INGEST_DATA right in sandbox
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
                .getAuthentication().getCredentials(), Arrays.asList(rights));

        debug("elevating rights to {}", Arrays.toString(rights));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public void setLeaAdminContext() {
        String user = "LeaAdmin";
        String fullName = "LEA Admin";
        List<String> roles = Arrays.asList(RoleInitializer.LEA_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("admin-staff");
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity);
        principal.setExternalId("lea_admin");
        setSecurityContext(principal);

        Right[] rights = new Right[] { Right.ADMIN_ACCESS, Right.EDORG_APP_AUTHZ };
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
                .getAuthentication().getCredentials(), Arrays.asList(rights));

        debug("elevating rights to {}", Arrays.toString(rights));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public void setSeaAdminContext() {
        String user = "LeaAdmin";
        String fullName = "LEA Admin";
        List<String> roles = Arrays.asList(RoleInitializer.SEA_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("admin-staff");
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity);
        principal.setExternalId("lea_admin");
        setSecurityContext(principal);

        Right[] rights = new Right[] { Right.ADMIN_ACCESS, Right.EDORG_DELEGATE };
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
                .getAuthentication().getCredentials(), Arrays.asList(rights));

        debug("elevating rights to {}", Arrays.toString(rights));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public void setOperatorContext() {
        String user = "Operator";
        String fullName = "SLC Operator";
        List<String> roles = Arrays.asList(RoleInitializer.SLC_OPERATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("admin-staff");
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity);
        principal.setRoles(roles);
        setSecurityContext(principal);

        Right[] rights = new Right[] { Right.ADMIN_ACCESS, Right.SLC_APP_APPROVE };
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
                .getAuthentication().getCredentials(), Arrays.asList(rights));

        debug("elevating rights to {}", Arrays.toString(rights));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public void setRealmAdminContext() {
        String user = "realmadmin";
        String fullName = "Realm Administrator";
        List<String> roles = Arrays.asList(RoleInitializer.REALM_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("admin-staff");
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity);
        principal.setEdOrg("fake-ed-org");
        principal.setRoles(roles);
        principal.setTenantId(TENANT_ID);
        setSecurityContext(principal);

        Right[] rights = new Right[] {  Right.ADMIN_ACCESS, Right.READ_GENERAL, Right.CRUD_REALM, Right.READ_PUBLIC, Right.CRUD_ROLE };
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
                .getAuthentication().getCredentials(), Arrays.asList(rights));
        SecurityContextHolder.getContext().setAuthentication(token);

    }

    public void setAdminContextWithElevatedRights() {
        setAdminContext();

        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
                .getAuthentication().getCredentials(), Arrays.asList(Right.FULL_ACCESS));

        debug("elevating rights to {}", Right.FULL_ACCESS);
        SecurityContextHolder.getContext().setAuthentication(token);

    }

    public void setAccessAllAdminContextWithElevatedRights() {
        setAccessAllAdminContext();

        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
                .getAuthentication().getCredentials(), Arrays.asList(Right.FULL_ACCESS));

        debug("elevating rights to {}", Right.FULL_ACCESS);
        SecurityContextHolder.getContext().setAuthentication(token);

    }

    public void setResolver(RolesToRightsResolver resolver) {
        this.resolver = resolver;
    }

    private PreAuthenticatedAuthenticationToken getAuthenticationToken(String token, final SLIPrincipal principal) {
        final RolesToRightsResolver finalResolver = resolver;
        Set<GrantedAuthority> authorities = SecurityUtil
                .sudoRun(new SecurityUtil.SecurityTask<Set<GrantedAuthority>>() {
                    @Override
                    public Set<GrantedAuthority> execute() {
                        return finalResolver.resolveRoles(principal.getTenantId(), principal.getRealm(), principal.getRoles());
                    }
                });

        return new PreAuthenticatedAuthenticationToken(principal, token, authorities);
    }

    public void setEducatorContext() {
        String user = "educator";
        String fullName = "Educator";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR);

        Entity entity = Mockito.mock(Entity.class);
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity);
        principal.setRoles(roles);
        setSecurityContext(principal);
    }

    /**
     * Injects the context of 'demo' user.
     */
    public void setDemoContext() {
        String user = "demo";
        String fullName = "demo";
        List<String> roles = Arrays.asList(RoleInitializer.IT_ADMINISTRATOR);
        Entity entity = Mockito.mock(Entity.class);
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity);
        setSecurityContext(principal);
    }

    private SLIPrincipal setSecurityContext(SLIPrincipal principal) {
        String token = "AQIC5wM2LY4SfczsoqTgHpfSEciO4J34Hc5ThvD0QaM2QUI.*AAJTSQACMDE.*";

        debug("assembling authentication token");
        PreAuthenticatedAuthenticationToken authenticationToken = getAuthenticationToken(token, principal);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return principal;
    }

    private SLIPrincipal buildPrincipal(String user, String fullName, String realmId, List<String> roles,
            Entity principalEntity) {
        SLIPrincipal principal = new SLIPrincipal(user);
        principal.setId(user);
        principal.setName(fullName);
        principal.setRoles(roles);
        principal.setEntity(principalEntity);
        principal.setRealm(realmId);
        // Create the user session for security caching
        Entity session = repo.create("userSession", new HashMap<String, Object>());
        principal.setSessionId(session.getEntityId());
        return principal;
    }

}
