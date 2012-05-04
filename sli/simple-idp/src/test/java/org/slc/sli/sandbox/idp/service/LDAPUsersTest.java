package org.slc.sli.sandbox.idp.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit tests
 */
 @RunWith(SpringJUnit4ClassRunner.class)
 @ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class LDAPUsersTest {
    
    @Autowired
    UserService userService;
    
     @Test
    public void getUserAuthentication() throws AuthenticationException {
        UserService.User user = userService.authenticate("DevTest", "unit", "unit1234");
        assertEquals("unit", user.userId);
        assertEquals("Unit Tester", user.getAttributes().get("userName"));
        assertEquals(1, user.getRoles().size());
        assertEquals("UnitTester", user.getRoles().get(0));
    }
    
}
