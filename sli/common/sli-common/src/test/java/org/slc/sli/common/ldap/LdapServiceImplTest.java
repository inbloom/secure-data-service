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

package org.slc.sli.common.ldap;

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
import java.util.Date;

import javax.naming.directory.SearchControls;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.common.ldap.Group;
import org.slc.sli.common.ldap.GroupContextMapper;
import org.slc.sli.common.ldap.LdapService;
import org.slc.sli.common.ldap.User;
import org.slc.sli.common.ldap.UserContextMapper;
import org.slc.sli.common.ldap.User.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class LdapServiceImplTest {

    @Autowired
    LdapService ldapService;

    private LdapTemplate ldapTemplate;
    private static String uid;
    private static User testUser;
    private static User slcUser;
    private static Group group;

    @Before
    public void init() throws UnknownHostException {
        testUser = buildTestUser();
        slcUser = buildSlcUser();
        uid = testUser.getUid();
        group = buildTestGroup();

        ldapTemplate = Mockito.mock(LdapTemplate.class);

        DistinguishedName dn = new DistinguishedName("ou=LocalNew");
        String[] attributes = new String[] {"*", LdapService.CREATE_TIMESTAMP, LdapService.MODIFY_TIMESTAMP };

        // mock: ldapTemplate.search(dn, filter.toString(), SearchControls.SUBTREE_SCOPE, new String[] {"*", CREATE_TIMESTAMP, MODIFY_TIMESTAMP }, new UserContextMapper())
        Mockito.when(ldapTemplate.search(
                Mockito.eq(dn),
                Mockito.eq("(&(objectclass=person)(uid=slcoperator))"),
                Mockito.eq(SearchControls.SUBTREE_SCOPE),
                Mockito.eq(attributes),
                Mockito.any(UserContextMapper.class))).thenReturn(Arrays.asList(slcUser));

        // mock: ldapTemplate.searchForObject(dn, filter.toString(), new GroupContextMapper());
        Mockito.when(ldapTemplate.searchForObject(
                Mockito.eq(dn),
                Mockito.eq("(&(objectclass=posixGroup)(cn=SLC Operator))"),
                Mockito.any(GroupContextMapper.class)))
            .thenReturn(group);

        // mock: ldapTemplate.search(dn, filter.toString(), new GroupContextMapper()
        Mockito.when(ldapTemplate.search(
                Mockito.eq(dn),
                Mockito.eq("(&(objectclass=posixGroup)(memberuid=slcoperator))"),
                Mockito.any(GroupContextMapper.class)))
            .thenReturn(Arrays.asList(group));

        ldapService.setLdapTemplate(ldapTemplate);
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

    // making live calls to an external ldap server is not appropriate for a junit
    @Test
    @Ignore
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

    private User buildSlcUser() {
        User user = new User();
        user.setFullName("SLC Operator");
        user.setUid("slcoperator");
        user.setEmail("slcoperator@slidev.org");
        user.setHomeDir("/dev/null");
        user.setCn("cn for slcoperator");
        user.setSn("sn for slcoperator");
        user.addGroup("SLC Operator");
        user.addGroup("SEA Administrator");
        user.addGroup("LEA Administrator");
        user.setCreateTime(new Date());
        user.setModifyTime(new Date());
        return user;
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

    private Group buildTestGroup() {
        Group group = new Group();
        group.setGroupName("SLC Operator");
        group.setMemberUids(Arrays.asList("slcoperator"));
        return group;
    }
}
