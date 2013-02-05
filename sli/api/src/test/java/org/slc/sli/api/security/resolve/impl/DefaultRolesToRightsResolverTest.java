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


package org.slc.sli.api.security.resolve.impl;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.security.roles.Role;
import org.slc.sli.api.security.roles.RoleRightAccess;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.Repository;
import org.springframework.stereotype.Component;

/**
 * Tests roles to rights resolver
 *
 * @author ycao
 *
 */
@Component
public class DefaultRolesToRightsResolverTest {

    @InjectMocks
    DefaultRolesToRightsResolver resolver = new DefaultRolesToRightsResolver();
    
    @Mock
    Repository<Entity> mockRepo;
    
    @Mock
    RoleRightAccess mockRoleRightAccessor;
    
    SecureRoleRightAccessImpl defaultRoles = new SecureRoleRightAccessImpl();
   
    private static final String DEVELOPER_REALM_ID = "DeveloperRealmId";
    private static final String ADMIN_REALM_ID = "adminRealmId";
    private List<String> sandboxRole = Arrays.asList(SecureRoleRightAccessImpl.SANDBOX_ADMINISTRATOR);
    private List<String> otherRole = Arrays.asList(SecureRoleRightAccessImpl.APP_DEVELOPER, SecureRoleRightAccessImpl.INGESTION_USER);
    private List<String> appAndProdLoginUser = Arrays.asList(SecureRoleRightAccessImpl.APP_DEVELOPER);
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        defaultRoles.init();
        Mockito.when(mockRepo.findById("realm", DEVELOPER_REALM_ID)).thenReturn(getDeveloperRealm());
        Mockito.when(mockRepo.findById("realm", ADMIN_REALM_ID)).thenReturn(getAdminRealm());
        Mockito.when(mockRoleRightAccessor.findAdminRoles(sandboxRole)).thenReturn(defaultRoles.findAdminRoles(sandboxRole));
        Mockito.when(mockRoleRightAccessor.findAdminRoles(appAndProdLoginUser)).thenReturn(defaultRoles.findAdminRoles(appAndProdLoginUser));
    }
    
    @Test
    public void sandboxAdminBecomeDeveloperInDevRealm() {
        Set<Role> roles = resolver.mapRoles(null, DEVELOPER_REALM_ID, sandboxRole, false);
        assertTrue("sandbox admin is not mapped to developer in developer realm", 
                    roles.containsAll(defaultRoles.findAdminRoles(appAndProdLoginUser)));
        assertTrue("sandbox admin is not only mapped to developer in developer realm", 
                    defaultRoles.findAdminRoles(appAndProdLoginUser).containsAll(roles));
    }
    
    @Test
    public void sandboxAdminstaysSandboxAdminInAdminRealm() {
        Set<Role> roles = resolver.mapRoles(null, ADMIN_REALM_ID, sandboxRole, true);
        assertTrue("sandbox admin is changed in admin realm", 
                    roles.containsAll(defaultRoles.findAdminRoles(Arrays.asList(SecureRoleRightAccessImpl.SANDBOX_ADMINISTRATOR))));
        assertTrue("sandbox admin is only mapped to sandbox admin in admin realm", 
                    defaultRoles.findAdminRoles(Arrays.asList(SecureRoleRightAccessImpl.SANDBOX_ADMINISTRATOR)).containsAll(roles));
    }
    
    @Test
    public void roleWithoutProdLoginIsChangedToEmptyGroupInDevRealm() {
        Set<Role> roles = resolver.mapRoles(null, DEVELOPER_REALM_ID, otherRole, false);
        assertTrue("other admin is not mapped to developer in developer realm", roles.isEmpty());
    }
    
    private Entity getDeveloperRealm() {
        MongoEntity devRealm = new MongoEntity("realm", getBody()); 
        devRealm.getBody().put("developer", true);
        return devRealm;
    }
    
    private Entity getAdminRealm() {
        MongoEntity adminRealm = new MongoEntity("realm", getBody()); 
        adminRealm.getBody().put("admin", true);
        return adminRealm;
    }
   
    private Map<String, Object> getBody() {
        Map<String, Object> body = new HashMap<String, Object>();
        //really nothing need to be built at this moment
        return body;
    }
}
