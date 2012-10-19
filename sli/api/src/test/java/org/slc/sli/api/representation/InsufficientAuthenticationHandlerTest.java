package org.slc.sli.api.representation;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
public class InsufficientAuthenticationHandlerTest {
    private InsufficientAuthenticationHandler handler;
    
    @Test
    public void checkResponse() {
        handler = new InsufficientAuthenticationHandler();
        Response resp = handler.toResponse(new InsufficientAuthenticationException("Invalid Token"));
        assertTrue(resp != null);
        Object entity = resp.getEntity();
        // No exception has been thrown.
        assertTrue(entity != null);
        
    }
}
