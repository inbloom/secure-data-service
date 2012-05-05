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
import org.slc.sli.sandbox.idp.service.AuthRequests;
import org.slc.sli.sandbox.idp.service.AuthRequests.Request;
import org.slc.sli.sandbox.idp.service.LoginService;
import org.slc.sli.sandbox.idp.service.LoginService.SamlResponse;
import org.springframework.web.servlet.ModelAndView;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class SelectUserTest {
    
    @Mock
    AuthRequests authRequestService;
    
    @Mock
    LoginService loginService;
    
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
        
        SamlResponse samlResponse = new SamlResponse("redirect_uri", "SAMLResponse");
        Mockito.when(loginService.login("userId", roles, null, reqInfo)).thenReturn(samlResponse);
        
        ModelAndView mov = selectUser.login("userId", roles, "SAMLRequest", "realm");
        assertEquals("SAMLResponse", ((SamlResponse) mov.getModel().get("samlResponse")).getSamlResponse());
        assertEquals("redirect_uri", ((SamlResponse) mov.getModel().get("samlResponse")).getRedirectUri());
        assertEquals("post", mov.getViewName());
    }
}
