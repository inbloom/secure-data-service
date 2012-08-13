package org.slc.sli.api.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.ldap.User.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LdapServiceImplTest {

    @Autowired
    LdapService ldapService;

    private static String uid;
    private static User testUser;

    @Before
    public void init() throws UnknownHostException {
        testUser = buildTestUser();
        uid = testUser.getUid();
        ldapService.removeUser("LocalNew", uid);
    }

    @After
    public void clear() {
        ldapService.removeUser("LocalNew", uid);
    }

    @Test
    public void testGetUser() {
        User slcoperator = ldapService.getUser("LocalNew", "slcoperator");
        assertNotNull(slcoperator);
        assertTrue(slcoperator.getGroups().contains("SLC Operator"));
        assertTrue(slcoperator.getEmail().equals("slcoperator@slidev.org"));
        assertTrue(slcoperator.getUid().equals("slcoperator"));
        assertNotNull(slcoperator.getHomeDir());
        assertNotNull(slcoperator.getGivenName());
        assertNotNull(slcoperator.getSn());
        assertNotNull(slcoperator.getFullName());
        assertNull(slcoperator.getTenant());
        assertNull(slcoperator.getEdorg());
        assertNotNull(slcoperator.getCreateTime());
        assertNotNull(slcoperator.getModifyTime());
    }

    @Test
    public void testGetGroup() {
        Group slcoperatorGroup = ldapService.getGroup("LocalNew", "SLC Operator");
        assertNotNull(slcoperatorGroup);
        assertEquals("SLC Operator", slcoperatorGroup.getGroupName());
        assertTrue(slcoperatorGroup.getMemberUids().contains("slcoperator"));
    }

    @Test
    public void testGetUserGroups() {

        Collection<Group> groups = ldapService.getUserGroups("LocalNew", "slcoperator");
        assertNotNull(groups);
        Collection<String> groupNames = new ArrayList<String>();
        for (Group group : groups) {
            groupNames.add(group.getGroupName());
        }
        assertTrue(groupNames.contains("SLC Operator"));

    }

    @Test
    public void testFindUserByGroups() {
        String[] groups = new String[] { "SEA Administrator" };
        Collection<User> users = ldapService.findUsersByGroups("LocalNew", Arrays.asList(groups));
        assertNotNull(users);
    }

    @Test
    public void testCRUDUser() throws UnknownHostException, NameAlreadyBoundException {

        // test create
        String newUserUid = ldapService.createUser("LocalNew", testUser);
        User newUser = ldapService.getUser("LocalNew", newUserUid);
        assertNotNull(newUser);
        assertNotNull(newUser.getGroups());
        assertTrue(newUser.getGroups().contains("SLC Operator"));
        assertTrue(newUser.getGroups().contains("SEA Administrator"));
        assertTrue(newUser.getGroups().contains("LEA Administrator"));
        assertEquals(uid, newUser.getUid());
        assertEquals("testemail@slidev.org", newUser.getEmail());
        assertEquals("testFirst", newUser.getGivenName());
        assertEquals("testLast", newUser.getSn());
        assertEquals("/dev/null", newUser.getHomeDir());
        assertEquals("testTenant", newUser.getTenant());
        assertEquals("testEdorg", newUser.getEdorg());
        assertEquals(User.Status.SUBMITTED, newUser.getStatus());

        // test update
        updateTestUser(newUser);
        ldapService.updateUser("LocalNew", newUser);
        User updatedUser = ldapService.getUser("LocalNew", newUserUid);
        assertNotNull(updatedUser);
        assertNotNull(updatedUser.getGroups());
        assertFalse(updatedUser.getGroups().contains("SLC Operator"));
        assertFalse(updatedUser.getGroups().contains("SEA Administrator"));
        assertTrue(updatedUser.getGroups().contains("LEA Administrator"));
        assertTrue(updatedUser.getGroups().contains("Realm Administrator"));
        assertEquals(uid, updatedUser.getUid());
        assertEquals("testemailupdate@slidev.org", updatedUser.getEmail());
        assertEquals("testFirstUpdate", updatedUser.getGivenName());
        assertEquals("testLastUpdate", updatedUser.getSn());
        assertEquals("/dev/null/update", updatedUser.getHomeDir());
        assertEquals("testTenantUpdate", updatedUser.getTenant());
        assertEquals("testEdorgUpdate", updatedUser.getEdorg());
        assertEquals(Status.APPROVED, updatedUser.getStatus());

        // test delete
        ldapService.removeUser("LocalNew", uid);
        User testUser = ldapService.getUser("LocalNew", uid);
        assertNull(testUser);

    }

    private User buildTestUser() throws UnknownHostException {
        User testUser = new User();
        testUser.setFullName("testFirst testLast");
        testUser.setUid("testUid_" + InetAddress.getLocalHost().getHostName());
        testUser.setEdorg("testEdorg");
        testUser.setTenant("testTenant");
        testUser.setEmail("testemail@slidev.org");
        testUser.setHomeDir("/dev/null");
        testUser.addGroup("SLC Operator");
        testUser.addGroup("SEA Administrator");
        testUser.addGroup("LEA Administrator");
        return testUser;
    }

    private void updateTestUser(User user) {
        user.setEdorg("testEdorgUpdate");
        user.setTenant("testTenantUpdate");
        user.setEmail("testemailupdate@slidev.org");
        user.setHomeDir("/dev/null/update");
        user.setFullName("testFirstUpdate testLastUpdate");
        user.removeGroup("SLC Operator");
        user.removeGroup("SEA Administrator");
        user.addGroup("Realm Administrator");
        user.setStatus(User.Status.APPROVED);
    }

}
