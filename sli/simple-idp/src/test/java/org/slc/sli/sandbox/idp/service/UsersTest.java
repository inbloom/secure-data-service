package org.slc.sli.sandbox.idp.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.sandbox.idp.service.Users.GroupContextMapper;
import org.slc.sli.sandbox.idp.service.Users.User;
import org.springframework.ldap.core.AuthenticationErrorCallback;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class UsersTest {
    @Mock
    LdapTemplate ldapTemplate = null;
    
    @InjectMocks
    Users userService = new Users();
    
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testAuthenticate() throws AuthenticationException {
        Mockito.when(ldapTemplate.authenticate(Mockito.eq(DistinguishedName.EMPTY_PATH), Mockito.eq("(&(objectclass=person)(uid=testuser))"), Mockito.eq("testuser1234"), Mockito.any(AuthenticationErrorCallback.class))).thenReturn(true);
        User mockUser = new User();
        mockUser.name = "Test User";
        mockUser.userId = "testuser";
        Mockito.when(ldapTemplate.searchForObject(Mockito.eq(DistinguishedName.EMPTY_PATH), Mockito.eq("(&(objectclass=person)(uid=testuser))"), Mockito.any(ContextMapper.class))).thenReturn(mockUser);
        List<String> mockGroups = new ArrayList<String>();
        mockGroups.add("TestGroup1");
        mockGroups.add("TestGroup2");
        Mockito.when(ldapTemplate.search(Mockito.eq(DistinguishedName.EMPTY_PATH), Mockito.eq("(&(objectclass=posixGroup)(memberuid=testuser))"), Mockito.any(GroupContextMapper.class))).thenReturn(mockGroups);
        
        Users.User user = userService.authenticate("SLI", "testuser", "testuser1234");
        assertEquals("testuser", user.getUserId());
        assertEquals("Test User", user.getName());
        assertEquals(2, user.getRoles().size());
        assertEquals("TestGroup1", user.getRoles().get(0));
        assertEquals("TestGroup2", user.getRoles().get(1));
    }
    
}
