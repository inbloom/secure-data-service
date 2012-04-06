package org.slc.sli.api.resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test for support email
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml" })
@TestExecutionListeners({WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SupportResourceTest {

    @Autowired
    private SupportResource resource;

    @Autowired
    private SecurityContextInjector injector;

    @Value("${sli.support.email}")
    private String email;

    @Test
    public void testGetEmailFailure() throws Exception {
        assertNotNull(resource);
        SecurityContextHolder.clearContext();
        try {
            resource.getEmail();
            assertFalse(true);
        } catch (InsufficientAuthenticationException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetEmailPass() throws Exception {
        injector.setEducatorContext();
        Map<String, String> returned = (Map<String, String>) resource.getEmail();
        assertTrue(returned.get("email").equals(email));
    }

    public void setResource(SupportResource resource) {
        this.resource = resource;
    }
}
