package org.slc.sli.api.resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.resources.SupportResource;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test for support email
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SupportResourceTest {
    
    @Autowired
    private SupportResource resource;
    
    @Value("${sli.support.email}")
    private String email;
    
    @Test
    public void testGetEmail() throws Exception {
        assertNotNull(resource);
        Map<String, String> returned = (Map<String, String>) resource.getEmail();
        assertTrue(returned.get("email").equals(email));
    }

    public void setResource(SupportResource resource) {
        this.resource = resource;
    }
}
