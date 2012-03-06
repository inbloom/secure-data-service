package org.slc.sli.api.security.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNull;

/**
 *
 * @author pwolf
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class SamlAuthenticationEntryPointTest {

    @Autowired
    SamlAuthenticationEntryPoint entryPoint;

    @Test
    public void testClientIdPassed() throws IOException, ServletException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameter("client_id")).thenReturn("ABC12345?");
        
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.doAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String redirect = (String) args[0];
                assertTrue("URL-encoded client_id contained in redirect " + redirect, 
                        redirect.indexOf("?clientId=ABC12345%3F") > -1);
                return redirect;
            }
        }).when(response).sendRedirect(Mockito.anyString());

        entryPoint.commence(request, response, null);
    }
    
    @Test //don't blow up
    public void testNoClientId() throws IOException, ServletException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameter("RealmName")).thenReturn("SLI?");

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Exception exception = null;
        try {
            entryPoint.commence(request, response, null);
        } catch (Exception e) {
            exception = e;
        }
        assertNull("We shouldn't have an exception: " + exception, exception);
    }


    @Test
    public void testRealmNamePassed() throws IOException, ServletException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameter("RealmName")).thenReturn("SLI?");

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.doAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String redirect = (String) args[0];
                assertTrue("URL-encoded RealmName contained in redirect " + redirect, 
                        redirect.indexOf("?RealmName=SLI%3F") > -1);
                return redirect;
            }
        }).when(response).sendRedirect(Mockito.anyString());

        entryPoint.commence(request, response, null);
    }
    
    @Test //don't blow up
    public void testNoRealm() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameter("client_id")).thenReturn("ABC12345?");

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Exception exception = null;
        try {
            entryPoint.commence(request, response, null);
        } catch (Exception e) {
            exception = e;
        }
        assertNull("We shouldn't have an exception: " + exception, exception);
    }
}
