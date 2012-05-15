package org.slc.sli.sandbox.idp.service;

import static org.junit.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Unit tests
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class LDAPUsersTest {
    
    @Autowired
    UserService userService;
    
    /**
     * Test SLIAdmin realm login
     * 
     * @throws AuthenticationException
     */
    //@Test
    public void testSLIAdmin() throws AuthenticationException {
        UserService.User user = userService.authenticate("SLIAdmin", "slcoperator", "slcoperator1234");
        assertEquals("slcoperator", user.userId);
        assertEquals("SLC Operator", user.getAttributes().get("userName"));
        assertEquals(1, user.getAttributes().size());
        assertEquals(1, user.getRoles().size());
        assertEquals("SLC Operator", user.getRoles().get(0));
    }
    
    /**
     * SANDBOX
     * Test sandbox developer logging in to SLIAdmin realm
     * 
     * @throws AuthenticationException
     */
    //@Test
    public void testDeveloperAdminLogin() throws AuthenticationException {
        UserService.User user = userService.authenticate("SLIAdmin", "testdeveloper", "testdeveloper1234");
        assertEquals("testdeveloper", user.userId);
        assertEquals("Test Developer", user.getAttributes().get("userName"));
        assertEquals("IL", user.getAttributes().get("Tenant"));
        assertEquals(1, user.getRoles().size());
        assertEquals("Application Developer", user.getRoles().get(0));
    }
    
    /**
     * SANDBOX
     * Test sandbox developer logging in to their realm
     * 
     * @throws AuthenticationException
     */
    //@Test
    public void testGoodDeveloper() throws AuthenticationException {
        UserService.User user = userService.authenticate("SLIAdmin", "testdeveloper", "testdeveloper1234");
        assertEquals("testdeveloper", user.userId);
        assertEquals("Test Developer", user.getAttributes().get("userName"));
        assertEquals("IL", user.getAttributes().get("Tenant"));
        assertEquals(1, user.getRoles().size());
        assertEquals("Application Developer", user.getRoles().get(0));
    }
}
