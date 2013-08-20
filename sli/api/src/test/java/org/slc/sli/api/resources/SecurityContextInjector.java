/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.util.*;

import org.mockito.Mockito;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.security.EdOrgContextRightsCache;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
    private static final String ADMIN_TYPE = "admin";

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    private RolesToRightsResolver resolver;

    public void setCustomContext(String user, String fullName, String realm, List<String> roles, Entity entity,
            String edOrgId) {
        SLIPrincipal principal = buildPrincipal(user, fullName, realm, roles, entity, edOrgId, new EdOrgContextRightsCache());

        setSecurityContext(principal, false);
    }

    public void setAdminContext() {
        String user = "administrator";
        String fullName = "IT Administrator";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("admin-staff");
        Mockito.when(entity.getEntityId()).thenReturn("-133");
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, ED_ORG_ID, new EdOrgContextRightsCache());

        principal.setUserType(ADMIN_TYPE);
        setSecurityContext(principal, false);
    }

    public void setStaffContext() {
        String user = "administrator";
        String fullName = "IT Administrator";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("staff");
        Mockito.when(entity.getEntityId()).thenReturn("blahblahstaffblahblahmerp");
        List<Right> rights = Arrays.asList(
                Right.WRITE_GENERAL, Right.READ_GENERAL, Right.READ_RESTRICTED, Right.WRITE_RESTRICTED, Right.READ_PUBLIC, Right.WRITE_PUBLIC);

        EdOrgContextRightsCache edOrgContextRights = new EdOrgContextRightsCache();
        Map<String, Collection<GrantedAuthority>> contextRights = new HashMap<String, Collection<GrantedAuthority>>();
        contextRights.put(Right.STAFF_CONTEXT.name(), new ArrayList<GrantedAuthority>(rights));
        edOrgContextRights.put(ED_ORG_ID, contextRights);

        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, ED_ORG_ID, edOrgContextRights);
        setSecurityContext(principal, new HashSet<GrantedAuthority>(rights));

        SecurityUtil.setUserContext(SecurityUtil.UserContext.STAFF_CONTEXT);
    }

    public void setDualContext() {
        String user = "administrator";
        String fullName = "IT Administrator";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("staff");
        Mockito.when(entity.getEntityId()).thenReturn("blahblahstaffblahblahmerp");
        List<Right> rights = Arrays.asList(
                Right.WRITE_GENERAL, Right.READ_GENERAL, Right.READ_RESTRICTED, Right.WRITE_RESTRICTED, Right.READ_PUBLIC, Right.WRITE_PUBLIC);

        List<Right> teacherRights = Arrays.asList(
                Right.WRITE_GENERAL, Right.READ_GENERAL);

        EdOrgContextRightsCache edOrgContextRights = new EdOrgContextRightsCache();
        Map<String, Collection<GrantedAuthority>> contextRights = new HashMap<String, Collection<GrantedAuthority>>();
        contextRights.put(Right.STAFF_CONTEXT.name(), new ArrayList<GrantedAuthority>(rights));
        contextRights.put(Right.TEACHER_CONTEXT.name(),  new ArrayList<GrantedAuthority>(teacherRights));
        edOrgContextRights.put(ED_ORG_ID, contextRights);

        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, ED_ORG_ID, edOrgContextRights);
        setSecurityContext(principal, new HashSet<GrantedAuthority>(rights));

        SecurityUtil.setUserContext(SecurityUtil.UserContext.DUAL_CONTEXT);
    }

    public void setAccessAllAdminContext() {
        String user = "administrator";
        String fullName = "IT Administrator";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("access-all-staff");
        Mockito.when(entity.getEntityId()).thenReturn(user);
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, ED_ORG_ID, new EdOrgContextRightsCache());
        setSecurityContext(principal, false);

        SecurityUtil.setUserContext(SecurityUtil.UserContext.STAFF_CONTEXT);
    }


    public void setDeveloperContext() {
        String user = "developer";
        String fullName = "App Developer";
        List<String> roles = Arrays.asList(RoleInitializer.APP_DEVELOPER);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("admin-staff");
        Mockito.when(entity.getEntityId()).thenReturn(user);
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, ED_ORG_ID, new EdOrgContextRightsCache());
        principal.setExternalId("developer");
        principal.setRoles(roles);
        setSecurityContext(principal, true);

        Right[] rights = new Right[] { Right.ADMIN_ACCESS, Right.DEV_APP_CRUD, Right.READ_PUBLIC };
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
                .getAuthentication().getCredentials(), Arrays.asList(rights));

        debug("elevating rights to {}", Arrays.toString(rights));
        SecurityContextHolder.getContext().setAuthentication(token);
        SecurityUtil.setUserContext(SecurityUtil.UserContext.NO_CONTEXT);
    }

    public void setLeaAdminContext() {
        String user = "LeaAdmin";
        String fullName = "LEA Admin";
        List<String> roles = Arrays.asList(RoleInitializer.LEA_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("admin-staff");
        Mockito.when(entity.getEntityId()).thenReturn(user);
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, null, null);
        principal.setExternalId("lea_admin");
        principal.setAdminRealmAuthenticated(true);
        setSecurityContext(principal, true);

        Right[] rights = new Right[] { Right.ADMIN_ACCESS, Right.EDORG_APP_AUTHZ };
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
                .getAuthentication().getCredentials(), Arrays.asList(rights));

        debug("elevating rights to {}", Arrays.toString(rights));
        SecurityContextHolder.getContext().setAuthentication(token);
        SecurityUtil.setUserContext(SecurityUtil.UserContext.STAFF_CONTEXT);
    }

    public void setSeaAdminContext() {
        String user = "LeaAdmin";
        String fullName = "LEA Admin";
        List<String> roles = Arrays.asList(RoleInitializer.SEA_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("admin-staff");
        Mockito.when(entity.getEntityId()).thenReturn(user);
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, ED_ORG_ID, new EdOrgContextRightsCache());
        principal.setExternalId("lea_admin");
        setSecurityContext(principal, true);

        Right[] rights = new Right[] { Right.ADMIN_ACCESS, Right.EDORG_DELEGATE };
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
                .getAuthentication().getCredentials(), Arrays.asList(rights));

        debug("elevating rights to {}", Arrays.toString(rights));
        SecurityContextHolder.getContext().setAuthentication(token);
        SecurityUtil.setUserContext(SecurityUtil.UserContext.STAFF_CONTEXT);
    }

    public void setOperatorContext() {
        String user = "Operator";
        String fullName = "SLC Operator";
        List<String> roles = Arrays.asList(RoleInitializer.SLC_OPERATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("admin-staff");
        Mockito.when(entity.getEntityId()).thenReturn(user);
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, ED_ORG_ID, new EdOrgContextRightsCache());
        principal.setRoles(roles);
        setSecurityContext(principal, true);

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
        Mockito.when(entity.getEntityId()).thenReturn(user);
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, "fake-ed-org", new EdOrgContextRightsCache());
        principal.setRoles(roles);
        principal.setTenantId(TENANT_ID);
        setSecurityContext(principal, false);

        Right[] rights = new Right[] {  Right.ADMIN_ACCESS, Right.READ_GENERAL, Right.CRUD_REALM, Right.READ_PUBLIC, Right.CRUD_ROLE };
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
                .getAuthentication().getCredentials(), Arrays.asList(rights));
        SecurityContextHolder.getContext().setAuthentication(token);

        SecurityUtil.setUserContext(SecurityUtil.UserContext.STAFF_CONTEXT);
    }

    public void setAdminContextWithElevatedRights() {
        Map<String, Collection<GrantedAuthority>> edorgRights = new HashMap<String, Collection<GrantedAuthority>>();
        setAdminContext();

        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(SecurityContextHolder
                .getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
                .getAuthentication().getCredentials(), Arrays.asList(Right.FULL_ACCESS));

        debug("elevating rights to {}", Right.FULL_ACCESS);
        SecurityContextHolder.getContext().setAuthentication(token);
        SecurityUtil.setUserContext(SecurityUtil.UserContext.STAFF_CONTEXT);

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

    private PreAuthenticatedAuthenticationToken getAuthenticationToken(String token, final SLIPrincipal principal, final boolean isAdminRealm) {
        final RolesToRightsResolver finalResolver = resolver;
        Set<GrantedAuthority> authorities = SecurityUtil
                .sudoRun(new SecurityUtil.SecurityTask<Set<GrantedAuthority>>() {
                    @Override
                    public Set<GrantedAuthority> execute() {
                        return finalResolver.resolveRolesIntersect(principal.getTenantId(), principal.getRealm(), principal.getRoles(), isAdminRealm, false);
                    }
                });

        return new PreAuthenticatedAuthenticationToken(principal, token, authorities);
    }

    private PreAuthenticatedAuthenticationToken getAuthenticationToken(String token, final SLIPrincipal principal, Set<GrantedAuthority> rights) {
        return new PreAuthenticatedAuthenticationToken(principal, token, rights);
    }

    public void setOauthAuthenticationWithEducationRole() {
        String user = "educator";
        String fullName = "Educator";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getEntityId()).thenReturn(user);
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, ED_ORG_ID, new EdOrgContextRightsCache());
        principal.setRoles(roles);
        principal.setSelfRights(Arrays.asList(new GrantedAuthority[]{ Right.READ_RESTRICTED}));

        setOauthSecurityContext(principal, false);
        SecurityUtil.setUserContext(SecurityUtil.UserContext.TEACHER_CONTEXT);
    }

    public void setEducatorContext() {
        String user = "educator";
        String fullName = "Educator";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getEntityId()).thenReturn(user);

        EdOrgContextRightsCache edOrgContextRights = new EdOrgContextRightsCache();
        Map<String, Collection<GrantedAuthority>> contextRights = new HashMap<String, Collection<GrantedAuthority>>();
        contextRights.put(Right.TEACHER_CONTEXT.name(), new ArrayList<GrantedAuthority>(Arrays.asList(Right.READ_RESTRICTED)));
        edOrgContextRights.put(ED_ORG_ID, contextRights);

        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, ED_ORG_ID, edOrgContextRights);
        principal.setRoles(roles);
        principal.setSelfRights(Arrays.asList(new GrantedAuthority[]{ Right.READ_RESTRICTED}));
        setSecurityContext(principal, false);
        SecurityUtil.setUserContext(SecurityUtil.UserContext.TEACHER_CONTEXT);
    }

    public void setEducatorContext(String userId) {
        String user = "educator";
        String fullName = "Educator";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn(EntityNames.TEACHER);
        Mockito.when(entity.getEntityId()).thenReturn(userId);
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, ED_ORG_ID, new EdOrgContextRightsCache());
        principal.setRoles(roles);
        principal.setSelfRights(Arrays.asList(new GrantedAuthority[]{ Right.READ_RESTRICTED}));
        setSecurityContext(principal, false);
        SecurityUtil.setUserContext(SecurityUtil.UserContext.TEACHER_CONTEXT);
    }

    public void setStudentContext(Entity entity) {
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.STUDENT);

        SLIPrincipal principal = buildPrincipal("Studious", "Estudiando", DEFAULT_REALM_ID, roles, entity, ED_ORG_ID, new EdOrgContextRightsCache());
        principal.setRoles(roles);
        principal.setSelfRights(Arrays.asList(new GrantedAuthority[]{Right.READ_STUDENT_OWNED}));
        setSecurityContext(principal, false);
        SecurityUtil.setUserContext(SecurityUtil.UserContext.NO_CONTEXT);
    }

    public void setAnonymousContext() {
        OAuth2Authentication token = new OAuth2Authentication(
                new ClientToken("blah", "blah", new HashSet<String>()),
                new AnonymousAuthenticationToken("blah", new Object(), new ArrayList<GrantedAuthority>(Arrays.asList(Right.ANONYMOUS_ACCESS))));
        SecurityContextHolder.getContext().setAuthentication(token);
        SecurityUtil.setUserContext(SecurityUtil.UserContext.NO_CONTEXT);
    }

    /**
     * Injects the context of 'demo' user.
     */
    public void setDemoContext() {
        String user = "demo";
        String fullName = "demo";
        List<String> roles = Arrays.asList(RoleInitializer.IT_ADMINISTRATOR);
        Entity entity = Mockito.mock(Entity.class);
        SLIPrincipal principal = buildPrincipal(user, fullName, DEFAULT_REALM_ID, roles, entity, ED_ORG_ID, new EdOrgContextRightsCache());
        setSecurityContext(principal, false);
    }

    public SLIPrincipal setSecurityContext(SLIPrincipal principal, boolean isAdminRealm) {
        String token = "AQIC5wM2LY4SfczsoqTgHpfSEciO4J34Hc5ThvD0QaM2QUI.*AAJTSQACMDE.*";

        debug("assembling authentication token");
        PreAuthenticatedAuthenticationToken authenticationToken = getAuthenticationToken(token, principal, isAdminRealm);
        ClientToken clientToken = new ClientToken("BOOP", null, null, null, null);
        OAuth2Authentication auth = new OAuth2Authentication(clientToken, authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(auth);
        SecurityUtil.getSLIPrincipal().setAuthorizingEdOrgs(new HashSet<String>(Arrays.asList(principal.getEdOrg())));

        return principal;
    }

    public SLIPrincipal setOauthSecurityContext(SLIPrincipal principal, boolean  isAdminRealm) {
        String token = "AQIC5wM2LY4SfczsoqTgHpfSEciO4J34Hc5ThvD0QaM2QUI.*AAJTSQACMDE.*";
        debug("assembling authentication token");
        PreAuthenticatedAuthenticationToken authenticationToken = getAuthenticationToken(token, principal, isAdminRealm);
        OAuth2Authentication oauth = new OAuth2Authentication(new ClientToken("clientId", "clientSecret", Collections.singleton("scope")), authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(oauth);
        SecurityUtil.getSLIPrincipal().setAuthorizingEdOrgs(new HashSet<String>(Arrays.asList(principal.getEdOrg())));
        return  principal;
    }

    public void addToAuthorizingEdOrgs(String edOrgId) {
        SLIPrincipal principal = SecurityUtil.getSLIPrincipal();
        Set<String> authorizing = principal.getAuthorizingEdOrgs();
        authorizing.add(edOrgId);
        principal.setAuthorizingEdOrgs(authorizing);
    }

    private SLIPrincipal setSecurityContext(SLIPrincipal principal, Set<GrantedAuthority> rights) {
        String token = "AQIC5wM2LY4SfczsoqTgHpfSEciO4J34Hc5ThvD0QaM2QUI.*AAJTSQACMDE.*";

        debug("assembling authentication token");
        PreAuthenticatedAuthenticationToken authenticationToken = getAuthenticationToken(token, principal, rights);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityUtil.getSLIPrincipal().setAuthorizingEdOrgs(new HashSet<String>(Arrays.asList(principal.getEdOrg())));

        return principal;
    }

    private SLIPrincipal buildPrincipal(String user, String fullName, String realmId, List<String> roles,
            Entity principalEntity, String edorg, EdOrgContextRightsCache edorgContextRights) {
        SLIPrincipal principal = new SLIPrincipal(user);
        principal.setId(user);
        principal.setName(fullName);
        principal.setRoles(roles);
        principal.setEntity(principalEntity);
        principal.setRealm(realmId);
        // Putting a default tenant
        principal.setTenantId(TENANT_ID);

        // Create the user session for security caching
        Entity session = repo.create("userSession", new HashMap<String, Object>());
        principal.setSessionId(session.getEntityId());


        Map<String, List<String>> edorgRoles = new HashMap<String, List<String>>();
        edorgRoles.put(edorg, roles);
        principal.setEdOrgRoles(edorgRoles);

        principal.setEdOrg(edorg);
        principal.setEdOrgId(edorg);
        principal.setEdOrgContextRights(edorgContextRights);

        principal.populateChildren(repo);

        return principal;
    }

    public void setRepo(Repository<Entity> repo) {
        this.repo = repo;
    }

}
