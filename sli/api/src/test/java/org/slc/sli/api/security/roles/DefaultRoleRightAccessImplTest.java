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


package org.slc.sli.api.security.roles;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.resolve.impl.DefaultRolesToRightsResolver;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Set of tests for the basic RoleRightsAccessImpl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class DefaultRoleRightAccessImplTest {

    Entity realm = null;
    
    @Autowired
    private DefaultRolesToRightsResolver resolver;

    @Autowired
    private SecurityContextInjector securityContextInjector;
    
    @Autowired
    private Repository<Entity> repo;
    
    @Autowired
    private RoleInitializer roleInit;

    @Before
    public void setUp() throws Exception {
        securityContextInjector.setAdminContext();
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("tenantId", "MyTenant");
        realm = repo.create("realm", body);
        
        body = new HashMap<String, Object>();
        List<Map<String, Object>> groups = roleInit.getDefaultRoles();
        body.put("realmId", realm.getEntityId());
        body.put("roles", groups);
        body.put("customRights", new ArrayList<String>());
        repo.create("customRole", body, metaData, "customRole");

    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testResolveEducator() {
        List<Map<String, Object>> groups = roleInit.getDefaultRoles();
        List<String> expectedRights = null;
        for (Map<String, Object> group : groups) {
            List<String> names = (List<String>) group.get("names");
            if (names.contains("Educator")) {
                expectedRights = (List<String>) group.get("rights");
            }
        }
        
        assertNotNull("Need default rights for Educator role", expectedRights);
        Set<GrantedAuthority> roles = resolver.resolveRoles("MyTenant", realm.getEntityId(), Arrays.asList("Educator"), false, false);
        for (GrantedAuthority auth : roles) {
            assertTrue("Looking for " + auth.toString(), expectedRights.contains(auth.toString()));
        }
        
    }

  
}
