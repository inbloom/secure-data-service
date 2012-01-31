package org.slc.sli.api.resources.security;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Simple test for ClientRoleManagerResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class ClientRoleManagerResourceTest {
    @Autowired
    private ClientRoleManagerResource resource;

    @Autowired
    private SecurityContextInjector injector;
    
    private EntityService service;
    private EntityBody mapping;

    @Before
    public void setUp() throws Exception {

        injector.setAdminContext();
        List<String> ids = new ArrayList<String>();
        ids.add("1234");
        ids.add("-1");
        
        mapping = new EntityBody();
        mapping.put("id", "123567324");
        mapping.put("realm_name", "Waffles");
        
        List<EntityBody> maps = new ArrayList<EntityBody>();
        maps.add(mapping);

        service = mock(EntityService.class);

        resource.setService(service);

        when(service.update("-1", mapping)).thenReturn(true);
        when(service.update("1234", mapping)).thenReturn(true);
        when(service.list(0, 100)).thenReturn(ids);
        when(service.get(ids)).thenReturn(maps);
    }

    @After
    public void tearDown() throws Exception {
        service = null;
    }

    @Test
    public void testAddClientRole() throws Exception {
        try {
            resource.addClientRole("Peanuts", "-1", null);
            assertFalse(false);
        } catch (EntityNotFoundException e)
        {
            assertTrue(true);
        }
        assertTrue(resource.addClientRole("Waffles", "1234", mapping));
    }

    @Test
    public void testGetMappings() throws Exception {
        assertNotNull(resource.getMappings("Waffles"));
        assertNull(resource.getMappings("Invalid"));
    }
}
