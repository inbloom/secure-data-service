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
