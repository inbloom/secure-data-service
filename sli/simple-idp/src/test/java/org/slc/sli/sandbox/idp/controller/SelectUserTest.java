package org.slc.sli.sandbox.idp.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.sandbox.idp.service.AuthRequestService;
import org.slc.sli.sandbox.idp.service.AuthRequestService.Request;
import org.slc.sli.sandbox.idp.service.SamlAssertionService;
import org.slc.sli.sandbox.idp.service.SamlAssertionService.SamlAssertion;
import org.springframework.web.servlet.ModelAndView;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class SelectUserTest {
    
    @Mock
    AuthRequestService authRequestService;
    
    @Mock
    SamlAssertionService loginService;
    
    @InjectMocks
    SelectUser selectUser = new SelectUser();
    
    @Test
    public void testImpersonate() {
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRequestId()).thenReturn("req1234");
        Mockito.when(reqInfo.getRealm()).thenReturn("realm");
        Mockito.when(authRequestService.processRequest("SAMLRequest", "realm")).thenReturn(reqInfo);
        List<String> roles = new ArrayList<String>();
        roles.add("myrole");
        
        SamlAssertion samlResponse = new SamlAssertion("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.buildAssertion("userId", roles, null, reqInfo)).thenReturn(samlResponse);
        
        ModelAndView mov = selectUser.login("userId", roles, "SAMLRequest", "realm");
        assertEquals("SAMLResponse", ((SamlAssertion) mov.getModel().get("samlAssertion")).getSamlResponse());
        assertEquals("redirect_uri", ((SamlAssertion) mov.getModel().get("samlAssertion")).getRedirectUri());
        assertEquals("post", mov.getViewName());
    }
}
