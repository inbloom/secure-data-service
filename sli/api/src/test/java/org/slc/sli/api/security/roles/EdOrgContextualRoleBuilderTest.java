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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.*;

/**
 * User: npandey ablum
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class EdOrgContextualRoleBuilderTest {

    @Autowired
    @InjectMocks
    private EdOrgContextualRoleBuilder edOrgRoleBuilder;

    private List<String> samlRoles;

    @Mock
    private EdOrgHelper edorgHelper;

    @Mock
    private RolesToRightsResolver resolver;



    private String tenant = "Midgar";
    private String realmId = "123_id";
    private String staffId = "staff123";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        samlRoles = new ArrayList<String>();
        samlRoles.add("Educator");
        samlRoles.add("IT Admin");
        samlRoles.add("Principal");
        samlRoles.add("terminator");
        samlRoles.add("nobody");
    }

    @Test
    public void testBuildEdOrgContextualRoles () {
        Set<Entity> seoas = new HashSet<Entity>();
        seoas.add(createSEOA("LEA1", "IT Admin"));
        seoas.add(createSEOA("LEA1", "Educator"));

        seoas.add(createSEOA("LEA2", "Educator"));
        seoas.add(createSEOA("LEA2", "Educator"));

        Set<Role> edOrg1RolesSet = new HashSet<Role>();
        edOrg1RolesSet.add(createRole("Educator"));
        edOrg1RolesSet.add(createRole("IT Admin"));
        Mockito.when(resolver.mapRoles(tenant, realmId, Arrays.asList("Educator", "IT Admin"), false)).thenReturn(edOrg1RolesSet);
        Mockito.when(resolver.mapRoles(tenant, realmId, Arrays.asList("IT Admin", "Educator"), false)).thenReturn(edOrg1RolesSet);

        Set<Role> edOrg2RolesSet = new HashSet<Role>();
        edOrg2RolesSet.add(createRole("Educator"));
        Mockito.when(resolver.mapRoles(tenant, realmId, Arrays.asList("Educator"), false)).thenReturn(edOrg2RolesSet);

        Mockito.when(edorgHelper.locateNonExpiredSEOAs(staffId)).thenReturn(seoas);
        Map<String, List<String>> edOrgRoles = edOrgRoleBuilder.buildValidStaffRoles(realmId, staffId,tenant, samlRoles);

        Assert.assertNotNull(edOrgRoles);
        Assert.assertEquals(2,edOrgRoles.size());

        List<String> edOrg1Roles = edOrgRoles.get("LEA1");
        Assert.assertNotNull(edOrg1Roles);
        Assert.assertEquals(2, edOrg1Roles.size());
        Assert.assertTrue(edOrg1Roles.contains("IT Admin"));
        Assert.assertTrue(edOrg1Roles.contains("Educator"));

        List<String> edOrg2Roles = edOrgRoles.get("LEA2");
        Assert.assertNotNull(edOrg2Roles);
        Assert.assertEquals(1, edOrg2Roles.size());
        Assert.assertTrue(edOrg2Roles.contains("Educator"));

    }

    @Test
    (expected = AccessDeniedException.class)
    public void testNoCustomRoleMatch () {
        Set<Entity> seoas = new HashSet<Entity>();
        seoas.add(createSEOA("LEA1", "IT Admin"));
        seoas.add(createSEOA("LEA1", "Educator"));

        Set<Role> edOrg1RolesSet = new HashSet<Role>();

        Mockito.when(resolver.mapRoles(tenant, realmId, Arrays.asList("Educator", "IT Admin"), false)).thenReturn(edOrg1RolesSet);

        Mockito.when(edorgHelper.locateNonExpiredSEOAs(staffId)).thenReturn(seoas);
        Map<String, List<String>> edOrgRoles = edOrgRoleBuilder.buildValidStaffRoles(realmId, staffId,tenant, samlRoles);

        Assert.assertNotNull(edOrgRoles);
        Assert.assertEquals(0,edOrgRoles.size());

        //List<String> edOrg1Roles = edOrgRoles.get("LEA1");
        //Assert.assertNotNull(edOrg1Roles);
        //Assert.assertEquals(0, edOrg1Roles.size());

    }

    @Test
    (expected = AccessDeniedException.class)
    public void testInvalidEdOrgRoles() {
        Mockito.when(edorgHelper.locateNonExpiredSEOAs(Mockito.anyString())).thenReturn(new HashSet<Entity>());

        Map<String, List<String>> edOrgRoles = edOrgRoleBuilder.buildValidStaffRoles(realmId, staffId, tenant, samlRoles);

        Assert.assertNotNull(edOrgRoles);
        Assert.assertEquals(0,edOrgRoles.size());
    }

    @Test
    (expected = AccessDeniedException.class)
    public void noMatchingRoles() {
        Set<Entity> seoas = new HashSet<Entity>();
        seoas.add(createSEOA("LEA1", "Random Role1"));
        seoas.add(createSEOA("LEA1", "Random Role2"));

        seoas.add(createSEOA("LEA2", "Random Role2"));

        Mockito.when(edorgHelper.locateNonExpiredSEOAs(Mockito.anyString())).thenReturn(seoas);

        Map<String, List<String>> edOrgRoles = edOrgRoleBuilder.buildValidStaffRoles(realmId, staffId, tenant, samlRoles);

        Assert.assertTrue(edOrgRoles.isEmpty());
    }

    @Test
    (expected = AccessDeniedException.class)
    public void testNoSamlRoles() {
        Set<Entity> seoas = new HashSet<Entity>();
        seoas.add(createSEOA("LEA1", "IT Admin"));
        seoas.add(createSEOA("LEA1", "Educator"));
        seoas.add(createSEOA("LEA2", "Educator"));

        Mockito.when(edorgHelper.locateNonExpiredSEOAs(Mockito.anyString())).thenReturn(seoas);

        Map<String, List<String>> edOrgRoles = edOrgRoleBuilder.buildValidStaffRoles(realmId, staffId, tenant, new ArrayList<String>());

        Assert.assertTrue(edOrgRoles.isEmpty());

    }

    private Entity createSEOA(String edorgId, String staffClassification) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.STAFF_EDORG_ASSOC_STAFF_CLASSIFICATION, staffClassification);
        body.put(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, edorgId);
        body.put(ParameterConstants.STAFF_REFERENCE, "staff1");

        return new MongoEntity(EntityNames.STAFF_ED_ORG_ASSOCIATION, "0", body, null);

    }

    private Role createRole(String roleName) {
        RoleBuilder rb = RoleBuilder.makeRole(Arrays.asList(roleName));
        return rb.build();
    }
}
