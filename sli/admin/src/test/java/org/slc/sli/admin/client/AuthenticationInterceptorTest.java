package org.slc.sli.admin.client;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.admin.test.bootstrap.WebContextTestExecutionListener;

/**
 * 
 * @author scole
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/commonCtx.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class AuthenticationInterceptorTest {
    
    private static final String NO_SESSION_JSON = "{\"authenticated\":false,\"redirect_user\":\"http://testapi1.slidev.org:8080/disco/realms/list.do\"}";
    private static final String VALID_SESSION_JSON = "{\"authenticated\":true,\"full_name\":\"Test User\"}";
    private AuthenticationInterceptor interceptor;
    private RESTClient rest;
    
    @Before
    public void init() {
        interceptor = new AuthenticationInterceptor();
        rest = Mockito.mock(RESTClient.class);
        interceptor.setRESTClient(rest);
    }
    
    @Test
    public void testNoSessionHaveCookie() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(new Cookie("iPlanetDirectoryPro", "1234567890"));
        
        JsonParser parser = new JsonParser();
        JsonObject jsonArray = parser.parse(VALID_SESSION_JSON).getAsJsonObject();
        Mockito.when(rest.sessionCheck("1234567890")).thenReturn(jsonArray);
        try {
            interceptor.preHandle(request, response, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Assert.assertEquals("SessionId was not set in session attributes", "1234567890", request.getSession()
                .getAttribute("ADMIN_SESSION_ID"));
        Assert.assertEquals("Full name does not match", "Test User", request.getSession().getAttribute("USER_NAME"));
    }
    
    @Test
    public void testHaveSession() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        request.getSession().setAttribute("ADMIN_SESSION_ID", "1234567890");
        
        
        try {
            interceptor.preHandle(request, response, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Assert.assertEquals("SessionId was not set in session attributes", "1234567890", request.getSession()
                .getAttribute("ADMIN_SESSION_ID"));
    }
    
    @Test
    public void testNoSessionNoCookie() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        JsonParser parser = new JsonParser();
        JsonObject jsonArray = parser.parse(NO_SESSION_JSON).getAsJsonObject();
        Mockito.when(rest.sessionCheck(null)).thenReturn(jsonArray);
        
        try {
            interceptor.preHandle(request, response, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String expectedUrl = "http://testapi1.slidev.org:8080/disco/realms/list.do?RelayState=http";
            
        Assert.assertTrue("Redirect url is not correct", response.getRedirectedUrl().startsWith(expectedUrl));
    }
}
