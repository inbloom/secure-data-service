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

package org.slc.sli.api.security.roles;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.security.resolve.impl.DefaultRolesToRightsResolver;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Tests default role to rights resolution pipeline
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class RolesToRightsTest {
    
    @Autowired
    @InjectMocks
    private DefaultRolesToRightsResolver resolver;
    
    @Mock
    private RoleRightAccess mockAccess;
    
    @Mock
    Repository<Entity> repo;
    
    private static final String DEFAULT_TENANT_ID = "huzzah";
    private static final String DEFAULT_REALM_ID = "dc=slidev,dc=net";
    private static final String ADMIN_REALM_ID = "adminRealmId";
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        Map<String, Object> adminRealmBody = new HashMap<String, Object>();
        adminRealmBody.put("admin", true);
        Entity adminRealmEntity = new MongoEntity("realm", adminRealmBody);
        
        Map<String, Object> userRealmBody = new HashMap<String, Object>();
        userRealmBody.put("admin", false);
        Entity userRealmEntity = new MongoEntity("realm", userRealmBody);
        
        when(repo.findById("realm", ADMIN_REALM_ID)).thenReturn(adminRealmEntity);
        when(repo.findById("realm", DEFAULT_REALM_ID)).thenReturn(userRealmEntity);
        
        when(
                mockAccess.findRoles(DEFAULT_TENANT_ID, DEFAULT_REALM_ID,
                        Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR, SecureRoleRightAccessImpl.AGGREGATOR)))
                .thenReturn(Arrays.asList(buildRole()));
        
        when(mockAccess.findRoles(DEFAULT_TENANT_ID, DEFAULT_REALM_ID, Arrays.asList("Pink", "Goo"))).thenReturn(
                new ArrayList<Role>());
        
        when(
                mockAccess.findRoles(DEFAULT_TENANT_ID, DEFAULT_REALM_ID, Arrays.asList(
                        SecureRoleRightAccessImpl.EDUCATOR, SecureRoleRightAccessImpl.AGGREGATOR, "bad", "doggie")))
                .thenReturn(Arrays.asList(buildRole()));
        
    }
    
    private Role buildRole() {
        return RoleBuilder.makeRole(SecureRoleRightAccessImpl.EDUCATOR).addRight(Right.AGGREGATE_READ).build();
    }
    
    @Test
    public void testMappedRoles() throws Exception {
        Set<GrantedAuthority> rights = resolver.resolveRoles(DEFAULT_TENANT_ID, DEFAULT_REALM_ID,
                Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR, SecureRoleRightAccessImpl.AGGREGATOR), false, false);
        Assert.assertTrue(rights.size() > 0);
    }
    
    @Test
    public void testBadRoles() throws Exception {
        Set<GrantedAuthority> authorities = resolver.resolveRoles(DEFAULT_TENANT_ID, DEFAULT_REALM_ID,
                Arrays.asList("Pink", "Goo"), false, false);
        Assert.assertTrue("Authorities must be empty", authorities.size() == 0);
    }
    
    @Test
    public void testMixedRoles() throws Exception {
        Set<GrantedAuthority> authorities = resolver.resolveRoles(DEFAULT_TENANT_ID, DEFAULT_REALM_ID, Arrays.asList(
                SecureRoleRightAccessImpl.EDUCATOR, SecureRoleRightAccessImpl.AGGREGATOR, "bad", "doggie"), false, false);
        Assert.assertTrue(authorities.size() > 0);
    }
}
