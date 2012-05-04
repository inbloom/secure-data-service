package org.slc.sli.sandbox.idp.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class LDAPUsersTest {

    @Autowired
    Users userService;
    
    @Test
    public void getUserAuthentication() throws AuthenticationException {
        Users.User user = userService.authenticate("SLI", "unit", "unit1234");
        assertEquals("unit", user.userId);
        assertEquals("Unit Tester", user.name);
        assertEquals(1, user.getRoles().size());
        assertEquals("UnitTester", user.getRoles().get(0));
    }
 
}
