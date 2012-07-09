package org.slc.sli.api.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Test
    public void testGetUser() {
        User slcoperator = ldapService.getUser("SLIAdmin", "slcoperator");
        assertNotNull(slcoperator);
        assertTrue(slcoperator.getGroups().contains("SLC Operator"));
        assertTrue(slcoperator.getEmail().equals("slcoperator@slidev.org"));
        assertTrue(slcoperator.getUid().equals("slcoperator"));
        assertNotNull(slcoperator.getHomeDir());
        assertNotNull(slcoperator.getFirstName());
        assertNotNull(slcoperator.getLastName());
        assertNotNull(slcoperator.getFullName());
        assertNull(slcoperator.getTenant());
        assertNull(slcoperator.getEdorg());
        assertNotNull(slcoperator.getCreateTime());
        assertNotNull(slcoperator.getModifyTime());
    }

    @Test
    public void testGetGroup() {
        Group slcoperatorGroup = ldapService.getGroup("SLIAdmin", "SLC Operator");
        assertNotNull(slcoperatorGroup);
        assertEquals("SLC Operator", slcoperatorGroup.getGroupName());
        assertTrue(slcoperatorGroup.getMemberUids().contains("slcoperator"));
    }

    @Test
    public void testGetUserGroups() {

        List<Group> groups = ldapService.getUserGroups("SLIAdmin", "slcoperator");
        assertNotNull(groups);
        List<String> groupNames = new ArrayList<String>();
        for (Group group : groups) {
            groupNames.add(group.getGroupName());
        }
        assertTrue(groupNames.contains("SLC Operator"));

    }

    @Test
    public void testFindUserByGroups() {
        String[] groups = new String[] { "SEA Administrator" };
        List<User> users = ldapService.findUserByGroups("SLIAdmin", Arrays.asList(groups));
        assertNotNull(users);
    }

}
