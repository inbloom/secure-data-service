package org.slc.sli.api.security.roles;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.security.enums.Rights;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class RolesAndPermissionsResourceTest {

    @Autowired
    private RolesAndPermissionsResource api;

    private Map<String, Object> createTestRole() {
        Map<String, Object> role = new HashMap<String, Object>();
        List<String> permissions = new ArrayList<String>();
        permissions.add("Read");
        permissions.add("Delete");
        role.put("name", "Role1");
        role.put("permissions", permissions);
        return role;
    }
    
    private Map<String, Object> createTestPermissions() {
        Map<String, Object> perm = createTestRole();
        perm.put("name", Rights.READ_GENERAL.getRight());
        perm.put("permissions", null);
        return perm;
    }

    @Test
    public void testGetRolesAndPermissions() {
        //Create a role.
        Map<String, Object> role = createTestRole();
        assertNotNull(role);

        try {
            api.createRoleWithPermission((String) role.get("name"), role.get("permissions"));
        } catch (Exception e) {
            fail(e.toString());
        }

        //Fetch it back out.
//        Object result = api.getRolesAndPermissions();
//        assertNotNull(result);
    }
}
