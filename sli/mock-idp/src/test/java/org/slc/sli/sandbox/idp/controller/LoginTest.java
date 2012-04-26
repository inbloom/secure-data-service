package org.slc.sli.sandbox.idp.controller;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.sandbox.idp.service.AuthRequests.Request;
import org.slc.sli.sandbox.idp.service.LoginService;
import org.slc.sli.sandbox.idp.service.Users;
import org.slc.sli.sandbox.idp.service.Users.User;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginTest {
    
    @Mock
    LoginService service;
    
    @Mock
    Users users;
    
    @InjectMocks
    Login loginController = new Login();
    
    @Test
    public void testLogin() {
        List<String> roles = Arrays.asList("role1", "role2");
        
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserId()).thenReturn("abc");
        //Mockito.when(users.getUser("tenant", "abc")).thenReturn(user);
        
        Request reqInfo = Mockito.mock(Request.class);
        Mockito.when(reqInfo.getRequestId()).thenReturn("req1234");
        Mockito.when(reqInfo.getTenant()).thenReturn("tenant");
        
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(session.getAttribute(Login.REQUEST_INFO)).thenReturn(reqInfo);
        
        Mockito.when(service.login(user, roles, reqInfo)).thenReturn(URI.create("redirect"));
        
        //View view = loginController.login("abc", roles, session);
        //assertEquals(RedirectView.class, view.getClass());
        //assertEquals("redirect", ((RedirectView) view).getUrl());
        
        //Mockito.verify(users).getUser("tenant", "abc");
        //Mockito.verify(service).login(user, roles, reqInfo);
    }
}
