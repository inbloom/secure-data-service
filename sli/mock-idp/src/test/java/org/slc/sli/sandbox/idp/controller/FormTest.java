package org.slc.sli.sandbox.idp.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.sandbox.idp.service.AuthRequests;
import org.slc.sli.sandbox.idp.service.AuthRequests.Request;
import org.slc.sli.sandbox.idp.service.Roles;
import org.slc.sli.sandbox.idp.service.Users;
import org.springframework.web.servlet.ModelAndView;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class FormTest {
    
    @Mock
    Roles roleService;
    @Mock
    Users userService;
    @Mock
    AuthRequests tenantService;
    
    @InjectMocks
    Form con = new Form();
    
    @Test
    public void testHome() throws IOException {
        
        Request request = Mockito.mock(AuthRequests.Request.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        
        Mockito.when(tenantService.processRequest("encodedSamlRequest", "tenant")).thenReturn(request);
        Mockito.when(request.getTenant()).thenReturn("tenant");
        
        ModelAndView mav = con.form("encodedSamlRequest", "tenant", session);
        assertEquals("form", mav.getViewName());
        Mockito.verify(roleService).getAvailableRoles();
        Mockito.verify(userService).getAvailableUsers("tenant");
        Mockito.verify(tenantService).processRequest("encodedSamlRequest", "tenant");
        Mockito.verify(session).setAttribute(Form.REQUEST_INFO, request);
        
        try {
            con.form(null, "tenant", session);
            fail("Expecting exception");
        } catch (RuntimeException e) {
            assertTrue(true);
        }
    }
}
