package org.slc.sli.api.security.context.resolver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class RealmHelperTest {
    
    @Autowired
    public RealmHelper helper;
    
    @Autowired
    private Repository<Entity> repo;
    
    
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
        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("tenantId", "foo");
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
        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("tenantId", "foo");
        Map<String, Object> body = new HashMap<String, Object>();
        body = new HashMap<String, Object>();
        body.put("uniqueIdentifier", "BlahBlah");
        body.put("edOrg", edOrg.getBody().get("stateOrganizationId"));
        Entity realm = repo.create("realm", body, metaData, "realm");
        return realm;
    }
    
    @Before
    public void setup() {

        repo.deleteAll("realm");
        repo.deleteAll("educationOrganization");
        repo.deleteAll("staff");
        
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

}
