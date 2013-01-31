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

package org.slc.sli.api.security.context.resolver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;

/**
 * Unit Tests
 *
 * @author pwolf
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class RealmHelperTest {

    @Autowired
    private RealmHelper helper;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private SecurityContextInjector injector;


    private Entity buildStaff(String name, Entity edOrg) {
        return buildStaff(name, edOrg, null);
    }

    private Entity buildStaff(String name, Entity edOrg1, Entity edOrg2) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("staffUniqueStateId", name);
        Entity staff = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", edOrg1.getEntityId());
        body.put("staffReference", staff.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        if (edOrg2 != null) {
            body = new HashMap<String, Object>();
            body.put("educationOrganizationReference", edOrg2.getEntityId());
            body.put("staffReference", staff.getEntityId());
            repo.create("staffEducationOrganizationAssociation", body);
        }
        return staff;
    }

    private Entity buildEdOrg(String stateOrgId, Entity parent, boolean isSEA) {
        return buildEdOrg(stateOrgId, parent, "foo", isSEA);
    }

    private Entity buildEdOrg(String stateOrgId, Entity parent, String tenantId, boolean isSEA) {
        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("tenantId", tenantId);
        Map<String, Object> body = new HashMap<String, Object>();
        body = new HashMap<String, Object>();
        if (isSEA) {
            body.put("organizationCategories", Arrays.asList("State Education Agency"));
        } else {
            body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        }
        body.put("stateOrganizationId", stateOrgId);

        if (parent != null) {
            body.put("parentEducationAgencyReference", parent.getEntityId());
        }
        Entity edorg = repo.create("educationOrganization", body, metaData, "educationOrganization");
        return edorg;
    }


    private Entity buildRealm(Entity edOrg) {
        return buildRealm(edOrg, "foo");
    }

    private Entity buildRealm(Entity edOrg, String tenantId) {
        Map<String, Object> metaData = new HashMap<String, Object>();

        Map<String, Object> body = new HashMap<String, Object>();
        body = new HashMap<String, Object>();
        TenantContext.setTenantId(tenantId);
        body.put("tenantId", tenantId);
        body.put("uniqueIdentifier", "BlahBlah");
        body.put("edOrg", edOrg.getBody().get("stateOrganizationId"));
        Entity realm = repo.create("realm", body, metaData, "realm");
        return realm;
    }

    @Before
    public void setup() {

        repo.deleteAll("realm", null);
        repo.deleteAll("educationOrganization", null);
        repo.deleteAll("staff", null);

    }

    @Test
    public void testLoginRealmIdsForSEA() {
        Entity sea = buildEdOrg("SEA1", null, true);
        Entity lea = buildEdOrg("LEA", sea, false);
        Entity seaStaff = buildStaff("SEA Staff", sea);
        Entity seaRealm = buildRealm(sea);
        Entity leaRealm = buildRealm(lea);
        assertTrue(helper.isUserAllowedLoginToRealm(seaStaff, seaRealm));
        assertFalse(helper.isUserAllowedLoginToRealm(seaStaff, leaRealm));
    }

    @Test
    public void testLoginRealmIdsForLEA() {
        Entity sea = buildEdOrg("SEA1", null, true);
        Entity lea = buildEdOrg("LEA", sea, false);
        Entity leaStaff = buildStaff("LEA Staff", lea);
        Entity seaRealm = buildRealm(sea);
        Entity leaRealm = buildRealm(lea);
        assertFalse(helper.isUserAllowedLoginToRealm(leaStaff, seaRealm));
        assertTrue(helper.isUserAllowedLoginToRealm(leaStaff, leaRealm));
    }



    @Test
    public void testSeaWithNoDirectRealm() {
        Entity sea = buildEdOrg("SEA1", null, true);
        Entity lea1 = buildEdOrg("LEA1", sea, false);
        Entity lea2 = buildEdOrg("LEA2", lea1, false);
        Entity lea1Realm = buildRealm(lea1);
        Entity lea2Realm = buildRealm(lea2);
        Entity seaStaff = buildStaff("SEA Staff", sea);
        assertTrue(helper.isUserAllowedLoginToRealm(seaStaff, lea1Realm));
        assertFalse(helper.isUserAllowedLoginToRealm(seaStaff, lea2Realm));
    }

    @Test
    public void testLeaWithNoDirectRealm() {
        Entity sea = buildEdOrg("SEA1", null, true);
        Entity lea1 = buildEdOrg("LEA1", sea, false);
        Entity lea2 = buildEdOrg("LEA2", lea1, false);
        Entity lea3 = buildEdOrg("LEA3", lea2, false);
        Entity lea1Realm = buildRealm(lea1);
        Entity lea3Realm = buildRealm(lea3);
        Entity seaRealm = buildRealm(sea);
        Entity leaStaff = buildStaff("LEA Staff", lea2);
        assertTrue(helper.isUserAllowedLoginToRealm(leaStaff, lea1Realm));
        assertFalse(helper.isUserAllowedLoginToRealm(leaStaff, lea3Realm));
        assertFalse(helper.isUserAllowedLoginToRealm(leaStaff, seaRealm));
    }

    @Test
    public void testSeaWithNoDirectRealmAndTwoAssociations() {
        Entity sea = buildEdOrg("SEA1", null, true);
        Entity lea1 = buildEdOrg("LEA1", sea, false);
        Entity lea2 = buildEdOrg("LEA2", lea1, false);
        Entity lea3 = buildEdOrg("LEA3", lea2, false);
        Entity lea1Realm = buildRealm(lea1);
        Entity lea3Realm = buildRealm(lea3);
        Entity seaStaff = buildStaff("SEA Staff", sea, lea2);
        assertTrue(helper.isUserAllowedLoginToRealm(seaStaff, lea1Realm));
        assertFalse(helper.isUserAllowedLoginToRealm(seaStaff, lea3Realm));
    }

    @Test
    public void testSeaWithNoDirectRealmAndTwoRealmsOnSameTier() {
        Entity sea = buildEdOrg("SEA1", null, true);
        Entity lea1 = buildEdOrg("LEA1", sea, false);
        Entity lea2 = buildEdOrg("LEA2", sea, false);
        Entity lea3 = buildEdOrg("LEA3", lea1, false);
        Entity lea1Realm = buildRealm(lea1);
        Entity lea2Realm = buildRealm(lea2);
        Entity lea3Realm = buildRealm(lea3);
        Entity seaStaff = buildStaff("SEA Staff", sea);
        assertTrue(helper.isUserAllowedLoginToRealm(seaStaff, lea1Realm));
        assertTrue(helper.isUserAllowedLoginToRealm(seaStaff, lea2Realm));
        assertFalse(helper.isUserAllowedLoginToRealm(seaStaff, lea3Realm));
    }

    @Test
    public void testGetAssociatedRealmIsTenantSpecific() {
        Entity sea = buildEdOrg("SEA1", null, injector.TENANT_ID, true);
        Entity lea1 = buildEdOrg("LEA1", sea, injector.TENANT_ID, false);
        Entity lea2 = buildEdOrg("LEA1", null, "Two", false);
        Entity lea2Realm = buildRealm(lea2, "Two");
        Entity lea1Realm = buildRealm(lea1, injector.TENANT_ID);

        Entity staff = buildStaff("LEA One", lea1);
        injector.setCustomContext("LEA", "LEA One", lea1Realm.getEntityId(),
                Arrays.asList("Realm Administrator"),
                staff, (String) lea1Realm.getBody().get("edOrg"));
        Set<String> realmIds = helper.getAssociatedRealmIds();
        assertTrue(realmIds != null);
        assertTrue(realmIds.contains(lea1Realm.getEntityId()));
    }

}
